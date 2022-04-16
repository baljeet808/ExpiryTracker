package com.baljeet.expirytracker.data.daos

import androidx.annotation.Keep
import androidx.lifecycle.LiveData
import androidx.room.*
import com.baljeet.expirytracker.data.Tracker
import com.baljeet.expirytracker.data.relations.TrackerAndProduct
import java.time.LocalDateTime

@Keep
@Dao
interface TrackerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addTracker(tracker: Tracker)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateTracker(tracker: Tracker)

    @Transaction
    @Query("SELECT * From tracker Where isArchived == :alwaysFalse And isUsed == :alwaysFalse")
    fun readAllTracker(alwaysFalse : Boolean = false) : LiveData<List<TrackerAndProduct>>

    @Transaction
    @Query("SELECT * FROM tracker")
    fun getAllTracker() : LiveData<List<TrackerAndProduct>>

    @Transaction
    @Query("SELECT * FROM tracker where trackerId == :trackerId")
    fun getTrackerByID(trackerId: Int) : TrackerAndProduct

    @Transaction
    @Query("SELECT * FROM tracker where (isArchived == :alwaysTrue And gotExpired == :alwaysTrue) Or isUsed == :alwaysTrue")
    fun getAllFinishedTracker(alwaysTrue : Boolean = true ) : LiveData<List<TrackerAndProduct>>

    @Transaction
    @Query("SELECT * From tracker Where isArchived == :alwaysFalse And isUsed == :alwaysFalse")
    fun getActiveTracker(alwaysFalse : Boolean = false) : List<TrackerAndProduct>


    @Transaction
    @Query("SELECT * From tracker Where isArchived == :alwaysFalse And isUsed == :alwaysFalse")
    fun getActiveTrackerLive(alwaysFalse : Boolean = false) : LiveData<List<TrackerAndProduct>>


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