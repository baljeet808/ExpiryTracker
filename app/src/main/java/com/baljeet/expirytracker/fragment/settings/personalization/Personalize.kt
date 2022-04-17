package com.baljeet.expirytracker.fragment.settings.personalization

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.databinding.FragmentPersonalizeBinding
import com.baljeet.expirytracker.util.Constants
import com.baljeet.expirytracker.util.SharedPref
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback


enum class SelectedTheme {
    BLUE, BLACK, WHITE, PINK, PEACH, YELLOW, GREEN, PURPLE, TEAL
}

class Personalize : Fragment() {

    private var mRewardedAd: RewardedAd? = null

    private lateinit var bind: FragmentPersonalizeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SharedPref.init(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentPersonalizeBinding.inflate(inflater, container, false)

        adLoad()


        bind.apply {
            when (SharedPref.themeName) {
                Constants.PURPLE -> {
                    purpleThemeCardBtn.isChecked = true
                }
                Constants.BLACK -> {
                    blackThemeCardBtn.isChecked = true
                }
                Constants.WHITE -> {
                    whiteThemeCardBtn.isChecked = true
                }
                Constants.BLUE -> {
                    blueThemeCardBtn.isChecked = true
                }
                Constants.PEACH -> {
                    peachThemeCardBtn.isChecked = true
                }
                Constants.GREEN -> {
                    greenThemeCardBtn.isChecked = true
                }
                Constants.YELLOW -> {
                    yellowThemeCardBtn.isChecked = true
                }
                Constants.PINK -> {
                    pinkThemeCardBtn.isChecked = true
                }
                Constants.TEAL -> {
                    tealThemeCardBtn.isChecked = true
                }
            }

            backButton.setOnClickListener { activity?.onBackPressed() }

            blueThemeCardBtn.setOnClickListener {
                unCheckAllColorCards()
                blueThemeCardBtn.isChecked = true
                if (SharedPref.isUserAPro.not()) {
                    showRewardAD(SelectedTheme.BLUE)
                } else {
                    setTheme(SelectedTheme.BLUE)
                }
            }
            peachThemeCardBtn.setOnClickListener {
                unCheckAllColorCards()
                peachThemeCardBtn.isChecked = true
                if (SharedPref.isUserAPro.not()) {
                    showRewardAD(SelectedTheme.PEACH)
                } else {
                    setTheme(SelectedTheme.PEACH)
                }
            }
            blackThemeCardBtn.setOnClickListener {
                unCheckAllColorCards()
                blackThemeCardBtn.isChecked = true
                if (SharedPref.isUserAPro.not()) {
                    showRewardAD(SelectedTheme.BLACK)
                } else {
                    setTheme(SelectedTheme.BLACK)
                }
            }
            whiteThemeCardBtn.setOnClickListener {
                unCheckAllColorCards()
                whiteThemeCardBtn.isChecked = true
                if (SharedPref.isUserAPro.not()) {
                    showRewardAD(SelectedTheme.WHITE)
                } else {
                    setTheme(SelectedTheme.WHITE)
                }
            }
            pinkThemeCardBtn.setOnClickListener {
                unCheckAllColorCards()
                pinkThemeCardBtn.isChecked = true
                if (SharedPref.isUserAPro.not()) {
                    showRewardAD(SelectedTheme.PINK)
                } else {
                    setTheme(SelectedTheme.PINK)
                }
            }
            greenThemeCardBtn.setOnClickListener {
                unCheckAllColorCards()
                greenThemeCardBtn.isChecked = true
                if (SharedPref.isUserAPro.not()) {
                    showRewardAD(SelectedTheme.GREEN)
                } else {
                    setTheme(SelectedTheme.GREEN)
                }
            }
            yellowThemeCardBtn.setOnClickListener {
                unCheckAllColorCards()
                yellowThemeCardBtn.isChecked = true
                if (SharedPref.isUserAPro.not()) {
                    showRewardAD(SelectedTheme.YELLOW)
                } else {
                    setTheme(SelectedTheme.YELLOW)
                }
            }
            purpleThemeCardBtn.setOnClickListener {
                unCheckAllColorCards()
                purpleThemeCardBtn.isChecked = true
                if (SharedPref.isUserAPro.not()) {
                    showRewardAD(SelectedTheme.PURPLE)
                } else {
                    setTheme(SelectedTheme.PURPLE)
                }
            }
            tealThemeCardBtn.setOnClickListener {
                unCheckAllColorCards()
                tealThemeCardBtn.isChecked = true
                if (SharedPref.isUserAPro.not()) {
                    showRewardAD(SelectedTheme.TEAL)
                } else {
                    setTheme(SelectedTheme.TEAL)
                }
            }
            darkModeToggle.isChecked = SharedPref.isNightModeOn
            darkModeToggle.text = if (SharedPref.isNightModeOn) {
                getString(R.string.dark_mode_on)
            } else {
                getString(R.string.dark_mode_off)
            }

            darkModeToggle.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    buttonView.text = getString(R.string.dark_mode_on)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    SharedPref.isNightModeOn = true
                } else {
                    buttonView.text = getString(R.string.notifications_off)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    SharedPref.isNightModeOn = false
                }
            }
        }
        return bind.root
    }

    private fun adLoad() {
        if (SharedPref.isUserAPro.not()) {
            val adRequest = AdRequest.Builder().build()
            RewardedAd.load(requireContext(),
                Constants.REWARDED_AD_ID, adRequest, object : RewardedAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        mRewardedAd = null
                    }

                    override fun onAdLoaded(rewardedAd: RewardedAd) {
                        mRewardedAd = rewardedAd
                    }
                })
            mRewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    mRewardedAd = null
                }
            }
        } else {
            bind.apply {
                adMessage.isGone = true
                adImage.isGone = true
            }
        }

    }

    private fun setTheme(theme: SelectedTheme) {
        when (theme) {
            SelectedTheme.BLACK -> {
                SharedPref.themeName = Constants.BLACK
                activity?.setTheme(R.style.BlackTheme)
            }
            SelectedTheme.WHITE -> {
                SharedPref.themeName = Constants.WHITE
                activity?.setTheme(R.style.WhiteTheme)
            }
            SelectedTheme.GREEN -> {
                SharedPref.themeName = Constants.GREEN
                activity?.setTheme(R.style.GreenTheme)
            }
            SelectedTheme.BLUE -> {
                SharedPref.themeName = Constants.BLUE
                activity?.setTheme(R.style.Theme_ExpiryTracker)
            }
            SelectedTheme.YELLOW -> {
                SharedPref.themeName = Constants.YELLOW
                activity?.setTheme(R.style.YellowTheme)
            }
            SelectedTheme.PURPLE -> {
                SharedPref.themeName = Constants.PURPLE
                activity?.setTheme(R.style.PurpleTheme)
            }
            SelectedTheme.PINK -> {
                SharedPref.themeName = Constants.PINK
                activity?.setTheme(R.style.PinkTheme)
            }
            SelectedTheme.PEACH -> {
                SharedPref.themeName = Constants.PEACH
                activity?.setTheme(R.style.PeachTheme)
            }
            SelectedTheme.TEAL -> {
                SharedPref.themeName = Constants.TEAL
                activity?.setTheme(R.style.TealTheme)
            }
        }
        activity?.recreate()
    }


    private fun showRewardAD(theme: SelectedTheme) {
        if (mRewardedAd != null) {
            mRewardedAd?.show(requireActivity()) {
                setTheme(theme)
            }
        } else {
            setTheme(theme)
            adLoad()
        }
    }

    private fun unCheckAllColorCards() {
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