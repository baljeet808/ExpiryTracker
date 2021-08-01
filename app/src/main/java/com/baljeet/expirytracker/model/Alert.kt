package com.baljeet.expirytracker.model

data class Alert (
    var itemName : String,
    var expiryDate : String,
    var imageUrl : String,
    var progress : Int
        )