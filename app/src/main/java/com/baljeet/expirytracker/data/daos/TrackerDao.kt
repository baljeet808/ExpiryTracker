package com.baljeet.expirytracker.data.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.baljeet.expirytracker.data.Tracker
import com.baljeet.expirytracker.data.relations.TrackerAndProduct
import kotlinx.datetime.LocalDate

@Dao
interface TrackerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTracker(tracker: Tracker)

    @Transaction
    @Query("SELECT * FROM tracker")
    fun readAllTracker() : LiveData<List<TrackerAndProduct>>

    @Transaction
    @Query("SELECT * FROM tracker")
    fun getAllTracker() : List<TrackerAndProduct>

    @Transaction
    @Query("SELECT * FROM tracker where trackerId == :id")
    fun readTrackerById(id: Int) : TrackerAndProduct

    @Transaction
    @Query("SELECT * FROM tracker where expiryDate == :date")
    fun readTrackerByDate(date: LocalDate) : List<TrackerAndProduct>

}