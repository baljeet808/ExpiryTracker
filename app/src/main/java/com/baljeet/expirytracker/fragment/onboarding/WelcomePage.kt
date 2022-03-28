package com.baljeet.expirytracker.fragment.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.databinding.FragmentWelcomePageBinding

class WelcomePage : Fragment() {


    private lateinit var bind : FragmentWelcomePageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentWelcomePageBinding.inflate(inflater, container, false)

        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.apply {

        }
    }

}