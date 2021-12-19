package com.baljeet.expirytracker.interfaces

import com.baljeet.expirytracker.data.Tracker

interface UpdateTrackerListener {
    fun updateTracker(updatedTracker : Tracker)
}