package com.svap.chat.base
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.databinding.ViewDataBinding
import com.google.gson.Gson
import com.svap.chat.ui.chat.model.ChatUserResponse
import com.svap.chat.ui.chat.model.MessageDataModel
import com.svap.chat.ui.chat.model.MessageModel
import com.svap.chat.ui.chat.model.UsersResult
import com.svap.chat.utils.NO_NETWORK_ERROR
import com.svap.chat.utils.SOCKET_LOCAL
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.client.SocketIOException
import io.socket.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject

abstract class BaseSocketActivity<D : ViewDataBinding>(
    resourceId: Int,
) : BaseActivity<D>(resourceId) {
    val TAG_SOCKET = "SOCKET_CHAT"
    var mSocket: Socket? = null
    var isConnected = true

    private fun initSocket() {
        try {
            Log.d(TAG_SOCKET, "Current_user " + mSharePresenter.token)
            val mOptions = IO.Options()
            mOptions.query = "token=${mSharePresenter.token}&name=${mSharePresenter.getCurrentUser().user_name}"
            mSocket = IO.socket(SOCKET_LOCAL,mOptions)
            initSocketIo()
        } catch (e: SocketIOException) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            onBackPressed()
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
        mSocket?.on("msgReceived", onNewMessage)
    }

    fun destroySocket() {
        mSocket?.disconnect()
        mSocket?.close()
        mSocket = null
    }

    open fun onNewMessage(msg: ArrayList<MessageModel>) {}
    open fun onConnect() {}
    open fun onDisConnect() {}
    open fun onConnectionError(msg: String) {}

    private val onConnect = Emitter.Listener {
        this@BaseSocketActivity.runOnUiThread(object : Runnable {
            override fun run() {
                Log.d(TAG_SOCKET, "connected")
                onConnect()
            }
        })
    }
    private val onDisconnect = Emitter.Listener {
        this@BaseSocketActivity.runOnUiThread(object : Runnable {
            override fun run() {
                isConnected = false
                Log.d(TAG_SOCKET, "$this disConnected")
            }
        })
    }
    private val onConnectError = Emitter.Listener {
        this@BaseSocketActivity.runOnUiThread {
            if (!isConnected) {
                onConnectionError(NO_NETWORK_ERROR)
            }
            Log.d(TAG_SOCKET, "onConnectError "+Gson().toJson(it))
        }
    }

    val onNewMessage = object : Emitter.Listener {
        override fun call(vararg args: Any) {
            Log.d(TAG_SOCKET, "onNewMessage call onNewMessage")
            this@BaseSocketActivity.runOnUiThread(object : Runnable {
                override fun run() {
                    val json = args[0] as JSONObject
                    Log.d("dataResponse", "" + json)
                    try {
                        val msg = Gson().fromJson(json.toString(), MessageDataModel::class.java)
                        onNewMessage(msg.getmsgreceived)
                    }catch (e: JSONException) {
                        return
                    }
                }
            })
        }
    }

}