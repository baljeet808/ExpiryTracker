package com.baljeet.expirytracker.util

import android.app.Application
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.baljeet.expirytracker.MainActivity
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.AppDatabase
import com.baljeet.expirytracker.data.Product
import com.baljeet.expirytracker.data.repository.TrackerRepository
import com.dwellify.contractorportal.util.TimeConvertor
import kotlinx.coroutines.CoroutineScope
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.periodUntil

class NotificationReceiver : BroadcastReceiver() {

    private val expired = ArrayList<Product>()
    private val needsAttention = ArrayList<Product>()
    private val healthy = ArrayList<Product>()


    override fun onReceive(context: Context?, intent: Intent?) {

        val trackerDao = AppDatabase.getDatabase(context!!).trackerDao()
        val repository = TrackerRepository(trackerDao)

        val i = Intent(context, MainActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            i,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        var message = ""
        try {
                repository.getAllTracker().let { trackers ->
                    expired.clear()
                    needsAttention.clear()
                    healthy.clear()
                    Toast.makeText(context, "${trackers.size} trackers found", Toast.LENGTH_LONG)
                        .show()
                    Log.d("Log for - ", "${trackers.size} trackers found")
                    for (tracker in trackers) {
                        val today = Clock.System.now()
                        val mfgInstant =
                            TimeConvertor.fromEpochMillisecondsToInstant(tracker.tracker.mfgDate!!)
                        val expiryInstant =
                            TimeConvertor.fromEpochMillisecondsToInstant(tracker.tracker.expiryDate!!)

                        val totalPeriod = mfgInstant.periodUntil(expiryInstant, TimeZone.UTC)
                        val periodSpent = mfgInstant.periodUntil(today, TimeZone.UTC)

                        val progressValue =
                            (periodSpent.days.toFloat() / (totalPeriod.days + 1).toFloat()) * 100
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
            message = message.plus("${e.message}")
            Toast.makeText(context,"${e.message}", Toast.LENGTH_SHORT).show()
        }



        try {
            if (expired.isNotEmpty()) {
                message = when (expired.size) {
                    1 -> {
                        message.plus("${expired[0].name} has been expired \ud83d\ude22.\n")
                    }
                    2 -> {
                        message.plus("${expired[0].name} and ${expired[1].name} have been expired \ud83d\ude22.\n")
                    }
                    3 -> {
                        message.plus("${expired[0].name}, ${expired[1].name} and ${expired[2].name} have been expired \ud83d\ude22.\n")
                    }
                    else -> {
                        message.plus("${expired.size} products have been expired \uD83D\uDE10.\n")
                    }
                }
            }
            if (needsAttention.isNotEmpty()) {
                message = when (needsAttention.size) {
                    1 -> {
                        message.plus("Product '${needsAttention[0].name}' needs your attention \uD83D\uDE42.\n")
                    }
                    2 -> {
                        message.plus("${needsAttention[0].name} and ${needsAttention[1].name} needs your attention \uD83D\uDE42.\n")
                    }
                    3 -> {
                        message.plus("${needsAttention[0].name}, ${needsAttention[1].name} and ${needsAttention[2].name} needs your attention \uD83D\uDE42.\n")
                    }
                    else -> {
                        message.plus("${needsAttention.size} needs your attention \uD83D\uDE42.\n")
                    }
                }
            }
            if (healthy.isNotEmpty()) {
                message = when (healthy.size) {
                    1 -> {
                        message.plus("Your product '${healthy[0].name}' is in good shape \uD83D\uDE00.\nWill keep an eye on it for you")
                    }
                    2 -> {
                        message.plus("Your products '${healthy[0].name}' and '${healthy[1].name}' are in good shape \uD83D\uDE00.\nWill keep an eye on them for you")
                    }
                    3 -> {
                        message.plus("${healthy[0].name}, ${healthy[1].name} and ${healthy[2].name} are in good shape.\nTracking them with eagle-eye \ud83d\ude07")
                    }
                    else -> {
                        message.plus("${healthy.size} products are in good shape \uD83D\uDE00.")
                    }
                }
            }
            if(message.isEmpty()){
                message = "Tracker dao not working"
            }
        }
        catch (e : Exception){
            message = "error - ${e.message}"
        }
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
        val builder = NotificationCompat.Builder(context, "expiryTrackerBaljeet")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Update on Your Products \ud83d\ude0e")
            .setContentText("Expand to view Descriptions")
            .setAutoCancel(true).setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(123, builder.build())
    }
}