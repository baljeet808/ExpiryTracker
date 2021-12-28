package com.baljeet.expirytracker.interfaces

import com.baljeet.expirytracker.model.DayWithProducts
import kotlinx.datetime.LocalDateTime

interface OnDateSelectedListener {
        fun openSelectedDate(dayWithProducts: DayWithProducts)
}