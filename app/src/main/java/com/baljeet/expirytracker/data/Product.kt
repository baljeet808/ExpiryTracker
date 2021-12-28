package com.baljeet.expirytracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    var productId: Int,
    var name: String,
    var categoryId: Int,
    var imageId: Int,
)
