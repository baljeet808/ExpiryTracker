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
            allNotificationToggle.text =if(SharedPref.isAlertEnabled){
                getString(R.string.notifications_on)
            } else{
                getString(R.string.notifications_off)
            }
            allNotificationToggle.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked){
                    buttonView.text = getString(R.string.notifications_on)
                    SharedPref.isAlertEnabled = true
                    Toast.makeText(requireContext(),"Notifications are on", Toast.LENGTH_SHORT).show()
                }else{
                    buttonView.text =getString(R.string.notifications_off)
                    SharedPref.isAlertEnabled = false
                    Toast.makeText(requireContext(),"Notifications are off now", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return bind.root
    }
}