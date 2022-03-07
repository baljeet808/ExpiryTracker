package com.baljeet.expirytracker.interfaces

import com.baljeet.expirytracker.data.relations.TrackerAndProduct

interface OnReminderCheckedChanged {
    fun setReminderOnValue(tracker : TrackerAndProduct, isChecked : Boolean)
}