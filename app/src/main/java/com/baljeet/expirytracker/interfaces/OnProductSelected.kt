package com.baljeet.expirytracker.interfaces

import com.baljeet.expirytracker.data.relations.ProductAndImage

interface OnProductSelected {
    fun openInfoOfProduct(productAndImage: ProductAndImage)
}