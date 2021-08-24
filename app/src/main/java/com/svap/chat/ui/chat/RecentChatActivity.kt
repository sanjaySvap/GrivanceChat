package com.svap.chat.ui.chat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.svap.chat.R
import com.svap.chat.base.BaseCallBack
import com.svap.chat.base.BaseSocketActivity
import com.svap.chat.databinding.ActivityRecentChatBinding
import com.svap.chat.ui.chat.adapter.ChatUserAdapter
import com.svap.chat.ui.chat.model.ChatUserResponse
import com.svap.chat.ui.chat.model.MessageModel
import com.svap.chat.ui.chat.model.UsersResult
import com.svap.chat.utils.EXTRA_KEY_RECEIVER_ID
import com.svap.chat.utils.EXTRA_KEY_SOCKET_ID
import com.svap.chat.utils.EXTRA_KEY_USER_NAME
import com.svap.chat.utils.extentions.addOnRefreshListener
import com.svap.chat.utils.itemdecor.RecyclerViewDecoration
import io.socket.emitter.Emitter
import org.json.JSONObject

class RecentChatActivity : BaseSocketActivity<ActivityRecentChatBinding>(
    R.layout.activity_recent_chat
), BaseCallBack<UsersResult> {

    private var mList = arrayListOf<UsersResult>()
    private val mAdapter: ChatUserAdapter by lazy {
        ChatUserAdapter(mList, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkBlock()
    }
    override fun initAdapter() {
        super.initAdapter()
        mBinding.recyclerView.adapter = mAdapter
        mBinding.recyclerView.addItemDecoration(
            RecyclerViewDecoration(
                18, 18, 18, 18
            )
        )
    }

    override fun initView() {
        super.initView()
        checkBlock()
    }

    override fun initListener() {
        super.initListener()
        mBinding.swipeRefresh.addOnRefreshListener {
            getUserList()
        }
    }

    override fun onStart() {
        super.onStart()
        mSocket?.on("recentChatListResponse", onUserChatList)
        mSocket?.on("checkOnlineOfline", onOnlineOffLine)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        destroySocket()
    }
    override fun onConnect() {
        super.onConnect()
        getUserList()
    }

    override fun onNewMessage(msg: ArrayList<MessageModel>) {
        getUserList()
    }

    private fun onUserChatList(list: ArrayList<UsersResult>) {
        mBinding.swipeRefresh.isRefreshing = false
        mList.clear()
        mList.addAll(list)
        mAdapter.notifyDataSetChanged()
        if (mList.isEmpty()) {
            onErrorReturn("No recent chat")
        } else {
            onErrorReturn(null)
        }
    }

    override fun onConnectionError(msg: String) {}

    override fun getCallbackItem(item: UsersResult, position: Int, tag: String) {
        destroySocket()
        val intent = Intent(this, OneToOneChatActivity::class.java)
        intent.putExtra(EXTRA_KEY_USER_NAME, mList[position].first_name)
        intent.putExtra(EXTRA_KEY_RECEIVER_ID, mList[position].id)
        intent.putExtra(EXTRA_KEY_SOCKET_ID, mList[position].socket_id)
        startActivity(intent)
    }

    private fun getUserList() {
        val data = JSONObject()
        data.put("from", mSharePresenter.getCurrentUser().id)
        data.put("page", 1)
        mSocket?.emit("recentChatList", data)
        Log.d(TAG_SOCKET, " emit chat-list " + data)
    }

    private val onUserChatList = Emitter.Listener { args ->
        Log.d(TAG_SOCKET, "onUserChatList call onUserChatList")
        this@RecentChatActivity.runOnUiThread {
            val data = args[0] as JSONObject
            val response = Gson().fromJson(data.toString(), ChatUserResponse::class.java)
            Log.d("onUserChatList", " " + data)
            if (!response.error) {
                onUserChatList(response.recentChatList ?: ArrayList())
            } else {
                onUserChatList(ArrayList())
            }
        }
    }

    private val onOnlineOffLine = Emitter.Listener { args ->
        Log.d(TAG_SOCKET, "onOnlineOffLine")
        getUserList()
    }
}
