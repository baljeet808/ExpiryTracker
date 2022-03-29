package com.baljeet.expirytracker.fragment.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.baljeet.expirytracker.OnBoarding
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.util.SharedPref


class SaveMoney : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_save_money, container, false)
        view.apply {
            SharedPref.init(requireContext())
            val nextBtn = findViewById<Button>(R.id.next_btn)
            nextBtn.setOnClickListener {
                SharedPref.hasOnboarded = true
                (activity as OnBoarding).moveToMainActivity()
            }
        }
        return view
    }

}