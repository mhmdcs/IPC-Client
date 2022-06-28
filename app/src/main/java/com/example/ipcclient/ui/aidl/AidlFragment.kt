package com.example.ipcclient.ui.aidl

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.Process
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.ipcclient.R
import com.example.ipcclient.databinding.FragmentAidlBinding
import com.example.ipcserver.AIDLInterface

// reference article
// https://proandroiddev.com/ipc-techniques-for-android-aidl-bb03ed62adaa

// Listen for connection situations by implementing the ServiceConnection interface.
// IMPORTANT NOTE: Don't forget to declare the service used in the manifest!
class AidlFragment : Fragment(), ServiceConnection, View.OnClickListener {

    private lateinit var binding: FragmentAidlBinding
    var remoteService: AIDLInterface? = null
    private var connected = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAidlBinding.inflate(layoutInflater, container, false)
        Log.i("AidlFragment", "IPCClient PID: ${Process.myPid()}")


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnConnect.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        connected =
            if(connected){
                disconnectedToRemoteService()
                binding.txtServerPid.text = ""
                binding.txtServerConnectionCount.text = ""
                binding.btnConnect.text = getString(R.string.connect)
                binding.linearLayoutClientInfo.visibility = View.INVISIBLE
                false
            } else {
                connectedToRemoteService()
                binding.btnConnect.text = getString(R.string.disconnect)
                binding.linearLayoutClientInfo.visibility = View.VISIBLE
                true
            }
    }

    private fun connectedToRemoteService() {
        val serviceIntent = Intent("aidlExample")
        val pack = AIDLInterface::class.java.`package`

        //if pack is not null, then:
        pack?.let{
            serviceIntent.setPackage(pack.name)
           val logIfSuccessful = activity?.applicationContext?.bindService(serviceIntent, this, Context.BIND_AUTO_CREATE) //If the service has not yet been created while we're binding, we add the BIND_AUTO_CREATE flag for automatic creation, so if it hasn't been created yet, it automatically  will be for us.
        Log.i("AidlFragment","bindService: $logIfSuccessful")
        }
    }

    private fun disconnectedToRemoteService() {
        if(connected){
            //When the operating system needs memory, it can kill normal background services in applications that have not user interaction for a while.
            //But in Bound Services, as long as there is at least *one* bound client to the service, background services are not killed, except in extreme cases. But in order to not waste resources, let’s not stay bound to the service even though we can since this is a practice app, call unbindService()
            activity?.applicationContext?.unbindService(this)
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        Log.i("AidlFragment", "onServiceConnected() called")

        //get an instance of our AIDL Interface by casting AIDLInterface.Stub as an Interface and feeding it the service, and store that it in remoteService
        remoteService = AIDLInterface.Stub.asInterface(service)

        binding.txtServerPid.text = remoteService?.pid.toString()
        binding.txtServerConnectionCount.text = remoteService?.connectionCount.toString()

        remoteService?.setDisplayedValue(
            context?.packageName,
            Process.myPid(),
            binding.editClientData.text.toString()
        )

        connected = true

    }

    //(this is called when the OS kills our bound service, or if we manually closed the service server app)
    override fun onServiceDisconnected(name: ComponentName?) {
        Log.i("AidlFragment", "onServiceDisconnected() called")

        Toast.makeText(context, "IPC server has disconnected unexpectedly", Toast.LENGTH_SHORT).show()
        remoteService = null
        connected = false
    }

}