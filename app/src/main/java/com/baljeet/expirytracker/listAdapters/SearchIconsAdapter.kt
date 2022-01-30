package com.baljeet.expirytracker.listAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.setPadding
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baljeet.expirytracker.data.Image
import com.baljeet.expirytracker.databinding.ItemOptionBinding
import com.baljeet.expirytracker.interfaces.OnIconSelected

class SearchIconsAdapter (
    private val context: Context ,
    private val listener : OnIconSelected
): ListAdapter<Image, SearchIconsAdapter.MyViewHolder>(DiffUtil()) {

    inner class MyViewHolder(val bind : ItemOptionBinding) : RecyclerView.ViewHolder(bind.root)

    class DiffUtil : androidx.recyclerview.widget.DiffUtil.ItemCallback<Image>(){
        override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean {
            return oldItem.imageId == newItem.imageId
        }

        override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean {
            return oldItem == newItem
        }

    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchIconsAdapter.MyViewHolder {
        return   MyViewHolder(ItemOptionBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: SearchIconsAdapter.MyViewHolder, position: Int) {
        val image = getItem(position)
        holder.bind.apply {
            optionTitle.visibility = View.GONE

            optionImage.setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    context.resources.getIdentifier(
                        image.imageUrl,
                        "drawable",
                        context.packageName
                    )
                )
            )
            optionImage.setPadding(30)
            optionLayout.setOnClickListener { listener.selectThisIcon(image) }
        }
    }
}