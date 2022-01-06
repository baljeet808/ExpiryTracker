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
        when(SharedPref.themeName){
            Constants.PURPLE->{
               purpleThemeCardBtn.isChecked = true
            }
            Constants.BLACK->{
                blackThemeCardBtn.isChecked = true
            }
            Constants.WHITE->{
                whiteThemeCardBtn.isChecked = true
            }
            Constants.BLUE->{
                blueThemeCardBtn.isChecked = true
            }
            Constants.PEACH->{
                peachThemeCardBtn.isChecked = true
            }
            Constants.GREEN->{
                greenThemeCardBtn.isChecked = true
            }
            Constants.YELLOW->{
                yellowThemeCardBtn.isChecked = true
            }
            Constants.PINK->{
                pinkThemeCardBtn.isChecked = true
            }
        }

             backButton.setOnClickListener { activity?.onBackPressed() }
            blueThemeCardBtn.setOnClickListener {
                unCheckAllColorCards()
                blueThemeCardBtn.isChecked = true
                SharedPref.themeName = Constants.BLUE
                activity?.setTheme(R.style.Theme_ExpiryTracker)
                activity?.recreate()
            }
            peachThemeCardBtn.setOnClickListener {
                unCheckAllColorCards()
                peachThemeCardBtn.isChecked = true
                SharedPref.themeName = Constants.PEACH
                activity?.setTheme(R.style.PeachTheme)
                activity?.recreate()
            }
            blackThemeCardBtn.setOnClickListener {
                unCheckAllColorCards()
                blackThemeCardBtn.isChecked = true
                SharedPref.themeName = Constants.BLACK
                activity?.setTheme(R.style.BlackTheme)
                activity?.recreate()
            }
            whiteThemeCardBtn.setOnClickListener {
                unCheckAllColorCards()
                whiteThemeCardBtn.isChecked = true
                SharedPref.themeName = Constants.WHITE
                activity?.setTheme(R.style.WhiteTheme)
                activity?.recreate()
            }
            pinkThemeCardBtn.setOnClickListener {
                unCheckAllColorCards()
                pinkThemeCardBtn.isChecked = true
                SharedPref.themeName = Constants.PINK
                activity?.setTheme(R.style.PinkTheme)
                activity?.recreate()
            }
            greenThemeCardBtn.setOnClickListener {
                unCheckAllColorCards()
                greenThemeCardBtn.isChecked = true
                SharedPref.themeName = Constants.GREEN
                activity?.setTheme(R.style.GreenTheme)
                activity?.recreate()
            }
            yellowThemeCardBtn.setOnClickListener {
                unCheckAllColorCards()
                yellowThemeCardBtn.isChecked = true
                SharedPref.themeName = Constants.YELLOW
                activity?.setTheme(R.style.YellowTheme)
                activity?.recreate()
            }
            purpleThemeCardBtn.setOnClickListener {
                unCheckAllColorCards()
                purpleThemeCardBtn.isChecked = true
                SharedPref.themeName = Constants.PURPLE
                activity?.setTheme(R.style.PurpleTheme)
                activity?.recreate()
            }
            tealThemeCardBtn.setOnClickListener {
                unCheckAllColorCards()
                tealThemeCardBtn.isChecked = true
                SharedPref.themeName = Constants.TEAL
                activity?.setTheme(R.style.PeachTheme)
                activity?.recreate()
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