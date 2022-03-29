package com.baljeet.expirytracker.fragment.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.databinding.FragmentWatchReportsBinding


class WatchReports : Fragment() {

    private lateinit var bind : FragmentWatchReportsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentWatchReportsBinding.inflate(inflater, container, false)
        return bind.root
    }

}