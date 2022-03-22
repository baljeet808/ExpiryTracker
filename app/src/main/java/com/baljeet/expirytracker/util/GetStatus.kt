package com.baljeet.expirytracker.util

import com.baljeet.expirytracker.data.Tracker
import java.time.Duration
import java.time.LocalDate

object GetStatus {

    fun getStatus(tracker : Tracker): String{

        val expiryDate = tracker.expiryDate
        val mfgDate = tracker.mfgDate

        val dateToday = LocalDate.now()

        val totalHours = Duration.between(mfgDate,expiryDate).toMinutes()
        val spentHours = Duration.between(mfgDate,dateToday).toMinutes()


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