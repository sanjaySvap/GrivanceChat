package com.svap.chat

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.svap.chat.coinDi.module.appModule
import com.svap.chat.coinDi.module.myViewModel
import com.svap.chat.coinDi.module.networkModule
import com.svap.chat.utils.AppPreferencesHelper
import com.svap.chat.utils.SOCKET_LOCAL
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.client.SocketIOException
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApp : MultiDexApplication() {

    private val TAG_SOCKET = "MyApp_Socket"
     var mSocket: Socket? = null

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        startKoin {
            androidLogger()
            androidContext(this@MyApp)
            modules(listOf(appModule, networkModule, myViewModel))
        }
    }

   /* @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onEnterForeground() {
        Log.d("AppController", "Foreground")
        createSocket()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onEnterBackground() {
        Log.d("AppController", "Background")
        destroySocket()
    }*/

    private fun destroySocket() {
        mSocket?.disconnect()
        mSocket?.close()
        mSocket = null
    }

    private fun createSocket() {
        try {
            val PREF_NAME = "com.chat"
            val MODE = Context.MODE_PRIVATE
            val mSharePresenter = AppPreferencesHelper(getSharedPreferences(PREF_NAME, MODE))
            Log.d(TAG_SOCKET, "Current_user " + mSharePresenter.token)
            val mOptions = IO.Options()
            mOptions.query = "token=${mSharePresenter.token}&name=${mSharePresenter.getCurrentUser().user_name}"
            mSocket = IO.socket(SOCKET_LOCAL, mOptions)
            mSocket?.connect()
        } catch (e: SocketIOException) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

}