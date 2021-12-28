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

    override fun onReceive(context: Context?, intent: Intent?) {

        val i = Intent(context, MainActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            i,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

       val messages = ProductStatus.getStatusMessage(context!!)
        var message = ""
        for(msg in messages){
            message = message.plus(msg)
        }
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