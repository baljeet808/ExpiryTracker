package com.baljeet.expirytracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    var categoryId : Int,
    var categoryName : String,
    var imageId : Int
)
