package com.svap.chat.ui.authenticate.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.svap.chat.R

import com.svap.chat.base.BaseBindingAdapter
import com.svap.chat.base.BaseCallBack
import com.svap.chat.base.BaseViewHolder
import com.svap.chat.databinding.ListItemCountryBinding
import com.svap.chat.ui.authenticate.model.CountryModel
import com.svap.chat.utils.country.Country
import java.text.ParsePosition

class CountryAdapter(
    var mList: ArrayList<Country>,
    private val mCallback: BaseCallBack<Country>
) : BaseBindingAdapter() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return ViewHolder(
            ListItemCountryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(val binding: ListItemCountryBinding) : BaseViewHolder(binding.root) {
        override fun bindData(position: Int) {
            binding.tv2.text = mList[layoutPosition].Name
            binding.ivFlag.setImageResource(mList[layoutPosition].Flag)

            binding.root.setOnClickListener {
               /* if(selected >=0 && selected != layoutPosition){
                    mList[selected].IsSelected = false
                    notifyItemChanged(selected)
                }
                selected = layoutPosition
                mList[selected].IsSelected = true
                notifyItemChanged(layoutPosition)
*/
                mCallback.getCallbackItem(mList[layoutPosition])
            }
            binding.executePendingBindings()
        }
    }

}