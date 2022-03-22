package com.baljeet.expirytracker.util

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.baljeet.expirytracker.data.AppDatabase
import com.baljeet.expirytracker.data.Product
import com.baljeet.expirytracker.data.repository.TrackerRepository
import java.time.Duration
import java.time.LocalDate

object ProductStatus {
    private val expired = ArrayList<Product>()
    private val needsAttention = ArrayList<Product>()
    private val healthy = ArrayList<Product>()
    private val messages = ArrayList<String>()

    fun getStatusMessage(context: Context): ArrayList<String> {
        val trackerDao = AppDatabase.getDatabase(context).trackerDao()
        val repository = TrackerRepository(trackerDao)

        try {
            repository.getActiveTrackers().let { trackers ->
                expired.clear()
                needsAttention.clear()
                healthy.clear()
                messages.clear()
                for (tracker in trackers) {

                    val expiryDate = tracker.tracker.expiryDate
                    val mfgDate = tracker.tracker.mfgDate

                    val dateToday = LocalDate.now()

                    val totalHours = Duration.between(mfgDate,expiryDate).toMinutes()
                    val spentHours = Duration.between(mfgDate,dateToday).toMinutes()


                    val progressValue =
                        (spentHours.toFloat() / totalHours.toFloat()) * 100
                    when {
                        progressValue >= 80 -> {
                            expired.add(tracker.productAndCategoryAndImage.product)
                        }
                        progressValue < 80 && progressValue >= 50 -> {
                            needsAttention.add(tracker.productAndCategoryAndImage.product)
                        }
                        progressValue < 50 -> {
                            healthy.add(tracker.productAndCategoryAndImage.product)
                        }
                    }
                }
            }
        }
        catch (e : Exception){
            Toast.makeText(context,"${e.message}", Toast.LENGTH_SHORT).show()
        }



        try {
            if (expired.isNotEmpty()) {
                 when (expired.size) {
                    1 -> {
                        messages.add("${expired[0].name} has been expired \ud83d\ude22.")
                    }
                    2 -> {
                        messages.add("${expired[0].name} and ${expired[1].name} have been expired \ud83d\ude22.")
                    }
                    else -> {
                        messages.add("${expired.size} products have been expired \uD83D\uDE10.")
                    }
                }
            }
            if (needsAttention.isNotEmpty()) {
                when (needsAttention.size) {
                    1 -> {
                        messages.add("'${needsAttention[0].name}' needs your attention \uD83D\uDE42.")
                    }
                    2 -> {
                        messages.add("${needsAttention[0].name} and ${needsAttention[1].name} needs your attention \uD83D\uDE42.")
                    }
                    else -> {
                        messages.add("${needsAttention.size} needs your attention \uD83D\uDE42.")
                    }
                }
            }
            if (healthy.isNotEmpty()) {
                when (healthy.size) {
                    1 -> {
                        messages.add("'${healthy[0].name}' is in good shape \uD83D\uDE00.")
                    }
                    2 -> {
                        messages.add("'${healthy[0].name}' and '${healthy[1].name}' are in good shape\uD83D\uDE00.")
                    }
                    else -> {
                        messages.add("${healthy.size} products are in good shape \uD83D\uDE00.")
                    }
                }
            }
            if(messages.isEmpty()){
                messages.add("Let's track your product and keep them healthy.")
            }
        }
        catch (e : Exception){
            Log.d("Log for ","error - ${e.message}")
        }

        return messages
    }

}