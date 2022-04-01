package com.baljeet.expirytracker.fragment.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.baljeet.expirytracker.databinding.FragmentWelcomePageBinding
import com.baljeet.expirytracker.listAdapters.ViewPagerAdapter

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
            bind.apply {
                val fragmentList = arrayListOf<Fragment>(
                    ImpressiveTrackers(),
                    UseWidgets(),
                    WatchReports(),
                    SaveMoney()
                )

                val mAdapter = ViewPagerAdapter(
                    fragmentList,
                    activity?.supportFragmentManager!!,
                    lifecycle
                )
                bind.tabDots.apply {
                    addTab(newTab())
                    addTab(newTab())
                    addTab(newTab())
                    addTab(newTab())
                }
                bind.fragmentContainer.apply {
                    adapter = mAdapter
                    isUserInputEnabled = true
                    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                        override fun onPageSelected(position: Int) {
                            tabDots.selectTab(bind.tabDots.getTabAt(position))
                        }
                    })
                }
            }
        }
    }

}