package com.baljeet.expirytracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.baljeet.expirytracker.util.SharedPref

class OnBoarding : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        SharedPref.init(this)
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