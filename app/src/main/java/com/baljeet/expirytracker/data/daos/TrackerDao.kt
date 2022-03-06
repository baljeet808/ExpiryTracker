package com.baljeet.expirytracker.data.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.baljeet.expirytracker.data.Tracker
import com.baljeet.expirytracker.data.relations.TrackerAndProduct
import kotlinx.datetime.LocalDateTime

@Dao
interface TrackerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addTracker(tracker: Tracker)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTracker(tracker: Tracker)

    @Transaction
    @Query("SELECT * From tracker Where isArchived == ${false} And isUsed == ${false}")
    fun readAllTracker() : LiveData<List<TrackerAndProduct>>

    @Transaction
    @Query("SELECT * FROM tracker")
    fun getAllTracker() : LiveData<List<TrackerAndProduct>>

    @Transaction
    @Query("SELECT * FROM tracker where trackerId == :trackerId")
    fun getTrackerByID(trackerId: Int) : TrackerAndProduct

    @Transaction
    @Query("SELECT * FROM tracker where (isArchived == ${true} And gotExpired == ${true}) Or isUsed == ${true}")
    fun getAllFinishedTracker() : LiveData<List<TrackerAndProduct>>

    @Transaction
    @Query("SELECT * From tracker Where isArchived == ${false} And isUsed == ${false}")
    fun getActiveTracker() : List<TrackerAndProduct>


    @Transaction
    @Query("SELECT * From tracker Where isArchived == ${false} And isUsed == ${false}")
    fun getActiveTrackerLive() : LiveData<List<TrackerAndProduct>>


    @Transaction
    @Query("SELECT * FROM tracker where trackerId == :id")
    fun readTrackerById(id: Int) : TrackerAndProduct

    @Transaction
    @Query("SELECT * FROM tracker where expiryDate == :date")
    fun readTrackerByDate(date: LocalDateTime) : List<TrackerAndProduct>

    @Transaction
    @Query("SELECT * FROM tracker ORDER BY trackerId DESC LIMIT 1")
    fun getLatestAddedTracker(): TrackerAndProduct


}