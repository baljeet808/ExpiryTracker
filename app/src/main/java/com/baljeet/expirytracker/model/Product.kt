package com.baljeet.expirytracker.model

data class Product(
    var productId : Int,
    var name : String,
    var categoryId: Int?,
    var image: Image?
)
