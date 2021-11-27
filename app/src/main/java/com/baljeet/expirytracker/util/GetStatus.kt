package com.baljeet.expirytracker.util

import com.baljeet.expirytracker.data.Tracker
import com.dwellify.contractorportal.util.TimeConvertor
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.periodUntil

object GetStatus {

    fun getStatus(tracker : Tracker): String{
        val today = Clock.System.now()
        val mfgInstant = TimeConvertor.fromEpochMillisecondsToInstant(tracker.mfgDate!!)
        val expiryInstant =
            TimeConvertor.fromEpochMillisecondsToInstant(tracker.expiryDate!!)

        val totalPeriod = mfgInstant.periodUntil(expiryInstant, TimeZone.UTC)
        val periodSpent = mfgInstant.periodUntil(today, TimeZone.UTC)

        val totalHours = totalPeriod.days * 24 + totalPeriod.hours
        val spentHours = periodSpent.days * 24 + periodSpent.hours

        val progressValue =
            (spentHours.toFloat() / totalHours.toFloat()) * 100

        return when {
            progressValue >= 100-> {
                Constants.PRODUCT_STATUS_EXPIRED
            }
            progressValue < 100 && progressValue >= 50 -> {
                Constants.PRODUCT_STATUS_EXPIRING
            }
            progressValue > 0 && progressValue < 50 -> {
                Constants.PRODUCT_STATUS_FRESH
            }
            else->{
                Constants.PRODUCT_STATUS_ALL
            }
        }
    }
}