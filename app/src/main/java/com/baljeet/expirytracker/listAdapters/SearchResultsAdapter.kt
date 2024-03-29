package com.baljeet.expirytracker.listAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.setPadding
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baljeet.expirytracker.data.Category
import com.baljeet.expirytracker.data.viewmodels.ImageViewModel
import com.baljeet.expirytracker.databinding.ItemOptionBinding
import com.baljeet.expirytracker.interfaces.OnCategorySelected
import com.baljeet.expirytracker.util.ImageConvertor

class SearchResultsAdapter(
    private val context: Context ,
    private val imageVM : ImageViewModel,
    private val listener : OnCategorySelected
): ListAdapter<Category, SearchResultsAdapter.MyViewHolder>(DiffUtil()) {

    inner class MyViewHolder(val bind : ItemOptionBinding) : RecyclerView.ViewHolder(bind.root)

    class DiffUtil : androidx.recyclerview.widget.DiffUtil.ItemCallback<Category>(){
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.categoryId == newItem.categoryId
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
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
            optionTitle.text = category.categoryName
            val image = imageVM.getImageById(category.imageId)
            when(image.mimeType){
                "asset"->{
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
                }
                else->{
                    optionImage.setImageBitmap(
                        ImageConvertor.stringToBitmap(image.bitmap)
                    )
                    optionImage.setPadding(0)
                }
            }
            optionLayout.setOnClickListener { listener.openInfoOfCategory(category) }
        }
    }

}