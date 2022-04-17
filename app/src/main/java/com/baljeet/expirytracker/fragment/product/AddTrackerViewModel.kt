package com.baljeet.expirytracker.fragment.product

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.baljeet.expirytracker.CustomApplication
import com.baljeet.expirytracker.data.Category
import com.baljeet.expirytracker.data.Product
import java.time.LocalDateTime

class AddTrackerViewModel(app: Application): AndroidViewModel(app) {

    val context = getApplication<CustomApplication>()

    var categoryGiven = MutableLiveData(false)
    var selectedCategory : Category? = null

    var productGiven = MutableLiveData(false)
    var selectedProduct : Product? = null

    var expiryGiven = MutableLiveData(false)
    var expiryDate: LocalDateTime? = null


    var mfgGiven = MutableLiveData(true)
    var mfgDate: LocalDateTime? = null

    var reminderGiven = MutableLiveData(false)
    var reminderDate: LocalDateTime? = null

    init {
        val currentDate = LocalDateTime.now()
        mfgDate = LocalDateTime.of(currentDate.year,currentDate.month,currentDate.dayOfMonth,0,5,0)
    }

}