package com.baljeet.expirytracker.model

import androidx.annotation.Keep
import com.baljeet.expirytracker.data.relations.TrackerAndProduct
import kotlinx.datetime.LocalDateTime

@Keep
data class DayWithProducts(
    var date : LocalDateTime?,
    var isCurrentDate : Boolean,
    var isSelected : Boolean,
    var products: ArrayList<TrackerAndProduct>?
)