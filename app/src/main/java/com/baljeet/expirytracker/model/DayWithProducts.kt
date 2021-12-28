package com.baljeet.expirytracker.model

import com.baljeet.expirytracker.data.relations.TrackerAndProduct
import kotlinx.datetime.LocalDateTime

data class DayWithProducts(
    var date : LocalDateTime?,
    var isCurrentDate : Boolean,
    var isSelected : Boolean,
    var products: ArrayList<TrackerAndProduct>?
)