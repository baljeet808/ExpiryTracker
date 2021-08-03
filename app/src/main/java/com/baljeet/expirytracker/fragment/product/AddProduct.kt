package com.baljeet.expirytracker.fragment.product

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.fragment.shared.SelectFrom
import com.baljeet.expirytracker.fragment.shared.SelectFromViewModel
import com.baljeet.expirytracker.listAdapters.PagerCustomAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class AddProduct : Fragment() {

    private lateinit var viewPager : ViewPager
    private lateinit var tabLayout : TabLayout

    private val viewModel : SelectFromViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_product, container, false)

        view.findViewById<ImageButton>(R.id.close_btn).setOnClickListener { activity?.onBackPressed() }


        viewPager = view.findViewById(R.id.tabs_host)
        tabLayout = view.findViewById(R.id.tab_layout)
        setUpPager()

        return view
    }

    fun setUpPager(){
        val selectCategory = SelectFrom.newInstance("Category")

        tabLayout.setupWithViewPager(viewPager)

        val pagerAdapter = PagerCustomAdapter(childFragmentManager,0)
        pagerAdapter.addFragment(selectCategory,"Category")

        viewPager.adapter = pagerAdapter
        viewPager.currentItem =0
    }

}