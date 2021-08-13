package com.svap.chat.base

import androidx.recyclerview.widget.RecyclerView
import com.svap.chat.base.BaseViewHolder

abstract class BaseBindingAdapter :RecyclerView.Adapter<BaseViewHolder>(){

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bindData(position)
    }
}