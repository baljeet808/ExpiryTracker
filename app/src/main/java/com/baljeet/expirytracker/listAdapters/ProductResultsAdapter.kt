package com.baljeet.expirytracker.listAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.setPadding
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baljeet.expirytracker.data.Product
import com.baljeet.expirytracker.data.relations.ProductAndImage
import com.baljeet.expirytracker.data.viewmodels.ImageViewModel
import com.baljeet.expirytracker.databinding.ItemOptionBinding
import com.baljeet.expirytracker.interfaces.OnProductSelected
import com.baljeet.expirytracker.util.ImageConvertor

class ProductResultsAdapter(
    private val context: Context ,
    private val imageVM : ImageViewModel,
    private val listener : OnProductSelected
): ListAdapter<Product, ProductResultsAdapter.MyViewHolder>(DiffUtil()) {

    inner class MyViewHolder(val bind : ItemOptionBinding) : RecyclerView.ViewHolder(bind.root)

    class DiffUtil : androidx.recyclerview.widget.DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
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
        val image = imageVM.getImageById(product.imageId)
        holder.bind.apply {
            optionTitle.text = product.name
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
            optionLayout.setOnClickListener { listener.openInfoOfProduct(product) }
        }
    }

}