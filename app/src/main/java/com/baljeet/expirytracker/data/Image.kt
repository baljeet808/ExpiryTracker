package com.baljeet.expirytracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Images")
data class Image(
    @PrimaryKey(autoGenerate = true)
    var imageId : Int,
    var imageUrl : String,
    var imageName : String,
    var alt : String
)
