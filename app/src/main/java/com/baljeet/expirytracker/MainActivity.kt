package com.baljeet.expirytracker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.baljeet.expirytracker.data.viewmodels.CategoryViewModel
import com.baljeet.expirytracker.data.viewmodels.ImageViewModel
import com.baljeet.expirytracker.data.viewmodels.ProductViewModel
import com.baljeet.expirytracker.fragment.shared.SelectFromViewModel
import com.baljeet.expirytracker.util.Constants
import com.baljeet.expirytracker.util.NotificationUtil
import com.baljeet.expirytracker.util.SharedPref
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {


    private lateinit var bottomNav : BottomNavigationView
    private lateinit var navController : NavController

    private val productVM: ProductViewModel by viewModels()
    private val imageVm: ImageViewModel by viewModels()
    private val selectVM: SelectFromViewModel by viewModels()
    private val categoryVM: CategoryViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        SharedPref.init(this)

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
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.bottom_navigation)
        navController = findNavController(R.id.fragmentContainerView)
        bottomNav.setupWithNavController(navController)

        if (!SharedPref.hasBeenSeeded) {
            seedData()
            SharedPref.hasBeenSeeded = true
        }

        NotificationUtil.createNotificationChannel(applicationContext)

    }

    private fun seedData() {
        addAllImages()
        addAllCategories()
        addAllProducts()
    }

    private fun addAllCategories() {
        for (category in selectVM.getDefaultCategories()) {
            categoryVM.addCategory(category)
        }
    }

    private fun addAllProducts() {
        for (product in selectVM.getAllProducts()) {
            productVM.addProduct(product)
        }
    }

    private fun addAllImages() {
        for (image in selectVM.getImages()) {
            imageVm.addImage(image)
        }
    }

}