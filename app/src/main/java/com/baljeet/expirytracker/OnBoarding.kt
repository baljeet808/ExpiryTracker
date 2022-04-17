package com.baljeet.expirytracker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.TelephonyManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.baljeet.expirytracker.util.Constants
import com.baljeet.expirytracker.util.NotificationUtil
import com.baljeet.expirytracker.util.SharedPref
import java.util.*

class OnBoarding : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        SharedPref.init(this)
        if (SharedPref.isAlertEnabled && SharedPref.isDailyAlertEnabled) {
            if (SharedPref.isDailyAlertSetup.not()) {
                NotificationUtil.setDailyReminder(applicationContext)
                SharedPref.isDailyAlertSetup = true
            }
        }
        if (SharedPref.isNightModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        when (SharedPref.themeName) {
            Constants.BLUE -> {
                setTheme(R.style.Theme_ExpiryTracker)
            }
            Constants.BLACK -> {
                setTheme(R.style.BlackTheme)
            }
            Constants.GREEN -> {
                setTheme(R.style.GreenTheme)
            }
            Constants.PEACH -> {
                setTheme(R.style.PeachTheme)
            }
            Constants.PURPLE -> {
                setTheme(R.style.PurpleTheme)
            }
            Constants.WHITE -> {
                setTheme(R.style.WhiteTheme)
            }
            Constants.YELLOW -> {
                setTheme(R.style.YellowTheme)
            }
            Constants.PINK -> {
                setTheme(R.style.PinkTheme)
            }
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)
        val manager =
            applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        SharedPref.usingTab =
            Objects.requireNonNull(manager).phoneType == TelephonyManager.PHONE_TYPE_NONE
    }

    fun moveToMainActivity() {
        val i = Intent(this.applicationContext, MainActivity::class.java)
        startActivity(i)
        finish()
    }
}