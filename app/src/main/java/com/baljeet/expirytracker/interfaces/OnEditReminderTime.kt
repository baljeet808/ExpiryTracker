package com.baljeet.expirytracker.interfaces

import com.baljeet.expirytracker.data.relations.TrackerAndProduct
import java.text.FieldPosition

interface OnEditReminderTime {
    fun openDateTimePickerToEditReminder(tracker : TrackerAndProduct, position: Int)
}