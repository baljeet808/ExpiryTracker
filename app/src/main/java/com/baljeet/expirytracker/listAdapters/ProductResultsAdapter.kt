package com.baljeet.expirytracker.listAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.setPadding
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baljeet.expirytracker.data.relations.ProductAndImage
import com.baljeet.expirytracker.databinding.ItemOptionBinding
import com.baljeet.expirytracker.util.ImageConvertor

class ProductResultsAdapter(
    private val context: Context
): ListAdapter<ProductAndImage, ProductResultsAdapter.MyViewHolder>(ProductResultsAdapter.DiffUtil()) {

    inner class MyViewHolder(val bind : ItemOptionBinding) : RecyclerView.ViewHolder(bind.root)

    class DiffUtil : androidx.recyclerview.widget.DiffUtil.ItemCallback<ProductAndImage>(){
        override fun areItemsTheSame(oldItem: ProductAndImage, newItem: ProductAndImage): Boolean {
            return oldItem.product.productId == newItem.product.productId
        }

        override fun areContentsTheSame(oldItem: ProductAndImage, newItem: ProductAndImage): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductResultsAdapter.MyViewHolder {
        return   MyViewHolder(ItemOptionBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ProductResultsAdapter.MyViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind.apply {
            optionTitle.text = product.product.name
            when(product.image.mimeType){
                "asset"->{
                    optionImage.setImageDrawable(
                        AppCompatResources.getDrawable(
                            context,
                            context.resources.getIdentifier(
                                product.image.imageUrl,
                                "drawable",
                                context.packageName
                            )
                        )
                    )
                    optionImage.setPadding(30)
                }
                else->{
                    optionImage.setImageBitmap(
                        ImageConvertor.stringToBitmap(product.image.bitmap)
                    )
                    optionImage.setPadding(0)
                }
            }
        }
    }

}