package com.baljeet.expirytracker.util

import kotlinx.datetime.TimeZone


object Constants {

    const val PRODUCT_STATUS_ALL ="All"
    const val PRODUCT_STATUS_FRESH ="Fresh"
    const val PRODUCT_STATUS_EXPIRED ="Expired"
    const val PRODUCT_STATUS_EXPIRING ="Expiring"
    val TIMEZONE = TimeZone.UTC
}