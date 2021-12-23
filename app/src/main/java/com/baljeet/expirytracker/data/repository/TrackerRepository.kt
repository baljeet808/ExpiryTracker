package com.baljeet.expirytracker.data.repository

import androidx.lifecycle.LiveData
import com.baljeet.expirytracker.data.Tracker
import com.baljeet.expirytracker.data.daos.TrackerDao
import com.baljeet.expirytracker.data.relations.TrackerAndProduct
import kotlinx.datetime.LocalDateTime

class TrackerRepository(private val trackerDao: TrackerDao) {

    val readAllTrackers : LiveData<List<TrackerAndProduct>> = trackerDao.readAllTracker()

    suspend fun addTracker(tracker: Tracker){
        trackerDao.addTracker(tracker)
    }

    suspend fun updateTracker(tracker: Tracker){
        trackerDao.updateTracker(tracker)
    }

    val getAllTrackers : LiveData<List<TrackerAndProduct>> = trackerDao.getAllTracker()

    fun getActiveTrackers():List<TrackerAndProduct>{
        return trackerDao.getActiveTracker()
    }

    fun readTrackerByExpiryDate(date : LocalDateTime): List<TrackerAndProduct>{
        return trackerDao.readTrackerByDate(date)
    }

}