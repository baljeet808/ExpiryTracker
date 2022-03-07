package com.baljeet.expirytracker.interfaces

import com.baljeet.expirytracker.data.relations.TrackerAndProduct

interface OnEditReminderTime {
    fun openDateTimePickerToEditReminder(tracker : TrackerAndProduct)
}