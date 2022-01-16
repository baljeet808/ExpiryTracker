package com.baljeet.expirytracker.fragment.settings.widgets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.baljeet.expirytracker.data.viewmodels.TrackerViewModel
import com.baljeet.expirytracker.databinding.FragmentWidgetMenuBinding
import com.baljeet.expirytracker.listAdapters.TrackerDemoAdapter


class WidgetMenu : Fragment() {

    private lateinit var bind : FragmentWidgetMenuBinding
    private val viewModel : TrackerViewModel by viewModels()
    private lateinit var demoAdapter : TrackerDemoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind =  FragmentWidgetMenuBinding.inflate(inflater, container, false)
        bind.apply {
            backButton.setOnClickListener { activity?.onBackPressed() }
            trackersStackView.layoutManager = LinearLayoutManager(requireContext())
            demoAdapter = TrackerDemoAdapter(requireContext())
            trackersStackView.adapter = demoAdapter
        }
        viewModel.getActiveTrackersLive().observe(viewLifecycleOwner,{
            demoAdapter.submitList(it)
        })

        return bind.root
    }
}