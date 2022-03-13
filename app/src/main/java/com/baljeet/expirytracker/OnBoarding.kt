package com.baljeet.expirytracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.baljeet.expirytracker.util.Constants
import com.baljeet.expirytracker.util.SharedPref
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener

class OnBoarding : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        SharedPref.init(this)

        MobileAds.initialize(applicationContext)

        when(SharedPref.themeName){
            Constants.BLUE->{
                setTheme(R.style.Theme_ExpiryTracker)
            }
            Constants.BLACK->{
                setTheme(R.style.BlackTheme)
            }
            Constants.GREEN->{
                setTheme(R.style.GreenTheme)
            }
            Constants.PEACH->{
                setTheme(R.style.PeachTheme)
            }
            Constants.PURPLE->{
                setTheme(R.style.PurpleTheme)
            }
            Constants.WHITE->{
                setTheme(R.style.WhiteTheme)
            }
            Constants.YELLOW->{
                setTheme(R.style.YellowTheme)
            }
            Constants.PINK->{
                setTheme(R.style.PinkTheme)
            }
        }

        if(SharedPref.isNightModeOn){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)
    }

    fun moveToMainActivity() {
        val i = Intent(this.applicationContext, MainActivity::class.java)
        startActivity(i)
        finish()
    }
}