package com.baljeet.expirytracker.fragment.shared

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.baljeet.expirytracker.R


class SelectFrom : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_select_from, container, false)

        view.findViewById<ImageButton>(R.id.close_btn).setOnClickListener { activity?.onBackPressed() }


        return view
    }

}