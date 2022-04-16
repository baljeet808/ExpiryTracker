package com.baljeet.expirytracker.fragment.product

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.baljeet.expirytracker.CustomApplication
import com.baljeet.expirytracker.data.Image

class CustomViewModel(app : Application) : AndroidViewModel(app) {
    val context  = getApplication<CustomApplication>()

    var croppedImage : Image? = null
    var once = 1
}