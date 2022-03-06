package com.baljeet.expirytracker.listAdapters

import android.content.Context
import android.graphics.drawable.LayerDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.relations.TrackerAndProduct
import com.baljeet.expirytracker.databinding.WidgetStackViewItemBinding
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.periodUntil
import kotlinx.datetime.toInstant


class TrackerDemoAdapter(private val context: Context) : ListAdapter<TrackerAndProduct, TrackerDemoAdapter.MyViewHolder>(DiffUtil()){

    class DiffUtil : androidx.recyclerview.widget.DiffUtil.ItemCallback<TrackerAndProduct>(){
        override fun areItemsTheSame(oldItem: TrackerAndProduct, newItem: TrackerAndProduct): Boolean {
            return oldItem.tracker.trackerId == newItem.tracker.trackerId
        }

        override fun areContentsTheSame(oldItem: TrackerAndProduct, newItem: TrackerAndProduct): Boolean {
            return oldItem == newItem
        }
    }

    class MyViewHolder(val bind : WidgetStackViewItemBinding) : RecyclerView.ViewHolder(bind.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ):MyViewHolder {
        return MyViewHolder(WidgetStackViewItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
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

            val expiryDate = tracker.tracker.expiryDate
            val mfgDate = tracker.tracker.mfgDate
            expiryDate?.let {
                expiringDate.text = context.getString(R.string.date_short_var,it.dayOfMonth,it.month.name.substring(0,3).uppercase())
                expiringDateYear.text = it.year.toString()
            }
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
                        status.text = context.getText(R.string.expired)
                    }
                    progressValue >= 80 -> {
                        progressDrawableClip.setTint(context.getColor(R.color.progress_bad))
                        status.text = context.getText(R.string.still_ok)
                    }
                    progressValue < 80 && progressValue >= 50 -> {
                        progressDrawableClip.setTint(context.getColor(R.color.progress_ok))
                        status.text = context.getText(R.string.good)
                    }
                    progressValue < 50 -> {
                        progressDrawableClip.setTint(context.getColor(R.color.progress_great))
                        status.text = context.getText(R.string.fresh)
                    }
                }
            }
            itemProgressbar.progress  = progressValue.toInt()
        }
    }
}