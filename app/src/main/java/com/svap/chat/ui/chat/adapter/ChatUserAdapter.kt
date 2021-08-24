package com.svap.chat.ui.chat.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import com.svap.chat.base.BaseBindingAdapter
import com.svap.chat.base.BaseCallBack
import com.svap.chat.base.BaseViewHolder
import com.svap.chat.databinding.ItemMessageBinding
import com.svap.chat.ui.authenticate.model.UserData
import com.svap.chat.ui.authenticate.model.UserDataModel
import com.svap.chat.ui.chat.OneToOneChatActivity
import com.svap.chat.ui.chat.model.UsersResult
import com.svap.chat.utils.EXTRA_KEY_RECEIVER_ID
import com.svap.chat.utils.EXTRA_KEY_SOCKET_ID
import com.svap.chat.utils.EXTRA_KEY_USER_NAME
import com.svap.chat.utils.extentions.parseRecentTime
import com.svap.chat.utils.extentions.parseTime
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ChatUserAdapter(
    private var mList: ArrayList<UsersResult>,
    private val mCallback:BaseCallBack<UsersResult>
) : BaseBindingAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {

        return MyViewHolderUser(
                ItemMessageBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                )
        )
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class MyViewHolderUser(val mBinding: ItemMessageBinding) : BaseViewHolder(mBinding.root) {
        override fun bindData(position: Int) {
            mBinding.user = mList[position]
            mBinding.tvDate.text = parseRecentTime(mList[position].last_sent_date)
            mBinding.root.setOnClickListener {
                mCallback.getCallbackItem(mList[position],position)
            }
            mBinding.executePendingBindings()
        }
    }
}