package com.baljeet.expirytracker.fragment.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.baljeet.expirytracker.CustomApplication
import com.baljeet.expirytracker.util.Constants
import com.baljeet.expirytracker.util.SharedPref

class DonateViewModel(app : Application) : AndroidViewModel(app) {

    val context = getApplication<CustomApplication>()

    private var donationAmount = 0.0

    init {
        SharedPref.init(context)
    }


    fun getDonationAmount (): Double{
        return donationAmount
    }

    fun selectCandy(){
        donationAmount = Constants.CANDY
    }
    fun selectChocolate(){
        donationAmount = Constants.CHOCOLATE
    }
    fun selectCoffee(){
        donationAmount = Constants.COFFEE
    }
    fun selectBurger(){
        donationAmount = Constants.BURGER
    }
    fun selectMeal(){
        donationAmount = Constants.MEAL
    }

}