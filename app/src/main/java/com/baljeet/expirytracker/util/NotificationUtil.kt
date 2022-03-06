package com.baljeet.expirytracker.util

import android.app.*
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.dwellify.contractorportal.util.TimeConvertor
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaLocalDateTime
import java.util.*

const val channelName = "Single Product Updates"
const val channelDescription = "This notification channel is dedicated to show reminders about approaching expiry date of a product on specified time."
const val notificationId = 1
const val channelID = "expiry_tracker_88"
const val titleExtra = "trackerId"

object NotificationUtil {

    fun setReminderForProducts(dateTime : LocalDateTime, context : Context, trackerId : Int) {

        val millis = dateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        val intent = Intent(context, NotificationReceiver::class.java)
        intent.putExtra(titleExtra, trackerId)

        val pendingIntent = PendingIntent.getBroadcast(
            context, trackerId, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, millis,pendingIntent)
    }

    fun removeReminderForProduct(context : Context , trackerId : Int){
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, trackerId, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
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