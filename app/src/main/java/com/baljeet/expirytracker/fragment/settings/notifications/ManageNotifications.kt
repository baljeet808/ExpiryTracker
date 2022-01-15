package com.baljeet.expirytracker.fragment.settings.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.databinding.FragmentManageNotificationsBinding
import com.baljeet.expirytracker.util.NotificationReceiver
import com.baljeet.expirytracker.util.SharedPref
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.*

class ManageNotifications : Fragment() {

    private lateinit var bind: FragmentManageNotificationsBinding

    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent


    private val calendar = Calendar.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SharedPref.init(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentManageNotificationsBinding.inflate(inflater, container, false)
        bind.apply {
            backButton.setOnClickListener { activity?.onBackPressed() }

            allNotificationToggle.isChecked = SharedPref.isAlertEnabled

            allNotificationToggle.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked){
                    buttonView.text = requireContext().getString(R.string.notifications_on)
                    SharedPref.isAlertEnabled = true
                    val time = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                    setReminderForProducts(time.hour, time.minute.plus(5))
                    Toast.makeText(requireContext(),"Notifications are on", Toast.LENGTH_SHORT).show()
                }else{
                    buttonView.text = requireContext().getString(R.string.notifications_off)
                    SharedPref.isAlertEnabled = false
                    alarmManager =
                        requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val intent = Intent(requireContext(), NotificationReceiver::class.java)
                    pendingIntent = PendingIntent.getBroadcast(
                        requireContext(), 0, intent,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                    alarmManager.cancel(pendingIntent)
                    Toast.makeText(requireContext(),"Notifications are off now", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return bind.root
    }


    private fun setReminderForProducts(hour: Int, minutes: Int) {

        calendar[Calendar.HOUR_OF_DAY] = hour
        calendar[Calendar.MINUTE] = minutes
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0

        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), NotificationReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(
            requireContext(), 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
            AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent
        )
    }




}