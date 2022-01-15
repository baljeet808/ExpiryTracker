package com.baljeet.expirytracker.fragment.settings

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.baljeet.expirytracker.databinding.FragmentSettingsBinding
import com.baljeet.expirytracker.util.SharedPref

class SettingsFragment : Fragment() {

    private lateinit var bind: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        SharedPref.init(requireContext())
        bind = FragmentSettingsBinding.inflate(inflater, container, false)

        bind.lightModeButton.translationX = 0f
        bind.lightModeButton.alpha = 0f
        bind.nightModeButton.translationX = 0f
        bind.nightModeButton.alpha = 0f
        if(SharedPref.isNightModeOn){
            bind.lightModeButton.visibility = View.VISIBLE
            showMoon()
        }else{
            bind.nightModeButton.visibility = View.VISIBLE
            showSun()
        }

        bind.nightModeButton.setOnClickListener {
            hideSun()
            Handler(Looper.getMainLooper()).postDelayed({
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                SharedPref.isNightModeOn = true
            },1000)
        }
        bind.lightModeButton.setOnClickListener {
            hideMoon()
            Handler(Looper.getMainLooper()).postDelayed({
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            SharedPref.isNightModeOn = false
            },1000)
        }

        bind.apply {
            donateTextview.setOnClickListener {
                Navigation.findNavController(requireView()).navigate(SettingsFragmentDirections.actionSettingsFragmentToDonateFragment())
            }
            feedbackTextview.setOnClickListener {
                Navigation.findNavController(requireView()).navigate(SettingsFragmentDirections.actionSettingsFragmentToReviewsFragment())
            }
            widgetsTextview.setOnClickListener {
                Navigation.findNavController(requireView()).navigate(SettingsFragmentDirections.actionSettingsFragmentToWidgetMenu())
            }
            personalizationTextview.setOnClickListener {
                Navigation.findNavController(requireView()).navigate(SettingsFragmentDirections.actionSettingsFragmentToPersonalize())
            }
            categoriesTextview.setOnClickListener {
                Navigation.findNavController(requireView()).navigate(SettingsFragmentDirections.actionSettingsFragmentToManageCategories())
            }
            productsTextview.setOnClickListener {
                Navigation.findNavController(requireView()).navigate(SettingsFragmentDirections.actionSettingsFragmentToManageProducts())
            }
        }
        return bind.root
    }

    private fun showMoon(){
        bind.lightModeButton.apply {
            animate().translationX(300F).alpha(1f).setDuration(1000).
                    withEndAction {
                        translationX = 300f
                        alpha = 1f
                    }
        }
    }
    private fun hideSun(){
        bind.nightModeButton.apply {
            animate().translationX(0F).alpha(0f).setDuration(1000).
            withEndAction {
                translationX = 0f
                alpha = 0F
                showMoon()
            }
        }
    }
    private fun showSun(){
        bind.nightModeButton.apply {
            animate().translationX(-300F).alpha(1f).setDuration(1000).
            withEndAction {
                translationX = -300f
                alpha = 1f
            }
        }
    }
    private fun hideMoon(){
        bind.lightModeButton.apply {
            animate().translationX(0F).alpha(0f).setDuration(1000).
            withEndAction {
                translationX = 0f
                alpha = 0F
                showSun()
            }
        }
    }


}