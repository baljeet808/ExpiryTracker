package com.baljeet.expirytracker.model

import com.baljeet.expirytracker.data.relations.TrackerAndProduct
import kotlinx.datetime.LocalDate

data class DayWithProducts(
    var date : LocalDate?,
    var products: ArrayList<TrackerAndProduct>?
)