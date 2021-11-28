package com.baljeet.expirytracker.data.repository

import androidx.lifecycle.LiveData
import com.baljeet.expirytracker.data.Tracker
import com.baljeet.expirytracker.data.daos.TrackerDao
import com.baljeet.expirytracker.data.relations.TrackerAndProduct
import kotlinx.datetime.LocalDate

class TrackerRepository(private val trackerDao: TrackerDao) {

    val readAllTrackers : LiveData<List<TrackerAndProduct>> = trackerDao.readAllTracker()

    suspend fun addTracker(tracker: Tracker){
        trackerDao.addTracker(tracker)
    }

    fun getAllTracker(): List<TrackerAndProduct>{
        return trackerDao.getAllTracker()
    }

    fun readTrackerById(id : Int): TrackerAndProduct{
        return trackerDao.readTrackerById(id)
    }

    fun readTrackerByExpiryDate(date : LocalDate): List<TrackerAndProduct>{
        return trackerDao.readTrackerByDate(date)
    }

}