package com.baljeet.expirytracker.util

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.baljeet.expirytracker.MainActivity
import com.baljeet.expirytracker.R

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        val i = Intent(context,MainActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context,0,i,PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(context!!,"expiryTrackerBaljeet")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Update on Your Products :)")
            .setContentText("All of your Products are in good shape, will keep you posted if something goes side ways!!")
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        val notificationManager =  NotificationManagerCompat.from(context)
        notificationManager.notify(123,builder.build())
    }
}