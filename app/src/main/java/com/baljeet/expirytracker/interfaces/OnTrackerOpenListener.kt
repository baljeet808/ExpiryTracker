package com.baljeet.expirytracker.interfaces

import com.baljeet.expirytracker.data.Tracker
import com.baljeet.expirytracker.data.relations.TrackerAndProduct

interface OnTrackerOpenListener {
    fun openTrackerInfo(tracker : TrackerAndProduct)
}