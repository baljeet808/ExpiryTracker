package com.baljeet.expirytracker.fragment.calendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.baljeet.expirytracker.CustomApplication
import com.baljeet.expirytracker.util.SharedPref

class CalendarViewModel(app : Application) : AndroidViewModel(app) {

    val context = getApplication<CustomApplication>()
    init {
        SharedPref.init(context)
    }


}