package com.baljeet.expirytracker.fragment.shared

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.databinding.FragmentTrackerDetailsBinding

class TrackerDetails : Fragment() {

    private lateinit var bind : FragmentTrackerDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentTrackerDetailsBinding.inflate(inflater, container, false)
        bind.apply {

        }
        return bind.root
    }

}