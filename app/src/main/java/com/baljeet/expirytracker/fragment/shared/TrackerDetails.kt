package com.baljeet.expirytracker.fragment.shared

import android.animation.ObjectAnimator
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.Tracker
import com.baljeet.expirytracker.data.viewmodels.TrackerViewModel
import com.baljeet.expirytracker.databinding.FragmentTrackerSummaryBinding
import com.baljeet.expirytracker.util.ImageConvertor
import com.baljeet.expirytracker.util.NotificationUtil
import com.google.android.material.timepicker.MaterialTimePicker
import kotlinx.datetime.*
import java.time.LocalDateTime

enum class PickingFor{
    EXPIRY, MFG, REMINDER
}

class TrackerDetails : Fragment() , DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private lateinit var bind : FragmentTrackerSummaryBinding
    private val navArgs : TrackerDetailsArgs by navArgs()
    private val trackerViewModel : TrackerViewModel by viewModels()

    private var pickedFor : PickingFor = PickingFor.EXPIRY
    private var tempDateTime : kotlinx.datetime.LocalDateTime ? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentTrackerSummaryBinding.inflate(inflater, container, false)
        bind.apply {

            backButton.setOnClickListener { activity?.onBackPressed() }
            val selectedTracker = trackerViewModel.getTrackerById(navArgs.selectedTrackerId)
            with(selectedTracker){
                productName.text = productAndCategoryAndImage.product.name
                categoryNameValue.text = productAndCategoryAndImage.categoryAndImage.category.categoryName
                expiryDateValue.text = tracker.expiryDate?.let {
                    getString(R.string.date_string_with_month_name, it.month.name.substring(0,3).uppercase(),it.dayOfMonth,it.year)
                }
                manufactureDateValue.text = tracker.mfgDate?.let {
                    getString(R.string.date_string_with_month_name, it.month.name.substring(0,3).uppercase(),it.dayOfMonth,it.year)
                }
                reminderDateValue.text = tracker.reminderDate?.let {
                    getString(
                        R.string.date_string_with_month_name_and_time_2_lined,
                        Month.of(it.monthNumber).name.substring(0, 3).uppercase(),
                        it.dayOfMonth,
                        it.year,
                        if(it.hour in 1..8) "0".plus(it.hour.toString()) else if (it.hour == 0) "00" else it.hour.toString(),
                        if(it.minute in 1..8) "0".plus(it.minute.toString()) else if (it.minute == 0) "00" else it.minute.toString(),
                        if (it.hour>=12) {"PM"} else {"AM"}
                    )
                } ?: kotlin.run {
                    getString(R.string.no_time)
                }

                when(productAndCategoryAndImage.image.mimeType){
                    "asset"->{
                        optionImage.setImageDrawable(
                            AppCompatResources.getDrawable(
                                requireContext(),
                                resources.getIdentifier(
                                    productAndCategoryAndImage.image.imageUrl,
                                    "drawable",
                                    requireContext().packageName
                                )
                            )
                        )
                    }
                    else->{
                        optionImage.setImageBitmap(
                            ImageConvertor.stringToBitmap(productAndCategoryAndImage.image.bitmap)
                        )
                    }
                }
                when(productAndCategoryAndImage.categoryAndImage.image.mimeType){
                    "asset" ->{
                        categoryImage.setImageDrawable(
                            AppCompatResources.getDrawable(
                                requireContext(),
                                resources.getIdentifier(
                                    productAndCategoryAndImage.categoryAndImage.image.imageUrl,
                                    "drawable",
                                    requireContext().packageName
                                )
                            )
                        )
                    }
                    else->{
                        categoryImage.setImageBitmap(
                            ImageConvertor.stringToBitmap(productAndCategoryAndImage.categoryAndImage.image.bitmap)
                        )
                    }
                }


                val expiryDate = tracker.expiryDate
                val mfgDate = tracker.mfgDate

                val dateToday = Clock.System.now()

                val mfgInstant = mfgDate!!.toInstant(TimeZone.UTC)
                val expiryInstant = expiryDate!!.toInstant(TimeZone.UTC)

                val totalPeriod = mfgInstant.periodUntil(expiryInstant, TimeZone.UTC)
                val periodSpent = mfgInstant.periodUntil(dateToday, TimeZone.UTC)

                val totalHours = totalPeriod.days * 24 + totalPeriod.hours
                val spentHours = periodSpent.days * 24 + periodSpent.hours

                val progressValue =
                    (spentHours.toFloat() / totalHours.toFloat()) * 100
                itemProgressbar.apply {
                    val layerDrawable = progressDrawable as LayerDrawable
                    val progressDrawableClip = layerDrawable.getDrawable(1)
                    when {
                        progressValue >= 100->{
                            progressDrawableClip.setTint(context.getColor(R.color.progress_bad))
                            statusText.text = getString(R.string.expired)
                        }
                        progressValue >= 80 -> {
                            progressDrawableClip.setTint(context.getColor(R.color.red_orange))
                            statusText.text = getString(R.string.expiring)
                        }
                        progressValue < 80 && progressValue >= 50 -> {
                            progressDrawableClip.setTint(context.getColor(R.color.progress_ok))
                            statusText.text = getString(R.string.still_ok)
                        }
                        progressValue < 50 -> {
                            progressDrawableClip.setTint(context.getColor(R.color.progress_great))
                            statusText.text = getString(R.string.fresh)
                        }
                    }
                    ObjectAnimator.ofInt(itemProgressbar,"progress",progressValue.toInt()).setDuration(1500).start()
                }


                editExpiryDateButton.setOnClickListener {
                    pickedFor = PickingFor.EXPIRY
                    DatePickerDialog(requireContext(),this@TrackerDetails,expiryDate.year,expiryDate.monthNumber,expiryDate.dayOfMonth).show()
                }
                editReminderButton.setOnClickListener {
                    pickedFor = PickingFor.REMINDER
                    val current = LocalDateTime.now()
                    DatePickerDialog(requireContext(),this@TrackerDetails,current.year,current.monthValue,current.dayOfMonth).show()
                }
                editMfgDateButton.setOnClickListener {
                    pickedFor = PickingFor.MFG
                    DatePickerDialog(requireContext(),this@TrackerDetails,mfgDate.year,mfgDate.monthNumber,mfgDate.dayOfMonth).show()
                }
                deleteButton.setOnClickListener {
                     delete(tracker,progressValue)
                    activity?.onBackPressed()
                }
                markUsedButton.setOnClickListener {
                    markTrackerAsUsedBasedOnProgress(progressValue, tracker)
                    activity?.onBackPressed()
                }
                reminderOnOffToggle.isChecked = tracker.reminderOn
                reminderOnOffToggle.setOnCheckedChangeListener { _, isChecked ->
                    setReminderOnValue(trackerViewModel.getTrackerById(navArgs.selectedTrackerId).tracker, isChecked)
                }
            }
        }
        return bind.root
    }


    private fun delete(tracker : Tracker, progressValue: Float){
            tracker.isArchived = true
            if(progressValue >= 100 ){
                tracker.gotExpired = true
                tracker.isUsed = true
            }
            updateTracker(tracker)
    }

    private fun markTrackerAsUsedBasedOnProgress(progressValue : Float, tracker: Tracker){
        when {
            progressValue >=100->{
                tracker.gotExpired = true
            }
            progressValue >= 80 && progressValue <100 -> {
                tracker.usedNearExpiry = true
            }
            progressValue < 80 && progressValue >= 50 -> {
                tracker.usedWhileOk = true
            }
            progressValue < 50 -> {
                tracker.usedWhileFresh = true
            }
        }
        tracker.isUsed = true
        updateTracker(tracker)
    }

    private fun setReminderOnValue(tracker: Tracker, isChecked: Boolean) {
        if (isChecked) {
            tracker.reminderDate?.let {
                NotificationUtil.setReminderForProducts(
                    it,
                    requireContext(),
                    tracker.trackerId
                )
            }
            Toast.makeText(requireContext(),"Reminder on", Toast.LENGTH_SHORT).show()
        } else {
            tracker.reminderDate?.let {
                NotificationUtil.removeReminderForProduct(
                    requireContext(),
                    tracker.trackerId
                )
            }
            Toast.makeText(requireContext(),"Reminder off", Toast.LENGTH_SHORT).show()
        }
        tracker.apply {
            reminderOn = isChecked
        }
        updateTracker(tracker)
    }

    private fun updateTracker(tracker : Tracker){
        trackerViewModel.updateTracker(tracker)
    }


    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        tempDateTime = LocalDateTime(
            year = year,
            monthNumber = month,
            dayOfMonth = dayOfMonth,
            hour = 0,
            minute = 0
        )
        val currentTime = LocalDateTime.now()
        when (pickedFor) {
            PickingFor.REMINDER -> {
                TimePickerDialog(requireContext(), this, currentTime.hour, currentTime.minute, false).show()
            }
            PickingFor.EXPIRY -> {
                updateTracker(trackerViewModel.getTrackerById(navArgs.selectedTrackerId).tracker.apply { expiryDate = tempDateTime })
                bind.expiryDateValue.text = tempDateTime?.let {
                    getString(R.string.date_string_with_month_name, it.month.name.substring(0,3).uppercase(),it.dayOfMonth,it.year)
                }
            }
            else -> {
                updateTracker(trackerViewModel.getTrackerById(navArgs.selectedTrackerId).tracker.apply { mfgDate = tempDateTime })
                bind.manufactureDateValue.text = tempDateTime?.let {
                    getString(R.string.date_string_with_month_name, it.month.name.substring(0,3).uppercase(),it.dayOfMonth,it.year)
                }
            }
        }
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
         tempDateTime = LocalDateTime(
             year = tempDateTime!!.year,
             monthNumber = tempDateTime!!.monthNumber,
             dayOfMonth = tempDateTime!!.dayOfMonth,
             hour = hourOfDay,
             minute = minute
         )
        updateTracker(trackerViewModel.getTrackerById(navArgs.selectedTrackerId).tracker.apply {
            reminderDate = tempDateTime
            reminderOn = true
        })
        bind.reminderOnOffToggle.isChecked = true
        bind.reminderDateValue.text = tempDateTime?.let {
            getString(
                R.string.date_string_with_month_name_and_time_2_lined,
                Month.of(it.monthNumber).name.substring(0, 3).uppercase(),
                it.dayOfMonth,
                it.year,
                if(it.hour in 1..8) "0".plus(it.hour.toString()) else if (it.hour == 0) "00" else it.hour.toString(),
                if(it.minute in 1..8) "0".plus(it.minute.toString()) else if (it.minute == 0) "00" else it.minute.toString(),
                if (it.hour>=12) {"PM"} else {"AM"}
            )
        }
    }

}