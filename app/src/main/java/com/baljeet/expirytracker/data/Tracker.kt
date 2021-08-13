package com.baljeet.expirytracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Tracker")
data class Tracker(
    @PrimaryKey(autoGenerate = true)
    var trackerId: Int,
    var productId: Int,
    var mfgDate: Long?,
    var expiryDate: Long?
)
