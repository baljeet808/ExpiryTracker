package com.baljeet.expirytracker.product

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.baljeet.expirytracker.R

class AddProduct : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_product, container, false)

        view.findViewById<ImageButton>(R.id.close_btn).setOnClickListener { activity?.onBackPressed() }

        return view
    }

}