package com.example.ipcclient.ui.broadcast

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.os.Process
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ipcclient.DATA
import com.example.ipcclient.PACKAGE_NAME
import com.example.ipcclient.PID
import com.example.ipcclient.R
import com.example.ipcclient.databinding.FragmentBroadcastBinding
import java.util.*

// reference article
// https://proandroiddev.com/ipc-techniques-for-android-broadcast-ee4ed1f56261

// Broadcasts can be sent by the system, or by applications.
// And other applications can register to listen to those specific broadcasts with BroadcastReceiver.
// Our client application will send the broadcasts with intents, and our server application will receive them with intents using BroadcastReceiver.
// In other words, we're initiating one-way communication channel. With broadcasts there is no two-way communication.
// We will not be able to receive the information from the server application, we will just send a broadcast containing the client’s information. We will show the broadcast time on the screen.
class BroadcastFragment : Fragment(), View.OnClickListener {

    /**Receivers, by default, are exported and can receive broadcast sent by any other application.
     *For external receivers, a simple security layer can be created by defining permissions/action filters
     *in the <receiver> tag in the Manifest. Broadcasts that may come from applications that do not have
     * the necessary permission/action are prevented from being received this way.

    In this example, we only want to send a broadcast to a specific application(s), so we will use explicit intent
    by specifying the package name and class name. Explicit receivers are exempt from the restrictions set for receivers
    defined in Manifest for API level 26. Receivers defined in the manifest are saved while the application is loaded.
    Then the receiver becomes another entry point for the application. By default in this implementation, the FLAG_INCLUDE_STOPPED_PACKAGES flag is set to true.

    In other words, if this broadcast arrives while the server application is not running, the system will launch the server application.
    Just like in AIDL and Messenger technique, the bindService call is launching the service/application.*/

    private var _binding: FragmentBroadcastBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        _binding = FragmentBroadcastBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnConnect.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        sendBroadcast()
        showBroadcastTime()
    }

    private fun sendBroadcast() {
        val intent = Intent()
        intent.action = "com.example.ipcclient"
        intent.putExtra(PACKAGE_NAME, context?.packageName)
        intent.putExtra(PID, Process.myPid().toString())
        intent.putExtra(DATA, binding.editClientData.text.toString())
        intent.component =
            ComponentName("com.example.ipcserver", "com.example.ipcserver.IPCBroadcastReceiver")

        activity?.applicationContext?.sendBroadcast(intent)
    }

    private fun showBroadcastTime() {
        val cal = Calendar.getInstance()
        val time = "${cal.get(Calendar.HOUR)}:${cal.get(Calendar.MINUTE)}:${cal.get(Calendar.SECOND)}"
        binding.linearLayoutClientInfo.visibility = View.VISIBLE
        binding.txtDate.text = time
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}