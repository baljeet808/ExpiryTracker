package com.baljeet.expirytracker.Dash

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import com.baljeet.expirytracker.R
import com.google.android.material.bottomnavigation.BottomNavigationView


class DashFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dash, container, false)

        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.VISIBLE

        view.findViewById<TextView>(R.id.add_product_button).setOnClickListener { Navigation.findNavController(requireView()).navigate(R.id.action_dashFragment_to_addProduct) }

        return view
    }

}