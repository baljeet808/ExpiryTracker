package com.baljeet.expirytracker.listAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.setPadding
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.relations.TrackerAndProduct
import com.baljeet.expirytracker.databinding.ReportListItemBinding
import com.baljeet.expirytracker.interfaces.ShowImagePreview
import com.baljeet.expirytracker.util.ImageConvertor


class SummaryDiffAdapter(private val context: Context, private val showImage : ShowImagePreview) : ListAdapter<TrackerAndProduct,SummaryDiffAdapter.MyViewHolder>(DiffUtil()) {
    class MyViewHolder(val bind: ReportListItemBinding) : RecyclerView.ViewHolder(bind.root)

    class DiffUtil : androidx.recyclerview.widget.DiffUtil.ItemCallback<TrackerAndProduct>(){
        override fun areItemsTheSame(oldItem: TrackerAndProduct, newItem: TrackerAndProduct): Boolean {
            return oldItem.tracker.trackerId == newItem.tracker.trackerId
        }

        override fun areContentsTheSame(oldItem: TrackerAndProduct, newItem: TrackerAndProduct): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):MyViewHolder {
        return MyViewHolder(ReportListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val tracker = getItem(position)
        holder.bind.apply {
            productName.text = tracker.productAndCategoryAndImage.product.name
            productCategory?.text = tracker.productAndCategoryAndImage.categoryAndImage.category.categoryName
            if(tracker.tracker.usedWhileFresh){
                trackingResult.text = context.getString(R.string.freshly_used)
                trackingResult.setTextColor(context.getColor(R.color.soft_green))
                statusColor.setBackgroundColor(context.getColor(R.color.soft_green))
            }
            if(tracker.tracker.usedWhileOk){
                trackingResult.text = context.getString(R.string.good_condition)
                trackingResult.setTextColor(context.getColor(R.color.soft_yellow))
                statusColor.setBackgroundColor(context.getColor(R.color.soft_yellow))
            }
            if(tracker.tracker.usedNearExpiry){
                trackingResult.text = context.getString(R.string.used_near_expiry_2)
                trackingResult.setTextColor(context.getColor(R.color.red_orange))
                statusColor.setBackgroundColor(context.getColor(R.color.red_orange))
            }
            if(tracker.tracker.gotExpired){
                trackingResult.text =context.getString(R.string.expired)
                trackingResult.setTextColor(context.getColor(R.color.soft_red))
                statusColor.setBackgroundColor(context.getColor(R.color.soft_red))
            }

            expiryDate?.text = tracker.tracker.expiryDate?.let {
                context.getString(R.string.expiry_date_var_1,
                    it.month.name.substring(0,3).uppercase(),
                    it.dayOfMonth,
                    it.year
                    )
            }
            mfgDate?.text = tracker.tracker.mfgDate?.let {
                context.getString(R.string.mfg_date_var_1,
                    it.month.name.substring(0,3).uppercase(),
                    it.dayOfMonth,
                    it.year
                )
            }

            usedDate.text = tracker.tracker.usedDate?.let {
                context.getString(R.string.used_date_var,
                    it.month.name.substring(0,3).uppercase(),
                    it.dayOfMonth,
                    it.year
                )
            }

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
                    productImage.setPadding(30)
                }
                else->{
                    productImage.setImageBitmap(
                        ImageConvertor.stringToBitmap(tracker.productAndCategoryAndImage.image.bitmap)
                    )
                    productImage.setOnClickListener { showImage.openImagePreviewFor(tracker.productAndCategoryAndImage.image) }
                    productImage.setPadding(0)
                }
            }

        }
    }
}