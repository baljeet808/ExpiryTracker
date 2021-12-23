package com.baljeet.expirytracker.listAdapters

import android.content.Context
import android.graphics.ColorFilter
import android.graphics.drawable.LayerDrawable
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.Tracker
import com.baljeet.expirytracker.data.relations.TrackerAndProduct
import com.baljeet.expirytracker.databinding.DashboardRecyclerItemViewBinding
import com.baljeet.expirytracker.interfaces.UpdateTrackerListener
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.periodUntil
import kotlinx.datetime.toInstant
import java.time.Month

class TrackerDiffAdapter(private val context : Context, private val updateTrackerListener :UpdateTrackerListener) : ListAdapter<TrackerAndProduct,TrackerDiffAdapter.MyViewHolder>(DiffUtil()) {

    private var isDeleteActionSelected = true

    class DiffUtil : androidx.recyclerview.widget.DiffUtil.ItemCallback<TrackerAndProduct>(){
        override fun areItemsTheSame(oldItem: TrackerAndProduct, newItem: TrackerAndProduct): Boolean {
            return oldItem.tracker.trackerId == newItem.tracker.trackerId
        }

        override fun areContentsTheSame(oldItem: TrackerAndProduct, newItem: TrackerAndProduct): Boolean {
            return oldItem == newItem
        }

    }

    class MyViewHolder(val bind: DashboardRecyclerItemViewBinding) :
        RecyclerView.ViewHolder(bind.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(DashboardRecyclerItemViewBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val tracker = getItem(position)
        holder.bind.apply {
            productName.text = tracker.productAndCategoryAndImage.product.name
            productImage.setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    context.resources.getIdentifier(
                        tracker.productAndCategoryAndImage.image.imageUrl,
                        "drawable",
                        context.packageName
                    )
                )
            )
            categoryImage.setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    context.resources.getIdentifier(
                        tracker.productAndCategoryAndImage.categoryAndImage.image.imageUrl,
                        "drawable",
                        context.packageName
                    )
                )
            )

            val expiryDate = tracker.tracker.expiryDate
            val mfgDate = tracker.tracker.mfgDate
            expiryDate.let {
                expiryDateText.text = context.resources.getString(
                    R.string.expiry_date_var,
                    Month.of(it.monthNumber).name.substring(0, 3),
                    it.dayOfMonth,
                    it.year
                )
            }
            val dateToday = Clock.System.now()

            val mfgInstant = mfgDate.toInstant(TimeZone.UTC)
            val expiryInstant = expiryDate.toInstant(TimeZone.UTC)

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
                    progressValue >= 80 -> {
                        progressDrawableClip.setTint(context.getColor(R.color.progress_bad))
                    }
                    progressValue < 80 && progressValue >= 50 -> {
                        progressDrawableClip.setTint(context.getColor(R.color.progress_ok))
                    }
                    progressValue < 50 -> {
                        progressDrawableClip.setTint(context.getColor(R.color.progress_great))
                    }
                }
            }

            itemProgressbar.progress  = progressValue.toInt()

                optionCard.setOnClickListener {
                    buttonsLayout.isGone = !buttonsLayout.isGone
                }

                favoriteButton.isChecked = tracker.tracker.isFavourite
                favoriteButton.setOnClickListener {
                    tracker.tracker.isFavourite = !tracker.tracker.isFavourite
                    favoriteButton.isChecked = tracker.tracker.isFavourite
                    updateTrackerListener.updateTracker(tracker.tracker)
                }
                markUsedButton.setOnClickListener {
                    isDeleteActionSelected = false
                    showUndoCountDown(holder,tracker.tracker,progressValue)
                }
                deleteButton.setOnClickListener {
                    isDeleteActionSelected = true
                    showUndoCountDown(holder,tracker.tracker,progressValue)
                }
                undoButton.setOnClickListener {
                    performUndo()
                    hideCountdown(holder)
                }
        }
    }

    private fun hideCountdown(holder : MyViewHolder){
        holder.bind.apply {
            buttonsLayout.visibility = View.GONE
            undoLayout.visibility = View.GONE
            countDownText.visibility = View.GONE
            itemProgressbar.visibility = View.VISIBLE
            openButton.visibility = View.VISIBLE
            favoriteButton.visibility = View.VISIBLE
            categoryImage.visibility = View.VISIBLE
            deleteButton.visibility = View.VISIBLE
            markUsedButton.visibility = View.VISIBLE
        }
    }

    private fun performUndo(){
        timer?.cancel()
    }

    private var timer  : CountDownTimer? = null
    private fun showUndoCountDown(holder : MyViewHolder, tracker : Tracker, progressValue: Float){
        var sec = 4
        holder.bind.apply {
            undoLayout.visibility = View.VISIBLE
            countDownText.visibility = View.VISIBLE
            itemProgressbar.visibility = View.INVISIBLE
            openButton.visibility = View.GONE
            favoriteButton.visibility = View.GONE
            categoryImage.visibility = View.INVISIBLE
            deleteButton.visibility = View.GONE
            markUsedButton.visibility = View.GONE
            timer?.cancel()
            timer = object : CountDownTimer(2400, 800) {
                override fun onTick(millisUntilFinished: Long) {
                    --sec
                    countDownText.text = sec.toString()
                }
                override fun onFinish() {
                    performAction(tracker, progressValue, holder)
                }
            }
            (timer as CountDownTimer).start()
        }
    }

    private fun performAction(tracker : Tracker, progressValue: Float,holder : MyViewHolder){
            if(isDeleteActionSelected) {
                tracker.isArchived = true
                if(progressValue >= 100 ){
                    tracker.gotExpired = true
                    tracker.isUsed = true
                }
                updateTrackerListener.updateTracker(tracker)
            }else{
                markTrackerAsUsedBasedOnProgress(progressValue,tracker)
            }
            hideCountdown(holder)
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
        updateTrackerListener.updateTracker(tracker)
    }

}