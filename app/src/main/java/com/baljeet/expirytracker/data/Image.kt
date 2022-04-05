package com.baljeet.expirytracker.data

import android.net.Uri
import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Keep
@Parcelize
@Entity(tableName = "Images")
data class Image(
    @PrimaryKey(autoGenerate = true)
    var imageId : Int,
    var imageUrl : String,
    var imageName : String,
    var alt : String,
    var mimeType : String,
    var uri : Uri,
    var bitmap : String
)  : Parcelable
