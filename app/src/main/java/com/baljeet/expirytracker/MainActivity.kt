package com.baljeet.expirytracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.baljeet.expirytracker.data.viewmodels.CategoryViewModel
import com.baljeet.expirytracker.data.viewmodels.ImageViewModel
import com.baljeet.expirytracker.data.viewmodels.ProductViewModel
import com.baljeet.expirytracker.fragment.shared.SelectFromViewModel
import com.baljeet.expirytracker.util.SharedPref
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