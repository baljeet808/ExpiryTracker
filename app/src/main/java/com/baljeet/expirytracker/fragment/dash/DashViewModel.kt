package com.baljeet.expirytracker.fragment.dash

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.baljeet.expirytracker.CustomApplication
import com.baljeet.expirytracker.util.Constants
import com.baljeet.expirytracker.util.SharedPref
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class DashViewModel(app: Application): AndroidViewModel(app) {
    val context = getApplication<CustomApplication>()

    var countForAd = MutableLiveData(0)
    var i=0

    var mInterstitialAd = MediatorLiveData<InterstitialAd?>().apply {
        addSource(countForAd) {
            if (it % 3 == 0 && it !=  0) {
                if(!SharedPref.isUserAPro) {
                    loadAdForAddTracker()
                }
            }
        }
    }

    fun incCount(){
        countForAd.postValue(i++)
    }



    private fun loadAdForAddTracker() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            Constants.DASH_INTERSTITIAL_AD_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    mInterstitialAd.postValue(null)
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    mInterstitialAd.postValue(ad)
                }
            })
    }


}