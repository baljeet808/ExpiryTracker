package com.baljeet.expirytracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime

@Entity(tableName = "Tracker")
data class Tracker(
    @PrimaryKey(autoGenerate = true)
    var trackerId: Int,
    var productId: Int,
    var mfgDate: LocalDateTime?,
    var expiryDate: LocalDateTime?,
    var userBeforeExpiry : Boolean?,
    var quantity : Double?,
    var measuringUnit : String?,
    var note : String?,
    var isFavourite : Boolean?
)
