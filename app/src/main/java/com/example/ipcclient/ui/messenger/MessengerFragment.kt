package com.example.ipcclient.ui.messenger

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ipcclient.*
import com.example.ipcclient.databinding.FragmentMessengerBinding

// reference article
// https://proandroiddev.com/ipc-techniques-for-android-messenger-3e8555a32167

class MessengerFragment : Fragment(), ServiceConnection, View.OnClickListener {

    private var _binding: FragmentMessengerBinding? = null
    private val binding get() = _binding!!

    // Is the client process bound to the service of the remote process?
    private var isBound: Boolean = false

    // Define two Messenger objects.
    //clientMessenger: to tell the server “use me when replying to my message”
    //serverMessenger: to send a message to the server

    // Messenger on the client
    private var clientMessenger: Messenger? = null

    // Messenger on the server
    private var serverMessenger: Messenger? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Log.i("MessengerFragment", "IPCClient PID: ${Process.myPid()}")

        _binding = FragmentMessengerBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnConnect.setOnClickListener(this)
    }

    // Define a handler to handle incoming messages. Update the graphical interface here.
    // Handle messages from the remote service
    private var handler: Handler = object : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            // Update the UI with remote process info
            val bundle = msg.data
            binding.linearLayoutClientInfo.visibility = View.VISIBLE
            binding.btnConnect.text = getString(R.string.disconnect)
            binding.txtServerPid.text = bundle.getInt(PID).toString()
            binding.txtServerConnectionCount.text = bundle.getInt(CONNECTION_COUNT).toString()
        }
    }

    override fun onClick(v: View?) {
        if(isBound){
            doUnbindService() // Unbind when disconnect button is clicked
        } else {
            doBindService() // Bind to the server when the connect button is clicked
        }
    }

    // If a connection is established with the service after calling the bindService() command, we receive the callback onServiceConnected.
    private fun doBindService() {
        clientMessenger = Messenger(handler)
        Intent("messengerExample").also { intent ->
            intent.`package` = "com.example.ipcserver"
            val logIfSuccessful = activity?.applicationContext?.bindService(intent, this, Context.BIND_AUTO_CREATE) //If the service has not yet been created while we're binding, we add the BIND_AUTO_CREATE flag for automatic creation, so if it hasn't been created yet, it automatically will be for us.
            Log.i("MessengerFragment","bindService: $logIfSuccessful")
        }
        isBound = true
    }

    private fun doUnbindService() {
        if(isBound){
            activity?.applicationContext?.unbindService(this)
        }
        clearUI()
        isBound = false
    }

    // If a connection is established with the service after calling the bindService() command, we receive the callback onServiceConnected.
    // Send the message to the server when we receive the callback.
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        Log.i("MessengerFragment", "onServiceConnected() called")
        serverMessenger = Messenger(service)
        // Ready to send messages to remote service
        sendMessageToServer()
    }

    // Declare the object for which we expect responses by saying message.replyTo = clientMessenger
    private fun sendMessageToServer() {
        if(!isBound) return
        val message = Message.obtain(handler)
        val bundle = Bundle()
        bundle.putString(DATA, binding.editClientData.text.toString())
        bundle.putString(PACKAGE_NAME, context?.packageName)
        bundle.putInt(PID, Process.myPid())
        message.data = bundle
        message.replyTo = clientMessenger // we offer our Messenger object for the two-way communication to be established

        try {
            serverMessenger?.send(message)
        } catch (e: RemoteException) {
            e.printStackTrace()
        } finally {
            message.recycle()
        }
    }

    // If the service connection is lost then let’s remove the information on the GUI (this is called when the OS kills our bound service, or if we manually closed the service server app)
    override fun onServiceDisconnected(name: ComponentName?) {
        Log.i("MessengerFragment", "onServiceDisconnected() called")
        serverMessenger = null
        clearUI()
    }

    private fun clearUI() {
        binding.txtServerPid.text = ""
        binding.txtServerConnectionCount.text = ""
        binding.btnConnect.text = getString(R.string.connect)
        binding.linearLayoutClientInfo.visibility = View.INVISIBLE
    }

    override fun onDestroyView() {
        doUnbindService()
        _binding = null
        super.onDestroyView()
    }

}