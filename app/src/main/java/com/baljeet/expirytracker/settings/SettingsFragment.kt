package com.baljeet.expirytracker.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.util.SharedPref
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingsFragment : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        SharedPref.init(requireContext())
       val view =  inflater.inflate(R.layout.fragment_settings, container, false)
    view.findViewById<TextView>(R.id.settings_label).setOnClickListener {
        if(SharedPref.isNightModeOn){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            SharedPref.isNightModeOn = false
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            SharedPref.isNightModeOn = true
        }
    }
        return view
    }


}