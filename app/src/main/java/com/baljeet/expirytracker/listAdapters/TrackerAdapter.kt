package com.baljeet.expirytracker.listAdapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.relations.TrackerAndProduct
import com.baljeet.expirytracker.databinding.DashboardRecyclerItemViewBinding
import com.dwellify.contractorportal.util.TimeConvertor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.periodUntil
import java.time.Month
import java.util.*


class TrackerAdapter(private val trackerList : ArrayList<TrackerAndProduct>,
                     private val context: Context) : RecyclerView.Adapter<TrackerAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ):MyViewHolder {
        return MyViewHolder(DashboardRecyclerItemViewBinding.inflate(LayoutInflater.from(parent.context),parent,false))
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

        val expiryDate =tracker.tracker.expiryDate?.let { TimeConvertor.fromEpochMillisecondsToLocalDateTime(it) }
        Log.d("Log for - mfg date ","${TimeConvertor.fromEpochMillisecondsToLocalDateTime(tracker.tracker.mfgDate)}")
        expiryDate?.let {
            Log.d("Log expiry date of ${tracker.productAndCategoryAndImage.product.name} - ","$it")
           holder.bind.expiryDate.text = context.resources.getString(R.string.expiry_date_var,
                Month.of(it.monthNumber).name.substring(0,3),
                it.dayOfMonth,
                it.year)
        }
        CoroutineScope(Dispatchers.Main).launch {
            val today = Clock.System.now()
            val mfgInstant = TimeConvertor.fromEpochMillisecondsToInstant(tracker.tracker.mfgDate!!)
            val expiryInstant = TimeConvertor.fromEpochMillisecondsToInstant(tracker.tracker.expiryDate!!)

            val totalPeriod = mfgInstant.periodUntil(expiryInstant, TimeZone.UTC).days+1
            val periodSpent = mfgInstant.periodUntil(today, TimeZone.UTC).days
            val progressValue = (periodSpent.toFloat()/totalPeriod.toFloat())*100
            Log.d("Log for - product ", tracker.productAndCategoryAndImage.product.name)
            Log.d("Log for - total period  ","$totalPeriod")
            Log.d("Log for - spent period  ","$periodSpent")
            Log.d("Log for - progress  ","$progressValue")
            holder.bind.itemProgressbar.apply {
                when {
                    progressValue >= 80 -> {
                        progressDrawable = AppCompatResources.getDrawable(context,R.drawable.pb_red_drawable)
                    }
                    progressValue < 80 && progressValue >= 50 ->{
                        progressDrawable = AppCompatResources.getDrawable(context,R.drawable.pb_yellow_drawable)
                    }
                    progressValue < 50 ->{
                        progressDrawable = AppCompatResources.getDrawable(context,R.drawable.pb_green_drawable)
                    }
                }
                progress = progressValue.toInt()
            }
        }
    }

    override fun getItemCount(): Int {
        return trackerList.size
    }
    class MyViewHolder(val bind : DashboardRecyclerItemViewBinding) : RecyclerView.ViewHolder(bind.root)
}