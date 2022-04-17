package com.baljeet.expirytracker.fragment.settings.notifications

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.relations.TrackerAndProduct
import com.baljeet.expirytracker.data.viewmodels.TrackerViewModel
import com.baljeet.expirytracker.databinding.FragmentManageNotificationsBinding
import com.baljeet.expirytracker.interfaces.OnEditReminderTime
import com.baljeet.expirytracker.interfaces.OnReminderCheckedChanged
import com.baljeet.expirytracker.listAdapters.RemindersAdapter
import com.baljeet.expirytracker.util.NotificationUtil
import com.baljeet.expirytracker.util.SharedPref
import java.time.LocalDateTime
import java.util.*

class ManageNotifications : Fragment(), OnEditReminderTime, OnReminderCheckedChanged,
    DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private lateinit var bind: FragmentManageNotificationsBinding

    private val viewModel: TrackerViewModel by viewModels()

    private lateinit var adapter: RemindersAdapter

    private var tempReminderTime: LocalDateTime? = null
    private var tempTrackerToUpdate: TrackerAndProduct? = null

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
            adapter = RemindersAdapter(
                requireContext(),
                this@ManageNotifications,
                this@ManageNotifications
            )
            allNotificationToggle.isChecked = SharedPref.isAlertEnabled
            allNotificationToggle.text = if (SharedPref.isAlertEnabled) {
                getString(R.string.notifications_on)
            } else {
                getString(R.string.notifications_off)
            }
            dailyNotificationToggle.isChecked = SharedPref.isDailyAlertEnabled
            allNotificationToggle.text = if (SharedPref.isDailyAlertEnabled) {
                getString(R.string.daily_reminder_is_on)
            } else {
            getString(R.string.daily_reminder_is_off)
        }
            allNotificationToggle.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    buttonView.text = getString(R.string.notifications_on)
                    SharedPref.isAlertEnabled = true
                    Toast.makeText(requireContext(), "Notifications are on", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    buttonView.text = getString(R.string.notifications_off)
                    SharedPref.isAlertEnabled = false
                    Toast.makeText(
                        requireContext(),
                        "Notifications are off now",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            dailyNotificationToggle.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    buttonView.text = getString(R.string.daily_reminder_is_on)
                    SharedPref.isDailyAlertEnabled = true
                    Toast.makeText(requireContext(), "Daily reminders are on now", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    buttonView.text = getString(R.string.daily_reminder_is_off)
                    SharedPref.isDailyAlertEnabled = false
                    Toast.makeText(
                        requireContext(),
                        "Daily reminders are off now",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            reminderRecycler.layoutManager = LinearLayoutManager(requireContext())
            reminderRecycler.adapter = adapter
            viewModel.getActiveTrackersLive().observe(viewLifecycleOwner) {
                if(it.isNotEmpty()){
                    adapter.submitList(it)
                    reminderRecycler.isGone = false
                    noItemText.isGone = true
                }else{
                    reminderRecycler.isGone = true
                    noItemText.isGone = false
                }
            }
        }
        return bind.root
    }
    private var adapterUpdateIndex = 0

    override fun openDateTimePickerToEditReminder(tracker: TrackerAndProduct, position : Int) {
        adapterUpdateIndex = position
        val currentTime = Calendar.getInstance()

        tempTrackerToUpdate = tracker
        tracker.tracker.reminderDate?.let {
            DatePickerDialog(
                requireContext(),
                this,
                it.year,
                it.monthValue-1,
                it.dayOfMonth
            ).show()
        }?: kotlin.run {
            DatePickerDialog(
                requireContext(),
                this,
                currentTime.get(Calendar.YEAR),
                currentTime.get(Calendar.MONTH),
                currentTime.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }
    override fun setReminderOnValue(tracker: TrackerAndProduct, isChecked: Boolean) {
        tracker.tracker.apply {
            reminderOn = isChecked
        }
        viewModel.updateTracker(tracker.tracker)
        if (isChecked) {
            tracker.tracker.reminderDate?.let {
                NotificationUtil.setReminderForProducts(
                    it,
                    requireContext(),
                    tracker.tracker.trackerId
                )
            }
        } else {
            tracker.tracker.reminderDate?.let {
                NotificationUtil.removeReminderForProduct(
                    requireContext(),
                    tracker.tracker.trackerId
                )
            }
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val currentTime = Calendar.getInstance()
        TimePickerDialog(requireContext(), this, currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE), false).show()
        tempReminderTime = LocalDateTime.of(year, month+1, dayOfMonth, 0, 0)
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        tempReminderTime?.let {
            tempReminderTime = LocalDateTime.of(it.year, it.month, it.dayOfMonth, hourOfDay, minute)
        }
        tempTrackerToUpdate?.let {
            it.tracker.reminderDate = tempReminderTime
            it.tracker.reminderOn = true
        }
        tempTrackerToUpdate?.tracker?.let {
            viewModel.updateTracker(it)
        }
        tempTrackerToUpdate?.tracker?.let {
            it.reminderDate?.let { date->
                NotificationUtil.setReminderForProducts(
                    date,
                    requireContext(),
                    it.trackerId
                )
            }
        }
        adapter.notifyItemChanged(adapterUpdateIndex)
    }
}