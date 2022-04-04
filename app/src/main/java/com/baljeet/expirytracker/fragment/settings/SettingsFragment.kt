package com.baljeet.expirytracker.fragment.settings

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.databinding.FragmentSettingsBinding
import com.baljeet.expirytracker.util.Constants
import com.baljeet.expirytracker.util.SharedPref
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

class SettingsFragment : Fragment() {

    private lateinit var bind: FragmentSettingsBinding

    private lateinit var adLoader : AdLoader


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        SharedPref.init(requireContext())
        bind = FragmentSettingsBinding.inflate(inflater, container, false)
        if(SharedPref.isUserAPro){
            bind.adLayout.isGone = true
            bind.settingIllustration.isGone = false
        }   else{
            adLoader = AdLoader.Builder(requireContext(), Constants.NATIVE_INLINE_AD_ID)
                .forNativeAd { ad : NativeAd ->
                    // Show the ad.
                    val adView =  layoutInflater.inflate(R.layout.native_ad_view_layout,container, false) as NativeAdView

                    populateAdVIew(ad,adView)
                    bind.settingIllustration.isGone = true
                    bind.adLayout.isGone = false
                    bind.adLayout.removeAllViews()
                    bind.adLayout.addView(adView)
                    if(activity?.isDestroyed == true){
                        ad.destroy()
                        return@forNativeAd
                    }
                }.withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        // Handle the failure by logging, altering the UI, and so on.
                        bind.adLayout.isGone = true
                        bind.settingIllustration.isGone = false
                        Log.d("Log for - Ad Failure ","$adError")
                    }
                }).build()
            adLoader.loadAd(AdRequest.Builder().build())
        }
        return bind.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

            proTextview.text = if(SharedPref.isUserAPro) getString(R.string.proud_pro_member) else getString(R.string.become_pro)

            qnaTextview.setOnClickListener {
                Navigation.findNavController(requireView()).navigate(SettingsFragmentDirections.actionSettingsFragmentToFAQ())
            }
            proTextview.setOnClickListener {
                Navigation.findNavController(requireView()).navigate(SettingsFragmentDirections.actionSettingsFragmentToBePro())
            }
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
            notificationsTextview.setOnClickListener {
                Navigation.findNavController(requireView()).navigate(SettingsFragmentDirections.actionSettingsFragmentToManageNotifications())
            }

        }
    }


    private fun populateAdVIew(nativeAd: NativeAd, adView: NativeAdView) {
        // Set the media view.
        // Set the media view.
        adView.mediaView = adView.findViewById(R.id.ad_media)

        // Set other ad assets.

        // Set other ad assets.
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.priceView = adView.findViewById(R.id.ad_price)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        adView.storeView = adView.findViewById(R.id.ad_store)
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

        // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.

        // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
        (adView.headlineView as TextView).text = nativeAd.headline


        nativeAd.mediaContent?.let {
            adView.mediaView?.setMediaContent(it)
        }

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.body == null) {
            adView.bodyView?.visibility = View.INVISIBLE
        } else {
            adView.bodyView?.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }

        if (nativeAd.callToAction == null) {
            adView.callToActionView?.visibility = View.INVISIBLE
        } else {
            adView.callToActionView?.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }

        if (nativeAd.icon == null) {
            adView.iconView?.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon?.drawable
            )
            adView.iconView?.visibility = View.VISIBLE
        }

        if (nativeAd.price == null) {
            adView.priceView?.visibility = View.INVISIBLE
        } else {
            adView.priceView?.visibility = View.VISIBLE
            (adView.priceView as TextView).text = nativeAd.price
        }

        if (nativeAd.store == null) {
            adView.storeView?.visibility = View.INVISIBLE
        } else {
            adView.storeView?.visibility = View.VISIBLE
            (adView.storeView as TextView).text = nativeAd.store
        }

        if (nativeAd.starRating == null) {
            adView.starRatingView?.visibility = View.INVISIBLE
        } else {
            (adView.starRatingView as RatingBar).rating = nativeAd.starRating?.toFloat()?:0F
            adView.starRatingView?.visibility = View.VISIBLE
        }

        if (nativeAd.advertiser == null) {
            adView.advertiserView?.visibility = View.INVISIBLE
        } else {
            (adView.advertiserView as TextView).text = nativeAd.advertiser
            adView.advertiserView?.visibility = View.VISIBLE
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd)
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