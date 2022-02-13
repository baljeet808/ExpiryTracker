package com.baljeet.expirytracker.fragment.settings.advancesettings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.baljeet.expirytracker.databinding.FragmentAdvanceSettingsBinding


class AdvanceSettings : Fragment() {

    private lateinit var bind: FragmentAdvanceSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentAdvanceSettingsBinding.inflate(inflater,container,false)
        bind.apply {



            
        }
        return bind.root
    }
}