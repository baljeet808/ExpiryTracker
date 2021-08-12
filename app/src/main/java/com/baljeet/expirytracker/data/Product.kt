package com.baljeet.expirytracker.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    var productId: Int,
    var name: String,
    var categoryId: Int,
    var imageId: Int
)
