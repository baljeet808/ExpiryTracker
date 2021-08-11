package com.baljeet.expirytracker.model

import kotlinx.datetime.LocalDateTime


data class Product(
    var productId : Int,
    var name : String,
    var category: Category,
    var image: Image?,
    var mfgDate : LocalDateTime,
    var expiryDate : LocalDateTime
)
