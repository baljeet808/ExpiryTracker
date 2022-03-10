package com.baljeet.expirytracker.fragment.shared

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.baljeet.expirytracker.CustomApplication

class PdfViewModel(app : Application): AndroidViewModel(app) {
    val context = getApplication<CustomApplication>()


}