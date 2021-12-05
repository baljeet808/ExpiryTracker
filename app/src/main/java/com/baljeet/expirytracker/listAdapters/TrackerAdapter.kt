package com.baljeet.expirytracker.listAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.relations.TrackerAndProduct
import com.baljeet.expirytracker.databinding.DashboardRecyclerItemViewBinding
import com.dwellify.contractorportal.util.TimeConvertor
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import java.time.Month
import java.util.*


class TrackerAdapter(
    private val trackerList: ArrayList<TrackerAndProduct>,
    private val context: Context
) : RecyclerView.Adapter<TrackerAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        return MyViewHolder(
            DashboardRecyclerItemViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val tracker = trackerList[position]
        holder.bind.productName.text = tracker.productAndCategoryAndImage.product.name
        holder.bind.productImage.setImageDrawable(
            AppCompatResources.getDrawable(
                context,
                context.resources.getIdentifier(
                    tracker.productAndCategoryAndImage.image.imageUrl,
                    "drawable",
                    context.packageName
                )
            )
        )
        holder.bind.categoryImage.setImageDrawable(
            AppCompatResources.getDrawable(
                context,
                context.resources.getIdentifier(
                    tracker.productAndCategoryAndImage.categoryAndImage.image.imageUrl,
                    "drawable",
                    context.packageName
                )
            )
        )

        val expiryDate =
            tracker.tracker.expiryDate
        expiryDate?.let {
            holder.bind.expiryDate.text = context.resources.getString(
                R.string.expiry_date_var,
                Month.of(it.monthNumber).name.substring(0, 3),
                it.dayOfMonth,
                it.year
            )
        }
        val dateToday = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val today = LocalDate(dateToday.year,dateToday.monthNumber,dateToday.dayOfMonth)
        val mfgInstant = tracker.tracker.mfgDate!!
        val expiryInstant =
            tracker.tracker.expiryDate!!

        val totalPeriod = mfgInstant.periodUntil(expiryInstant)
        val periodSpent = mfgInstant.periodUntil(today)

        val totalHours = totalPeriod.days * 24 + totalPeriod.hours
        val spentHours = periodSpent.days * 24 + periodSpent.hours

        val progressValue =
            (spentHours.toFloat() / totalHours.toFloat()) * 100

        holder.bind.itemProgressbar.apply {
            when {
                progressValue >= 80 -> {
                    progressDrawable =
                        AppCompatResources.getDrawable(context, R.drawable.pb_red_drawable)
                }
                progressValue < 80 && progressValue >= 50 -> {
                    progressDrawable =
                        AppCompatResources.getDrawable(context, R.drawable.pb_yellow_drawable)
                }
                progressValue < 50 -> {
                    progressDrawable =
                        AppCompatResources.getDrawable(context, R.drawable.pb_green_drawable)
                }
            }
            progress = progressValue.toInt()
        }

        holder.bind.apply {
            optionCard.setOnClickListener {
                buttonsLayout.isGone = !buttonsLayout.isGone
            }
        }

    }

    override fun getItemCount(): Int {
        return trackerList.size
    }

    class MyViewHolder(val bind: DashboardRecyclerItemViewBinding) :
        RecyclerView.ViewHolder(bind.root)
}

