package com.baljeet.expirytracker.listAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.relations.TrackerAndProduct
import com.baljeet.expirytracker.databinding.ReminderListItemBinding
import com.baljeet.expirytracker.interfaces.OnEditReminderTime
import com.baljeet.expirytracker.interfaces.OnReminderCheckedChanged
import com.baljeet.expirytracker.util.ImageConvertor
import com.dwellify.contractorportal.util.TimeConvertor
import kotlinx.datetime.Month


class RemindersAdapter(
    private val context : Context,
    private val toggleListener : OnReminderCheckedChanged,
    private val editListener : OnEditReminderTime
) : ListAdapter<TrackerAndProduct,RemindersAdapter.MyViewHolder>(DiffUtil()) {
    class MyViewHolder(val bind: ReminderListItemBinding) : RecyclerView.ViewHolder(bind.root)

    class DiffUtil : androidx.recyclerview.widget.DiffUtil.ItemCallback<TrackerAndProduct>(){
        override fun areItemsTheSame(oldItem: TrackerAndProduct, newItem: TrackerAndProduct): Boolean {
            return oldItem.tracker.trackerId == newItem.tracker.trackerId
        }
        override fun areContentsTheSame(oldItem: TrackerAndProduct, newItem: TrackerAndProduct): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ):MyViewHolder {
        return MyViewHolder(ReminderListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind.apply {
            getItem(position).let { tracker ->

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

                productName.text = tracker.productAndCategoryAndImage.product.name
                reminderDateTime.text = tracker.tracker.reminderDate?.let {
                    context.getString(
                        R.string.date_string_with_month_name_and_time,
                        Month.of(it.monthNumber).name.substring(0, 3),
                        it.dayOfMonth,
                        it.year,
                        TimeConvertor.getTime(it.hour,it.minute,true)
                    )
                }?: kotlin.run { 
                    context.getString(R.string.no_time)
                }
                onOffToggle.isChecked = tracker.tracker.reminderOn
                onOffToggle.setOnCheckedChangeListener { _, isChecked ->
                     toggleListener.setReminderOnValue(tracker, isChecked)
                }
                editButton.setOnClickListener {
                     editListener.openDateTimePickerToEditReminder(tracker)
                }
            }
        }
    }


}