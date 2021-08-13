package com.baljeet.expirytracker.data.repository

import androidx.lifecycle.LiveData
import com.baljeet.expirytracker.data.Tracker
import com.baljeet.expirytracker.data.daos.TrackerDao
import com.baljeet.expirytracker.data.relations.TrackerAndProduct

class TrackerRepository(private val trackerDao: TrackerDao) {

    val readAllTrackers : LiveData<List<TrackerAndProduct>> = trackerDao.readAllTracker()

    suspend fun addTracker(tracker: Tracker){
        trackerDao.addTracker(tracker)
    }

    fun readTrackerById(id : Int): TrackerAndProduct{
        return trackerDao.readTrackerById(id)
    }

}