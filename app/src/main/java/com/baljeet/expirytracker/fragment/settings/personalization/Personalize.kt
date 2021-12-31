package com.baljeet.expirytracker.fragment.settings.personalization

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.databinding.FragmentPersonalizeBinding
import com.baljeet.expirytracker.util.Constants
import com.baljeet.expirytracker.util.SharedPref


class Personalize : Fragment() {

    private lateinit var bind : FragmentPersonalizeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SharedPref.init(requireContext())
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentPersonalizeBinding.inflate(inflater, container, false)
        bind.apply {
            blueThemeCardBtn.setOnClickListener {
                unCheckAllColorCards()
                blueThemeCardBtn.isChecked = true
                SharedPref.themeName = Constants.BLUE
                activity?.setTheme(R.style.Theme_ExpiryTracker)
            }
            peachThemeCardBtn.setOnClickListener {
                unCheckAllColorCards()
                peachThemeCardBtn.isChecked = true
                SharedPref.themeName = Constants.PEACH
                activity?.setTheme(R.style.PeachTheme)
            }
            blackThemeCardBtn.setOnClickListener {
                unCheckAllColorCards()
                blackThemeCardBtn.isChecked = true
                SharedPref.themeName = Constants.BLACK
                activity?.setTheme(R.style.BlackTheme)
            }
            whiteThemeCardBtn.setOnClickListener {
                unCheckAllColorCards()
                whiteThemeCardBtn.isChecked = true
                SharedPref.themeName = Constants.WHITE
                activity?.setTheme(R.style.WhiteTheme)
            }
            pinkThemeCardBtn.setOnClickListener {
                unCheckAllColorCards()
                pinkThemeCardBtn.isChecked = true
                SharedPref.themeName = Constants.PINK
                activity?.setTheme(R.style.PinkTheme)
            }
            greenThemeCardBtn.setOnClickListener {
                unCheckAllColorCards()
                greenThemeCardBtn.isChecked = true
                SharedPref.themeName = Constants.GREEN
                activity?.setTheme(R.style.GreenTheme)
            }
            yellowThemeCardBtn.setOnClickListener {
                unCheckAllColorCards()
                yellowThemeCardBtn.isChecked = true
                SharedPref.themeName = Constants.YELLOW
                activity?.setTheme(R.style.YellowTheme)
            }
            purpleThemeCardBtn.setOnClickListener {
                unCheckAllColorCards()
                purpleThemeCardBtn.isChecked = true
                SharedPref.themeName = Constants.PURPLE
                activity?.setTheme(R.style.PurpleTheme)
            }
            tealThemeCardBtn.setOnClickListener {
                unCheckAllColorCards()
                tealThemeCardBtn.isChecked = true
                SharedPref.themeName = Constants.TEAL
                activity?.setTheme(R.style.PeachTheme)
            }
        }
        return bind.root
    }


    fun unCheckAllColorCards(){
        bind.apply {
            blueThemeCardBtn.isChecked = false
            peachThemeCardBtn.isChecked = false
            blackThemeCardBtn.isChecked = false
            whiteThemeCardBtn.isChecked = false
            pinkThemeCardBtn.isChecked = false
            greenThemeCardBtn.isChecked = false
            yellowThemeCardBtn.isChecked = false
            purpleThemeCardBtn.isChecked = false
            tealThemeCardBtn.isChecked = false
        }
    }
}