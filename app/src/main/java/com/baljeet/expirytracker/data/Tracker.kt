package com.baljeet.expirytracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate

@Entity(tableName = "Tracker")
data class Tracker(
    @PrimaryKey(autoGenerate = true)
    var trackerId: Int,
    var productId: Int,
    var mfgDate: LocalDate?,
    var expiryDate: LocalDate?
)
