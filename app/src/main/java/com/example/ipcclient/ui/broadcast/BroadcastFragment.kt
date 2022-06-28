package com.example.ipcclient.ui.broadcast

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ipcclient.R

// reference article
// https://proandroiddev.com/ipc-techniques-for-android-broadcast-ee4ed1f56261

class BroadcastFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_broadcast, container, false)
    }

}