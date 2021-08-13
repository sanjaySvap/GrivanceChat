package com.svap.chat.ui.home.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import com.svap.chat.base.BaseBindingAdapter
import com.svap.chat.base.BaseCallBack
import com.svap.chat.base.BaseViewHolder
import com.svap.chat.databinding.ListItemHomeBinding
import com.svap.chat.ui.chat.OneToOneChatActivity
import com.svap.chat.ui.chat.model.HomeUser
import com.svap.chat.ui.chat.model.UsersResult
import com.svap.chat.utils.EXTRA_KEY_RECEIVER_ID
import com.svap.chat.utils.EXTRA_KEY_SOCKET_ID
import com.svap.chat.utils.EXTRA_KEY_USER_NAME
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class HomeDataAdapter(
        private var mList: ArrayList<HomeUser>,
        private val mCallback: BaseCallBack<HomeUser>
) : BaseBindingAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return MyViewHolderUser(
                ListItemHomeBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                )
        )
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class MyViewHolderUser(val mBinding: ListItemHomeBinding) :
            BaseViewHolder(mBinding.root) {
        override fun bindData(position: Int) {
            mBinding.user = mList[position]
            mBinding.ivChat.setOnClickListener {
                mCallback.getCallbackItem(mList[position],position)
            }
            mBinding.executePendingBindings()
        }
    }

}