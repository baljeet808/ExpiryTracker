package com.baljeet.expirytracker.listAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.databinding.QNAListItemBinding
import com.baljeet.expirytracker.model.QnA


class QnAAdapter(private val list : ArrayList<QnA>,
                private val context : Context
) : RecyclerView.Adapter<QnAAdapter.MyViewHolder>() {
    class MyViewHolder(val bind : QNAListItemBinding) : RecyclerView.ViewHolder(bind.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ):MyViewHolder {
        return MyViewHolder(QNAListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind.apply {
            val item = list[position]
            question.text = item.question
            answer.text = item.answer
            qnaCard.setOnClickListener {
                answer.isGone = !answer.isGone
                if(answer.isGone){
                    arrowImage.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_arrow_down_24))
                }else{
                    arrowImage.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_arrow_up_24))
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}