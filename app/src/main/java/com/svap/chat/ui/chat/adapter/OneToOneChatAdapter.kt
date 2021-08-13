package com.svap.chat.ui.chat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.svap.chat.base.BaseBindingAdapter
import com.svap.chat.base.BaseViewHolder
import com.svap.chat.databinding.ItemChatReceiveMessageBinding
import com.svap.chat.databinding.ItemChatSendMessageBinding
import com.svap.chat.ui.chat.model.MessageModel
import com.svap.chat.utils.MSG_DATE_FORMAT
import com.svap.chat.utils.bindingAdapter.setUrlSan
import com.svap.chat.utils.extentions.*
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.Locale

class OneToOneChatAdapter(
        var messagesList: ArrayList<MessageModel>,
        var currentUserId: String
) : BaseBindingAdapter() {
    private val time = ArrayList<String>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        when (viewType) {
            1 -> {
                val binding =
                    ItemChatSendMessageBinding.inflate(
                        LayoutInflater.from(parent.context), parent,
                        false
                    )
                return MyMessageViewHolder(binding)
            }
            2 -> {
                val binding =
                    ItemChatReceiveMessageBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                return TheirMessageViewHolder(binding)
            }
            else -> {
                val binding =
                    ItemChatSendMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return MyMessageViewHolder(binding)
            }

        }
    }

    override fun getItemCount(): Int {
        return messagesList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (messagesList[position].from == currentUserId) {
            true -> 1
            false -> 2
            else -> -1
        }
    }

    inner class MyMessageViewHolder(var mBinding: ItemChatSendMessageBinding) :
        BaseViewHolder(mBinding.root) {
        override
        fun bindData(position: Int) {
            setUrlSan(mBinding.tvMsg, messagesList[position].message)
            val d1 = getDateTimeFormat(messagesList[position].created_at)
            mBinding.tvTime.text = parseTime(messagesList[position].created_at)
            mBinding.tvChatDate.text = d1
            mBinding.tvChatDate.visibility = if(position == 0) View.VISIBLE else {
                val d2 = getDateTimeFormat(messagesList[position-1].created_at)
                if(compareTwoDates(d1,d2)){
                    View.GONE
                }else{
                    View.VISIBLE
                }
            }
            mBinding.executePendingBindings()
        }
    }

    inner class TheirMessageViewHolder(var mBinding: ItemChatReceiveMessageBinding) :
        BaseViewHolder(mBinding.root) {
        override fun bindData(position: Int) {
            setUrlSan(mBinding.tvMsg, messagesList[position].message)
            val d1 = getDateTimeFormat(messagesList[position].created_at)
            mBinding.tvTime.text = parseTime(messagesList[position].created_at)
            mBinding.tvChatDate.text = d1

            mBinding.tvChatDate.visibility = if(position == 0) View.VISIBLE else {
                val d2 = getDateTimeFormat(messagesList[position-1].created_at)
                if(compareTwoDates(d1,d2)){
                    View.GONE
                }else{
                    View.VISIBLE
                }
            }
            mBinding.executePendingBindings()
        }
    }

    fun add(message: MessageModel, time: String) {
        this.messagesList.add(message)
        this.time.add(time)
        notifyDataSetChanged()
    }
}
