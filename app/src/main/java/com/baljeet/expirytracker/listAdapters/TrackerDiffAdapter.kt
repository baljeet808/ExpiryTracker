package com.baljeet.expirytracker.listAdapters

import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.LayerDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isGone
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.Tracker
import com.baljeet.expirytracker.data.relations.TrackerAndProduct
import com.baljeet.expirytracker.databinding.DashboardRecyclerItemViewBinding
import com.baljeet.expirytracker.interfaces.OnTrackerOpenListener
import com.baljeet.expirytracker.interfaces.UpdateTrackerListener
import com.baljeet.expirytracker.util.ImageConvertor
import com.baljeet.expirytracker.util.SharedPref
import com.dwellify.contractorportal.util.TimeConvertor
import java.time.Duration
import java.time.LocalDateTime

class TrackerDiffAdapter(private val context : Context,
                         private val updateTrackerListener :UpdateTrackerListener,
                         private val openListener : OnTrackerOpenListener
                         ) : ListAdapter<TrackerAndProduct,TrackerDiffAdapter.MyViewHolder>(DiffUtil()) {


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
            when(tracker.productAndCategoryAndImage.image.mimeType){
                "asset"->{
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
                }
                else->{
                    productImage.setImageBitmap(
                        ImageConvertor.stringToBitmap(tracker.productAndCategoryAndImage.image.bitmap)
                    )
                }
            }
            when(tracker.productAndCategoryAndImage.categoryAndImage.image.mimeType){
                "asset" ->{
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
                }
                else->{
                    categoryImage.setImageBitmap(
                        ImageConvertor.stringToBitmap(tracker.productAndCategoryAndImage.categoryAndImage.image.bitmap)
                    )
                }
            }


            val expiryDate = tracker.tracker.expiryDate
            val mfgDate = tracker.tracker.mfgDate
            expiryDate?.let {
                expiringMonthAndDay.text = context.getString(R.string.date_short_var,it.dayOfMonth,it.month.name.substring(0,3).uppercase())
                expiringYear.text = it.year.toString()
            }
            val dateToday = LocalDateTime.now()

            val totalHours = Duration.between(mfgDate,expiryDate).toMinutes()
            val spentHours = Duration.between(mfgDate,dateToday).toMinutes()

            val progressValue =
                (spentHours.toFloat() / totalHours.toFloat()) * 100
            itemProgressbar.apply {
                val layerDrawable = progressDrawable as LayerDrawable
                val progressDrawableClip = layerDrawable.getDrawable(1)
                when {
                    progressValue >= 100->{
                        progressDrawableClip.setTint(context.getColor(R.color.progress_bad))
                        trackingStatus.text = context.getText(R.string.expired)
                    }
                    progressValue >= 80 -> {
                        progressDrawableClip.setTint(context.getColor(R.color.red_orange))
                        trackingStatus.text = context.getText(R.string.still_ok)
                    }
                    progressValue < 80 && progressValue >= 50 -> {
                        progressDrawableClip.setTint(context.getColor(R.color.progress_ok))
                        trackingStatus.text = context.getText(R.string.good)
                    }
                    progressValue < 50 -> {
                        progressDrawableClip.setTint(context.getColor(R.color.progress_great))
                        trackingStatus.text = context.getText(R.string.fresh)
                    }
                }
            }

            itemProgressbar.progress  = progressValue.toInt()

                optionCard.setOnClickListener {
                    buttonsLayout.isGone = !buttonsLayout.isGone
                    expiringMonthAndDay.isGone = !expiringMonthAndDay.isGone
                    expiringYear.isGone = !expiringYear.isGone
                }

                favoriteButton.isChecked = tracker.tracker.isFavourite
                favoriteButton.setOnClickListener {
                    tracker.tracker.isFavourite = !tracker.tracker.isFavourite
                    favoriteButton.isChecked = tracker.tracker.isFavourite
                    updateTrackerListener.updateTracker(tracker.tracker)
                }
            openButton.setOnClickListener { openListener.openTrackerInfo(tracker) }
                markUsedButton.setOnClickListener {
                    performAction(tracker.tracker,progressValue, false)
                }
                deleteButton.setOnClickListener {
                    performAction(tracker.tracker,progressValue, true)
                }
            reminderDate?.let {
                if(tracker.tracker.reminderOn){
                    it.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(context,R.drawable.ic_notifications),null,null,null)
                }else{
                    it.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(context,R.drawable.ic_notifications_off),null,null,null)
                }
                tracker.tracker.reminderDate?.let {date->
                    it.text = context.getString(R.string.date_string_with_month_name_last_and_time_2_lined,
                        date.dayOfMonth,
                        date.month.name.substring(0,3).uppercase(),
                        date.year,
                        TimeConvertor.getTime(date.hour,date.minute,true)
                        )
                }
            }
            mfgDateText?.let {  date->
                if(SharedPref.usingTab){
                    if( context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        mfgDateText.isGone = false
                        tracker.tracker.mfgDate?.let { mfg ->
                            date.text = context.getString(
                                R.string.mfg_date_string_with_month_name,
                                mfg.dayOfMonth,
                                mfg.month.name.substring(0, 3).uppercase(),
                                mfg.year
                            )
                        } ?: kotlin.run {
                            date.text = context.getString(R.string.no_date)
                        }
                    }else{
                        mfgDateText.isGone = true
                    }
                }
            }
        }
    }
    
    private fun performAction(tracker : Tracker, progressValue: Float, performDelete : Boolean){
            if(performDelete) {
                tracker.isArchived = true
                if(progressValue >= 100 ){
                    tracker.gotExpired = true
                    tracker.isUsed = true
                    tracker.usedDate = LocalDateTime.now()
                }
                updateTrackerListener.updateTracker(tracker)
            }else{
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
                tracker.usedDate = LocalDateTime.now()
                updateTrackerListener.updateTracker(tracker)
            }
    }
}