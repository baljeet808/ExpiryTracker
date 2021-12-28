package com.baljeet.expirytracker.listAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.relations.TrackerAndProduct
import com.baljeet.expirytracker.databinding.SummaryViewItemBinding


class SummaryDiffAdapter(private val context: Context) : ListAdapter<TrackerAndProduct,SummaryDiffAdapter.MyViewHolder>(DiffUtil()) {
    class MyViewHolder(val bind: SummaryViewItemBinding) : RecyclerView.ViewHolder(bind.root)

    class DiffUtil : androidx.recyclerview.widget.DiffUtil.ItemCallback<TrackerAndProduct>(){
        override fun areItemsTheSame(oldItem: TrackerAndProduct, newItem: TrackerAndProduct): Boolean {
            return oldItem.tracker.trackerId == newItem.tracker.trackerId
        }

        override fun areContentsTheSame(oldItem: TrackerAndProduct, newItem: TrackerAndProduct): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):MyViewHolder {
        return MyViewHolder(SummaryViewItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val tracker = getItem(position)
        holder.bind.apply {
            srNumber.text = (position+1).toString()
            productName.text = tracker.productAndCategoryAndImage.product.name
            productCategory.text = tracker.productAndCategoryAndImage.categoryAndImage.category.categoryName
            if(tracker.tracker.usedWhileFresh){
                trackingResult.text = context.getString(R.string.freshly_used)
            }
            if(tracker.tracker.usedWhileOk){
                trackingResult.text = context.getString(R.string.good_condition)
            }
            if(tracker.tracker.usedNearExpiry){
                trackingResult.text = context.getString(R.string.used_near_expiry)
            }
            if(tracker.tracker.gotExpired){
                trackingResult.text =context.getString(R.string.expired)
            }

            productExpiry.text = tracker.tracker.expiryDate.date.toString()
            productMfg.text = tracker.tracker.mfgDate.date.toString()

        }
    }
}