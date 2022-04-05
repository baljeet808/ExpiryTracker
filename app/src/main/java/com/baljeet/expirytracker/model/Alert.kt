package com.baljeet.expirytracker.model

import androidx.annotation.Keep

@Keep
data class Alert (
    var itemName : String,
    var expiryDate : String,
    var imageUrl : String,
    var progress : Int
        )