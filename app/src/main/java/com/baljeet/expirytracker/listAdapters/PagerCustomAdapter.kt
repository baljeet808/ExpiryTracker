package com.baljeet.expirytracker.listAdapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class PagerCustomAdapter(fm : FragmentManager, behavior: Int): FragmentPagerAdapter(fm,behavior) {

    var fragmentList  = ArrayList<Fragment>()
    var fragTitleList = ArrayList<String>()

    override fun getCount(): Int {
        return  fragmentList.size
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

   /* override fun getPageTitle(position: Int): CharSequence {
        return fragTitleList[position]
    }*/

    fun addFragment(fragment : Fragment, title : String){
        fragmentList.add(fragment)
        fragTitleList.add(title)
    }
}