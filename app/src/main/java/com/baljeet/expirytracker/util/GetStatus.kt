package com.baljeet.expirytracker.util

import com.baljeet.expirytracker.data.Tracker
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.periodUntil
import kotlinx.datetime.toInstant

object GetStatus {

    fun getStatus(tracker : Tracker): String{
        val dateToday = Clock.System.now()
        val mfgInstant = tracker.mfgDate!!.toInstant(TimeZone.UTC)
        val expiryInstant = tracker.expiryDate!!.toInstant(TimeZone.UTC)

        val totalPeriod = mfgInstant.periodUntil(expiryInstant, TimeZone.UTC)
        val periodSpent = mfgInstant.periodUntil(dateToday, TimeZone.UTC)

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
            progressValue > -100 && progressValue < 50 -> {
                Constants.PRODUCT_STATUS_FRESH
            }
            else->{
                Constants.PRODUCT_STATUS_ALL
            }
        }
    }
}