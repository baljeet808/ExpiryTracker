package com.baljeet.expirytracker.listAdapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.relations.CategoryAndImage
import com.baljeet.expirytracker.data.relations.ProductAndImage
import com.baljeet.expirytracker.databinding.ItemOptionBinding
import com.baljeet.expirytracker.util.MyColors

class OptionsAdapter(
    private val categoryList: ArrayList<CategoryAndImage>?,
    private val context: Context,
    private var selectedCategory: Int?,
    private val optionClickListener: OnOptionSelectedListener,
    private val productList: ArrayList<ProductAndImage>?
) : RecyclerView.Adapter<OptionsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {

        return MyViewHolder(ItemOptionBinding.inflate(LayoutInflater.from(parent.context),parent,false), optionClickListener, context)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind.optionCard.strokeWidth = 2
        holder.bind.optionSelectedCheck.visibility = View.GONE
        holder.bind.optionTitle.textSize = 11F
        holder.bind.optionCard.setCardBackgroundColor(
            context.resources.getColor(
                R.color.main_background, null
            )
        )
        categoryList?.let {
            val category = categoryList[position]
            holder.bind.optionTitle.text = category.category.categoryName
            holder.bind.optionImage.setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    context.resources.getIdentifier(
                        category.image.imageUrl,
                        "drawable",
                        context.packageName
                    )
                )
            )
        }
        productList?.let {
            val product = productList[position]
            holder.bind.optionTitle.text = product.product.name
            holder.bind.optionImage.setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    context.resources.getIdentifier(
                        product.image.imageUrl,
                        "drawable",
                        context.packageName
                    )
                )
            )
        }
        selectedCategory?.let {
            if (it == position) {
                holder.bind.optionSelectedCheck.visibility = View.VISIBLE
                holder.bind.optionCard.strokeWidth = 7
                holder.bind.optionCard.setCardBackgroundColor(MyColors.getColorByAttr(context,R.attr.card_background,R.color.tab_background))
                holder.bind.optionTitle.textSize = 13F
            }
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    fun setCategoriesWithImages(list : List<CategoryAndImage>){
        categoryList?.clear()
        categoryList?.addAll(list)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setProducts(products: List<ProductAndImage>) {

        productList?.clear()
        productList?.addAll(products)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        categoryList?.let { return categoryList.size }
        productList?.let { return productList.size }
        return 0
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshAll(position: Int?) {
        selectedCategory = position
        notifyDataSetChanged()
    }



    inner class MyViewHolder(val bind : ItemOptionBinding, listener: OnOptionSelectedListener, context: Context) :
        RecyclerView.ViewHolder(bind.root), View.OnClickListener {
        private val con = context
        private val optionListener = listener

        init {
            bind.optionCard.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            optionListener.onOptionSelected(adapterPosition, bind.optionSelectedCheck.visibility, categoryList != null)
            if (bind.optionSelectedCheck.visibility == View.GONE) {
                bind.optionSelectedCheck.visibility = View.VISIBLE
                bind.optionCard.strokeWidth = 7
                bind.optionTitle.textSize = 13F
                bind.optionCard.setCardBackgroundColor(MyColors.getColorByAttr(context,R.attr.card_background,R.color.tab_background))
                refreshAll(adapterPosition)
            } else {
                bind.optionSelectedCheck.visibility = View.GONE
                bind.optionCard.strokeWidth = 2
                bind.optionTitle.textSize = 11F
                bind.optionCard.setCardBackgroundColor(con.resources.getColor(R.color.main_background, null))
            }
        }

    }

    interface OnOptionSelectedListener {
        fun onOptionSelected(position: Int, checkVisibility: Int, optionIsCategory: Boolean)
    }
}