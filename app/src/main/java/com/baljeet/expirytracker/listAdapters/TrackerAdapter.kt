package com.baljeet.expirytracker.listAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.Tracker
import com.baljeet.expirytracker.data.relations.TrackerAndProduct
import com.google.android.material.imageview.ShapeableImageView
import java.time.Month
import java.util.*
import kotlin.collections.ArrayList


class TrackerAdapter(private val trackerList : ArrayList<TrackerAndProduct>,
                     private val context: Context) : RecyclerView.Adapter<TrackerAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ):MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dashboard_recycler_item_view,parent,false)

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val tracker = trackerList[position]
        holder.productName.text = tracker.productAndCategoryAndImage.product.name
        holder.productImage.setImageDrawable(
            AppCompatResources.getDrawable(
                context,
                context.resources.getIdentifier(
                    tracker.productAndCategoryAndImage.image.imageUrl,
                    "drawable",
                    context.packageName
                )
            )
        )
        holder.categoryImage.setImageDrawable(
            AppCompatResources.getDrawable(
                context,
                context.resources.getIdentifier(
                    tracker.productAndCategoryAndImage.categoryAndImage.image.imageUrl,
                    "drawable",
                    context.packageName
                )
            )
        )
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        cal.time = Date(tracker.tracker.mfgDate!!)
        holder.mfgDate.text = context.resources.getString(R.string.manufactured_date_var,
            Month.of(cal.get(Calendar.MONTH)+1).name.substring(0,3),
            cal.get(Calendar.DAY_OF_MONTH),
            cal.get(Calendar.YEAR))

        val cal2 = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        cal2.time = Date(tracker.tracker.expiryDate!!)
        holder.expiryDate.text = context.resources.getString(R.string.expiry_date_var,
            Month.of(cal2.get(Calendar.MONTH)+1).name.substring(0,3),
            cal2.get(Calendar.DAY_OF_MONTH),
            cal2.get(Calendar.YEAR))

    }

    override fun getItemCount(): Int {
        return trackerList.size
    }
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage : ImageView = itemView.findViewById(R.id.product_image)
        val categoryImage : ShapeableImageView = itemView.findViewById(R.id.category_image)
        val productName : TextView = itemView.findViewById(R.id.product_name)
        val expiryDate : TextView = itemView.findViewById(R.id.expiry_date)
        val mfgDate : TextView = itemView.findViewById(R.id.mfg_date)
    }
}