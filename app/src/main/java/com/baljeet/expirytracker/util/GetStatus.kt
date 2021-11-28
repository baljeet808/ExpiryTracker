package com.baljeet.expirytracker.util

import com.baljeet.expirytracker.data.Tracker
import com.dwellify.contractorportal.util.TimeConvertor
import kotlinx.datetime.*

object GetStatus {

    fun getStatus(tracker : Tracker): String{
        val dateToday = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val today = LocalDate(dateToday.year,dateToday.monthNumber,dateToday.dayOfMonth)
        val mfgInstant = tracker.mfgDate!!
        val expiryInstant = tracker.expiryDate!!

        val totalPeriod = mfgInstant.periodUntil(expiryInstant)
        val periodSpent = mfgInstant.periodUntil(today)

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