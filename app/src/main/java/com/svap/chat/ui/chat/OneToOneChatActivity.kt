package com.svap.chat.ui.chat

import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import com.svap.chat.R
import com.svap.chat.base.BaseSocketActivity
import com.svap.chat.databinding.ActivityOneToChatBinding
import com.svap.chat.ui.chat.adapter.OneToOneChatAdapter
import com.svap.chat.ui.chat.model.AllMessageResponse
import com.svap.chat.ui.chat.model.MessageModel
import com.svap.chat.ui.dialog.DialogBio
import com.svap.chat.utils.*
import com.svap.chat.utils.itemdecor.CustomItemVerDecoration
import io.socket.emitter.Emitter
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class OneToOneChatActivity : BaseSocketActivity<ActivityOneToChatBinding>(
    R.layout.activity_one_to_chat
) {
    private val messagesList: ArrayList<MessageModel> = arrayListOf()
    private val messageAdapter: OneToOneChatAdapter by lazy {
        OneToOneChatAdapter(messagesList, mSharePresenter.getCurrentUser().id)
    }
    private val mReceiverSocketId: String by lazy {
        intent?.getStringExtra(EXTRA_KEY_SOCKET_ID) ?: ""
    }
    private val mReceiverUserId: String by lazy {
        intent?.getStringExtra(EXTRA_KEY_RECEIVER_ID) ?: ""
    }
    private val mReceiverUserName: String by lazy {
        intent?.getStringExtra(EXTRA_KEY_USER_NAME) ?: ""
    }
    private val bio: String by lazy {
        intent?.getStringExtra(EXTRA_KEY_BIO) ?: ""
    }

    private var messageToSend = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.mTitle = mReceiverUserName
        mBinding.bio = bio
        checkBlock()
    }

    override fun onStart() {
        super.onStart()
        mSocket?.on("selectUserResponse", onAllMessages)
    }

    override fun onDestroy() {
        super.onDestroy()
        destroySocket()
    }

    override fun onBackPressed() {
        destroySocket()
        if (isTaskRoot) {
            finish()
        } else {
            super.onBackPressed()
        }
    }

    override fun initAdapter() {
        mBinding.recyclerView.addItemDecoration(
            CustomItemVerDecoration(12)
        )
        mBinding.recyclerView.adapter = messageAdapter
    }

    override fun initListener() {
        super.initListener()
        mBinding.etMsg.filters = arrayOf<InputFilter>(EmojiExcludeFilter())

        mBinding.ivSend.setOnClickListener {

            if (!TextUtils.isEmpty(mBinding.etMsg.text.toString().trim())) {
                checkBlock()
                messageToSend = mBinding.etMsg.text.toString().trim()
                emitMessage()

                val messageModel = MessageModel(
                    from = mSharePresenter.getCurrentUser().id,
                    to = mReceiverUserId,
                    type = "text",
                    file = "",
                    isRead = 0,
                    message = messageToSend,
                    created_at = getCurrentDate(),
                    timestamp = System.currentTimeMillis().toString()
                )
                addMessageToList(messageModel)
                mBinding.etMsg.setText("")
            }
        }

        mBinding.toolbar1.tvBio.setOnClickListener {
            if(!TextUtils.isEmpty(bio)){
                DialogBio(mBinding.root, bio).show()
            }
        }
        mBinding.toolbar1.tvTitle.setOnClickListener {
            if(!TextUtils.isEmpty(bio)){
                DialogBio(mBinding.root, bio).show()
            }
        }

    }

    private fun getCurrentDate(): String {
        val date = Date()
        return SimpleDateFormat(MSG_DATE_FORMAT, Locale.getDefault()).format(date)
    }

    private fun emitMessage() {
        if (!TextUtils.isEmpty(messageToSend)) {
            val data1 = JSONObject()
            data1.put("socket_id", mReceiverSocketId)
            data1.put("token", mSharePresenter.token)
            data1.put("to", mReceiverUserId)
            data1.put("from", mSharePresenter.getCurrentUser().id)
            data1.put("message", messageToSend)
            mSocket?.emit("message", data1)
            Log.d(TAG_SOCKET, "$mReceiverUserId emitMessage " + data1)
        }
    }

    private fun readMessage() {
        val data1 = JSONObject()
        data1.put("to", mReceiverUserId)
        data1.put("from", mSharePresenter.getCurrentUser().id)
        mSocket?.emit("isRead", data1)
        Log.d(TAG_SOCKET, "$mReceiverUserId readMessage " + data1)
    }

    private fun addMessageToList(messageModel: MessageModel) {
        onErrorReturn(null)

        messagesList.add(messageModel)
        messageAdapter.notifyItemChanged(messageAdapter.itemCount)
        mBinding.recyclerView.scrollToPosition(messageAdapter.itemCount - 1)
    }

    private fun getAllChat() {
        val jsonObject = JSONObject()
        jsonObject.put("token", mSharePresenter.token)
        jsonObject.put("from", mSharePresenter.getCurrentUser().id)
        jsonObject.put("to", mReceiverUserId)
        mSocket?.emit("selectUser", jsonObject)
    }

    private val onAllMessages = Emitter.Listener { args ->
        Log.d(TAG_SOCKET, "onAllMessages call")
        this@OneToOneChatActivity.runOnUiThread {
            val data = args[0] as JSONObject
            val response = Gson().fromJson(data.toString(), AllMessageResponse::class.java)
            messagesList.clear()
            response.userChat?.let { messagesList.addAll(it) }
            if (messagesList.isEmpty()) {
                onErrorReturn("No message found")
            } else {
                messageAdapter.notifyDataSetChanged()
                mBinding.recyclerView.scrollToPosition(messageAdapter.itemCount - 1)
                onErrorReturn(null)
            }
            Log.d("dataResponse", "onAllMessages " + data)
        }
    }

    override fun onConnect() {
        readMessage()
        getAllChat()
    }

    override fun onNewMessage(msgList: ArrayList<MessageModel>) {
        msgList.forEach {
            if (it.from == mReceiverUserId) {
                addMessageToList(it)
                readMessage()
            }
        }
    }

    private class EmojiExcludeFilter : InputFilter {
        override fun filter(
            source: CharSequence,
            start: Int,
            end: Int,
            dest: Spanned,
            dstart: Int,
            dend: Int
        ): CharSequence? {
            for (i in start until end) {
                val type = Character.getType(source[i])
                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt()) {
                    return ""
                }
            }
            return null
        }
    }
}
