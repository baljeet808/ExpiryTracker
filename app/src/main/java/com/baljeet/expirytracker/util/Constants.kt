package com.baljeet.expirytracker.util

import kotlinx.datetime.TimeZone


object Constants {

    const val PRODUCT_STATUS_ALL ="All"
    const val PRODUCT_STATUS_FRESH ="Fresh"
    const val PRODUCT_STATUS_EXPIRED ="Expired"
    const val PRODUCT_STATUS_EXPIRING ="Expiring"
    val TIMEZONE = TimeZone.UTC


    const val SHOW_ALL = 1
    const val SHOW_ONLY_FAVOURITE = 2
    const val SHOW_ONLY_NON_FAVOURITE = 3
}