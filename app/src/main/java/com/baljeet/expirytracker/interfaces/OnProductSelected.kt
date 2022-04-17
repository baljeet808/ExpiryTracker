package com.baljeet.expirytracker.interfaces

import com.baljeet.expirytracker.data.Product

interface OnProductSelected {
    fun openInfoOfProduct(product: Product)
}