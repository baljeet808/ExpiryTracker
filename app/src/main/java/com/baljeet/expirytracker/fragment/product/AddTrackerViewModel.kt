package com.baljeet.expirytracker.fragment.product

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.baljeet.expirytracker.CustomApplication
import com.baljeet.expirytracker.data.relations.CategoryAndImage
import com.baljeet.expirytracker.data.relations.ProductAndImage
import java.time.LocalDateTime

class AddTrackerViewModel(app: Application): AndroidViewModel(app) {

    val context = getApplication<CustomApplication>()

    var categoryGiven = MutableLiveData(false)
    var selectedCategory : CategoryAndImage? = null

    var productGiven = MutableLiveData(false)
    var selectedProduct : ProductAndImage? = null

    var expiryGiven = MutableLiveData(false)
    var expiryDate: LocalDateTime? = null


    var mfgGiven = MutableLiveData(true)
    var mfgDate: LocalDateTime? = LocalDateTime.now()

    var reminderGiven = MutableLiveData(false)
    var reminderDate: LocalDateTime? = null

}