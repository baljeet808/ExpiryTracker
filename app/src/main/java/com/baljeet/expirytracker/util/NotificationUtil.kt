package com.baljeet.expirytracker.util

import android.app.*
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.datetime.toKotlinLocalDateTime
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

const val channelName = "Single Product Updates"
const val channelDescription = "This notification channel is dedicated to show reminders about approaching expiry date of a product on specified time."
const val channelID = "expiry_tracker_88"
const val titleExtra = "trackerId"

object NotificationUtil {

    fun setReminderForProducts(dateTime : LocalDateTime, context : Context, trackerId : Int) {
        val cal = Calendar.getInstance()
        cal.set(dateTime.year,dateTime.monthValue-1,dateTime.dayOfMonth,dateTime.hour,dateTime.minute,dateTime.second)

        Log.d("Log for - calendar date","\n\n\n\n\n\n${cal.time}\n\n\n\n\n")

        val intent = Intent(context, NotificationReceiver::class.java)
        intent.putExtra(titleExtra, trackerId)
        val pendingIntent = PendingIntent.getBroadcast(
            context, trackerId, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis,pendingIntent)
    }

    fun removeReminderForProduct(context : Context , trackerId : Int){
        val intent = Intent(context, NotificationReceiver::class.java)
        intent.putExtra(titleExtra, trackerId)
        val pendingIntent = PendingIntent.getBroadcast(
            context, trackerId, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    fun createNotificationChannel(context : Context){
        val name = channelName
        val desc = channelDescription
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelID,name,importance)
        channel.description = desc
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

}