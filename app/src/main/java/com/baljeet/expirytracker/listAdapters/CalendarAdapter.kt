package com.baljeet.expirytracker.listAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.databinding.DayItemBinding
import com.baljeet.expirytracker.model.DayWithProducts


class CalendarAdapter(
    private val daysWithProducts: ArrayList<DayWithProducts>,
    private val context: Context,
    private val dateSelectedListener: OnDateSelectedListener
) : RecyclerView.Adapter<CalendarAdapter.MyViewHolder>() {
    class MyViewHolder(val bind: DayItemBinding) : RecyclerView.ViewHolder(bind.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        return MyViewHolder(
            DayItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val itemData = daysWithProducts[position]
        itemData.date?.let {
            holder.bind.apply {
                root.setOnClickListener { dateSelectedListener.openSelectedDate(itemData) }
                dayText.text = itemData.date?.dayOfMonth.toString()
                itemData.products?.let {
                    if (it.isEmpty()) {
                        itemsCountImage.visibility = View.INVISIBLE
                    } else if (it.size == 1) {
                        itemsCountImage.visibility = View.VISIBLE
                        itemsCountImage.setImageDrawable(
                            AppCompatResources.getDrawable(
                                context,
                                R.drawable.ic_single_item
                            )
                        )
                    } else if (it.size == 2) {
                        itemsCountImage.visibility = View.VISIBLE
                        itemsCountImage.setImageDrawable(
                            AppCompatResources.getDrawable(
                                context,
                                R.drawable.ic_double_items
                            )
                        )
                    } else if (it.size > 2) {
                        itemsCountImage.visibility = View.VISIBLE
                        itemsCountImage.setImageDrawable(
                            AppCompatResources.getDrawable(
                                context,
                                R.drawable.ic_multiple_items
                            )
                        )
                    }
                }
            }
        }?: kotlin.run {
            holder.bind.apply {
                dayText.visibility = View.INVISIBLE
                itemsCountImage.visibility = View.INVISIBLE
            }
        }
    }

    override fun getItemCount(): Int {
        return daysWithProducts.size
    }

    interface OnDateSelectedListener {
        fun openSelectedDate(dayWithProducts: DayWithProducts)
    }
}