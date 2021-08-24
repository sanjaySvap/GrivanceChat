package com.svap.chat.ui.home.fragments

import android.content.Context
import android.content.Intent
import android.util.Log
import com.fantasy.utils.extentions.goto
import com.fantasy.utils.extentions.gotoNewTask
import com.google.gson.Gson
import com.svap.chat.R
import com.svap.chat.base.BaseCallBack
import com.svap.chat.base.BaseFragment
import com.svap.chat.base.BaseVmFragment
import com.svap.chat.databinding.FragmentHomeBinding
import com.svap.chat.ui.authenticate.LoginActivity
import com.svap.chat.ui.chat.OneToOneChatActivity
import com.svap.chat.ui.chat.model.HomeUser
import com.svap.chat.ui.chat.model.UserResponse
import com.svap.chat.ui.dialog.DialogAlert
import com.svap.chat.ui.home.HomeActivity
import com.svap.chat.ui.home.adapter.HomeDataAdapter
import com.svap.chat.ui.home.viewModel.HomeViewModel
import com.svap.chat.utils.EXTRA_KEY_RECEIVER_ID
import com.svap.chat.utils.EXTRA_KEY_SOCKET_ID
import com.svap.chat.utils.EXTRA_KEY_USER_NAME
import com.svap.chat.utils.extentions.addOnRefreshListener
import com.svap.chat.utils.extentions.gone
import com.svap.chat.utils.extentions.toPx
import com.svap.chat.utils.extentions.visible
import com.svap.chat.utils.itemdecor.GridSpacingItemDecoration
import io.socket.emitter.Emitter
import org.json.JSONObject

class HomeFragment() :
        BaseVmFragment<FragmentHomeBinding,HomeViewModel>(R.layout.fragment_home,HomeViewModel::class), BaseCallBack<HomeUser> {

    private val TAG_SOCKET = "Sock_HomeFragment"
    private var mLoadingNext = false
    private var mPage = 1
    private var mUserList = arrayListOf<HomeUser>()
    private val mAdapter: HomeDataAdapter by lazy {
        HomeDataAdapter(mUserList, this)
    }

    private var mHomeActivity: HomeActivity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HomeActivity) {
            mHomeActivity = context
        }
    }

    override fun initAdapter() {
        super.initAdapter()
        mBinding.recyclerView.adapter = mAdapter
        mBinding.recyclerView.addItemDecoration(
                GridSpacingItemDecoration(2, 20.toPx, true)
        )
    }

    override fun initListener() {
        super.initListener()
        mBinding.swipeRefresh.addOnRefreshListener {
            getMessageStatus()
            getHomeUserList()
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onConnect() {
        super.onConnect()
        mBinding.root.postDelayed({
            progressBar?.visible()
            getMessageStatus()
            getHomeUserList()

        }, 50)
    }

    private fun getMessageStatus() {
        val data = JSONObject()
        data.put("from", mSharePresenter.getCurrentUser().id)
        Log.d(TAG_SOCKET, "emit getMessageStatus " + mHomeActivity?.mSocket?.id())

        mHomeActivity?.mSocket?.emit("messageSeenStatus", data)
    }

    private fun getHomeUserList() {
        checkBlock()
        val data = JSONObject()
        data.put("token", mSharePresenter.token)
        data.put("page", mPage)
        data.put("country_id", mSharePresenter.chooseCountryId)
        Log.d(TAG_SOCKET, "getHomeUserList " + data)
        mHomeActivity?.mSocket?.emit("chatList", data)
    }

    private fun onHomeUserList(list: ArrayList<HomeUser>) {
        mBinding.swipeRefresh.isRefreshing = false
        progressBar?.gone()
        mUserList.clear()
        mUserList.addAll(list)
        mAdapter.notifyDataSetChanged()

        if (mUserList.isEmpty()) {
            onErrorReturn("No user found for this country")
        } else {
            onErrorReturn(null)
        }
    }

    override fun getCallbackItem(item: HomeUser, position: Int, tag: String) {
        if (item.is_online == 1) {
            mHomeActivity?.destroySocket()
            val intent = Intent(requireActivity(), OneToOneChatActivity::class.java)
            intent.putExtra(EXTRA_KEY_USER_NAME, mUserList[position].first_name)
            intent.putExtra(EXTRA_KEY_RECEIVER_ID, mUserList[position].id)
            intent.putExtra(EXTRA_KEY_SOCKET_ID, mUserList[position].socket_id)
            requireActivity().goto(intent)
        }
    }

    private val onHomeUserList = Emitter.Listener { args ->
        Log.d(TAG_SOCKET, "onHomeUserList call onUserChatList")
        if (activity != null && isAdded) {
            requireActivity().runOnUiThread {
                val data = args[0] as JSONObject
                val response = Gson().fromJson(data.toString(), UserResponse::class.java)
                Log.d("onHomeUserList", " $data")
                if (!response.error) {
                    onHomeUserList(response.userChatList ?: ArrayList())
                } else {
                    onHomeUserList(ArrayList())
                }
            }
        }
    }

    private val onOnlineOffLine = Emitter.Listener { args ->
        Log.d(TAG_SOCKET, "onOnlineOffLine")
        if (activity != null && isAdded) {
            requireActivity().runOnUiThread {
                getHomeUserList()
            }
        }
    }

    private val messageSeenStatusResponse = Emitter.Listener { args ->
        Log.d(TAG_SOCKET, "messageSeenStatusResponse")
        if (activity != null && isAdded) {
            requireActivity().runOnUiThread {
                val response = args[0] as JSONObject
                val isRead = if (response.has("is_read")) response.getInt("is_read") else 1
                Log.d(TAG_SOCKET, "messageSeen " + response)
                if (activity != null && activity is HomeActivity) {
                    (activity as HomeActivity).setChatIcon(isRead)
                }
            }
        }
    }

    fun initSocket() {
        mHomeActivity?.mSocket?.on("chatListResponse", onHomeUserList)
        mHomeActivity?.mSocket?.on("checkOnlineOfline", onOnlineOffLine)
        mHomeActivity?.mSocket?.on("messageSeenStatusResponse", messageSeenStatusResponse)
    }
}