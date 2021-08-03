package com.baljeet.expirytracker.listAdapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.model.Category
import com.baljeet.expirytracker.model.Product
import com.google.android.material.card.MaterialCardView

class OptionsAdapter(private val categoryList : ArrayList<Category>?,
                     private val context: Context,
                     private var selectedCategory : Int?,
                     private val optionClickListener : OnOptionSelectedListener,
                     private val productList : ArrayList<Product>?) : RecyclerView.Adapter<OptionsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ):MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_option,parent,false)

        return MyViewHolder(view,optionClickListener,context)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.card.strokeWidth = 2
        holder.check.visibility = View.GONE
        holder.title.textSize = 11F
        holder.card.setCardBackgroundColor(context.resources.getColor(R.color.main_background,null))
        categoryList?.let {
            val category = categoryList[position]
            holder.title.text = category.categoryName

            holder.optionImage.setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    context.resources.getIdentifier(
                        category.categoryIcon.imageUrl,
                        "drawable",
                        context.packageName
                    )
                )
            )
        }
        productList?.let {
            val product = productList[position]
            holder.title.text = product.name
            holder.optionImage.setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    context.resources.getIdentifier(
                        product.image?.imageUrl,
                        "drawable",
                        context.packageName
                    )
                )
            )
        }
        selectedCategory?.let {
            if(it == position) {
                holder.check.visibility = View.VISIBLE
                holder.card.strokeWidth = 7
                holder.card.setCardBackgroundColor(context.resources.getColor(R.color.card_background,null))
                holder.title.textSize = 13F
            }
        }
    }

    override fun getItemCount(): Int {
        categoryList?.let { return categoryList.size }
        productList?.let { return productList.size }
        return 0
    }
    @SuppressLint("NotifyDataSetChanged")
    fun refreshAll(position: Int?){
        selectedCategory = position
        notifyDataSetChanged()
    }

    inner class MyViewHolder(itemView: View, listener : OnOptionSelectedListener,context : Context) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
            val card : MaterialCardView = itemView.findViewById(R.id.option_card)
            val optionImage : ImageView = itemView.findViewById(R.id.option_image)
            val check : ImageView = itemView.findViewById(R.id.option_selected_check)
            val title : TextView = itemView.findViewById(R.id.option_title)
            private val con = context
            private val optionListener = listener

            init {
                card.setOnClickListener(this)
            }

        override fun onClick(v: View?) {
            optionListener.onOptionSelected(adapterPosition,check.visibility,categoryList != null)
            if(check.visibility == View.GONE) {
                check.visibility = View.VISIBLE
                card.strokeWidth = 7
                title.textSize = 13F
                card.setCardBackgroundColor(con.resources.getColor(R.color.card_background,null))
                refreshAll(adapterPosition)
            }else{
                check.visibility = View.GONE
                card.strokeWidth = 2
                title.textSize = 11F
                card.setCardBackgroundColor(con.resources.getColor(R.color.main_background,null))
            }
        }

    }

    interface OnOptionSelectedListener{
        fun onOptionSelected(position : Int,checkVisibility : Int,optionIsCategory : Boolean)
    }
}