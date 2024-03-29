package com.baljeet.expirytracker.data

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Keep
@Parcelize
@Entity(tableName = "Categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    var categoryId : Int,
    var categoryName : String,
    var imageId : Int,
    var isDeleted : Boolean
) :Parcelable
