package com.svap.chat.utils.extentions

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import com.svap.chat.ui.chat.OneToOneChatActivity
import com.svap.chat.utils.EXTRA_KEY_SOCKET_ID
import com.svap.chat.utils.EXTRA_KEY_USER_IMAGE
import com.svap.chat.utils.EXTRA_KEY_USER_NAME
import com.svap.chat.utils.MSG_DATE_FORMAT
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun Context.isConnected(): Boolean {
    val manager: ConnectivityManager? =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    val info = manager?.activeNetworkInfo
    return !(info == null || !info.isConnected)
}


fun Context.openLink(
    url: String
) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.data = Uri.parse(url.trim())
    startActivity(Intent.createChooser(intent, "Open with"))
}

fun openOneToOneChat(
    context: Context,
    recieverId: String,
    name: String,
    image: String
) {
    val intent = Intent(context, OneToOneChatActivity::class.java)
    intent.putExtra(EXTRA_KEY_SOCKET_ID, recieverId)
    intent.putExtra(EXTRA_KEY_USER_NAME, name)
    intent.putExtra(EXTRA_KEY_USER_IMAGE, image)
    context.startActivity(intent)

}


const val DATE_FORMATS_HH_MM_A_DD_MMMM_YYYY = "hh:mm a, dd MMMM yyyy"
fun setDateTimeFormat(value: String, format: String = "dd-MM-yyyy hh:mm a"): String {
    val formatTime = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val formatDate = SimpleDateFormat(format, Locale.getDefault())
    try {
        val calendar = Calendar.getInstance()
        val date = Date(value.toLong())
        val today = calendar.time
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val tomorrow = calendar.time
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        if (compareTwoDates(
                dateFormatter.format(
                    date
                ), dateFormatter.format(today)
            )
        ) {
            return "Today " + formatTime.format(date)
        } else if (compareTwoDates(
                dateFormatter.format(
                    date
                ), dateFormatter.format(tomorrow)
            )
        ) {
            return "Yesterday " + formatTime.format(date)
        } else {
            return formatDate.format(date)
        }
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return ""
}

fun parseTime(dateStr: String): String {
    val simpleDateFormatServer = SimpleDateFormat(MSG_DATE_FORMAT, Locale.getDefault())
    val messageDate = simpleDateFormatServer.parse(dateStr) ?: Date()
    val f = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return f.format(Date(messageDate.time))
}

fun getDateTimeFormat(dateStr: String): String {
    try {
        val simpleDateFormatServer = SimpleDateFormat(MSG_DATE_FORMAT, Locale.getDefault())
        val messageDate = simpleDateFormatServer.parse(dateStr) ?: Date()
        val calendar = Calendar.getInstance()
        val today = calendar.time
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val yesterday = calendar.time
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd",Locale.getDefault())

        return when {
            compareTwoDates(dateFormatter.format(messageDate), dateFormatter.format(today)) -> "Today"
            compareTwoDates(dateFormatter.format(messageDate), dateFormatter.format(yesterday)) -> "Yesterday"
            else -> dateFormatter.format(messageDate)
        }
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return ""
}

fun compareTwoDates(date1: String?, date2: String?): Boolean {
    return if (date1 != null && date2 != null) {
        date1 == date2
    } else false
}

