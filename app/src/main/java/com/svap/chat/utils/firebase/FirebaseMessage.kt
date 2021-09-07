package com.svap.chat.utils.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.svap.chat.R
import com.svap.chat.ui.authenticate.SplashActivity
import com.svap.chat.ui.chat.OneToOneChatActivity
import com.svap.chat.ui.chat.RecentChatActivity
import com.svap.chat.utils.AppPreferencesHelper
import org.koin.core.context.GlobalContext

class FirebaseMessage : FirebaseMessagingService() {

    private var alarmNotificationManager: NotificationManager? = null
    private var builder: NotificationCompat.Builder? = null
    private val NOTIFICATION_CHANNEL_NAME = "Malik's Grievance"
    private val NOTIFICATION_CHANNEL_ID = "com.grievance"
    private val mSharedPresenter: AppPreferencesHelper = GlobalContext.get().get()

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        addNotification(remoteMessage)
    }
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        mSharedPresenter.deviceToken = token
    }
    private fun addNotification(remoteMessage: RemoteMessage?) {
        Log.d("FirebaseMessage","remoteMessage "+Gson().toJson(remoteMessage?.data))
        val data = Gson().fromJson(Gson().toJson(remoteMessage?.data), NotificationData::class.java)
        alarmNotificationManager = this
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        alarmNotificationManager = this
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(this, SplashActivity::class.java)
        val contentIntent = PendingIntent.getActivity(
                this, 0,
                intent, 0
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setNotificationCompatChaanl()
            builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        } else {
            builder = NotificationCompat.Builder(this)
        }
        builder?.setSmallIcon(R.drawable.notification_logo)
        builder?.color = ContextCompat.getColor(this, R.color.colorGreen)
        builder?.setContentTitle(data.title)
        builder?.setContentText(data.subtitle)
        builder?.setContentIntent(contentIntent)
        builder?.setAutoCancel(true)
        alarmNotificationManager?.notify(1, builder?.build())
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    internal fun setNotificationCompatChaanl() {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance
        )
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.enableVibration(true)
        notificationChannel.vibrationPattern =
                longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
        val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }
}

data class NotificationData(
        var subtitle: String? = "",
        var title: String? = "",
        var iconUrl: String? = "",
)
