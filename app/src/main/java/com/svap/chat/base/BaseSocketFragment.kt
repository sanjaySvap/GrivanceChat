package com.svap.chat.base
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.databinding.ViewDataBinding
import com.google.gson.Gson
import com.svap.chat.ui.chat.model.*
import com.svap.chat.utils.NO_NETWORK_ERROR
import com.svap.chat.utils.SOCKET_LOCAL
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.client.SocketIOException
import io.socket.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject

abstract class BaseSocketFragment<D : ViewDataBinding>(
    resourceId: Int,
) : BaseFragment<D>(resourceId) {
    val TAG_SOCKET = "SOCKET_CHAT_Fragment"
    var mSocket: Socket? = null
    var isConnected = true

    private fun initSocket() {
        try {
            Log.d(TAG_SOCKET, "Current_user " + mSharePresenter.token)
            val mOptions = IO.Options()
            mOptions.query = "token=${mSharePresenter.token}&name=${mSharePresenter.getCurrentUser().user_name}"
            mSocket = IO.socket(SOCKET_LOCAL,mOptions)
            Log.d(TAG_SOCKET,SOCKET_LOCAL)
            initSocketIo()
        } catch (e: SocketIOException) {
            Toast.makeText(requireActivity(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        initSocket()
        Handler(Looper.getMainLooper()).postDelayed({
            mSocket?.connect()
        }, 100)
        Log.d(TAG_SOCKET, "onStart " + mSocket?.connected() + " " + mSocket?.id())
    }

    override fun onStop() {
        super.onStop()
        mSocket?.disconnect()
    }

    private fun initSocketIo() {
        mSocket?.on(Socket.EVENT_CONNECT, onConnect)
        mSocket?.on(Socket.EVENT_DISCONNECT, onDisconnect)
        mSocket?.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
        Log.d(TAG_SOCKET, "initSocketIo $mSocket")
    }

    fun destroySocket() {
        mSocket?.disconnect()
        mSocket?.close()
        mSocket = null
    }

    open fun onHideKeyboard() {}
    open fun onShowKeyboard() {}
    open fun onNewMessage(msg: MessageModel) {}
    open fun onUserChatList(list: ArrayList<UsersResult>) {}

    open fun onHomeUserList(list: ArrayList<HomeUser>) {}

    private val onConnect = Emitter.Listener {
        requireActivity().runOnUiThread(object : Runnable {
            override fun run() {
                Log.d(TAG_SOCKET, "connected")
                onConnect()
            }
        })
    }

    private val onDisconnect = Emitter.Listener {
        requireActivity().runOnUiThread {
            isConnected = false
            Log.d(TAG_SOCKET, "disConnected")
        }
    }

    private val onConnectError = Emitter.Listener {
        requireActivity().runOnUiThread {
            if (!isConnected) {
                onConnectionError(NO_NETWORK_ERROR)
            }
            Log.d(TAG_SOCKET, "onConnectError "+Gson().toJson(it))
        }
    }

}