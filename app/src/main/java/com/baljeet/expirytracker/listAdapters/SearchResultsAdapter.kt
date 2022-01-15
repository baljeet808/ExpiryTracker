package com.baljeet.expirytracker.listAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.setPadding
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baljeet.expirytracker.data.relations.CategoryAndImage
import com.baljeet.expirytracker.data.relations.TrackerAndProduct
import com.baljeet.expirytracker.databinding.ItemOptionBinding
import com.baljeet.expirytracker.util.ImageConvertor

class SearchResultsAdapter(
    private val context: Context
): ListAdapter<CategoryAndImage, SearchResultsAdapter.MyViewHolder>(SearchResultsAdapter.DiffUtil()) {

    inner class MyViewHolder(val bind : ItemOptionBinding) : RecyclerView.ViewHolder(bind.root)

    class DiffUtil : androidx.recyclerview.widget.DiffUtil.ItemCallback<CategoryAndImage>(){
        override fun areItemsTheSame(oldItem: CategoryAndImage, newItem: CategoryAndImage): Boolean {
            return oldItem.category.categoryId == newItem.category.categoryId
        }

        override fun areContentsTheSame(oldItem: CategoryAndImage, newItem: CategoryAndImage): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchResultsAdapter.MyViewHolder {
        return   MyViewHolder(ItemOptionBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: SearchResultsAdapter.MyViewHolder, position: Int) {
        val category = getItem(position)
        holder.bind.apply {
            optionTitle.text = category.category.categoryName
            when(category.image.mimeType){
                "asset"->{
                    optionImage.setImageDrawable(
                        AppCompatResources.getDrawable(
                            context,
                            context.resources.getIdentifier(
                                category.image.imageUrl,
                                "drawable",
                                context.packageName
                            )
                        )
                    )
                    optionImage.setPadding(30)
                }
                else->{
                    optionImage.setImageBitmap(
                        ImageConvertor.stringToBitmap(category.image.bitmap)
                    )
                    optionImage.setPadding(0)
                }
            }
        }
    }

}