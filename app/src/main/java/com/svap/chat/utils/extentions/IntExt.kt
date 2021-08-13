package com.svap.chat.utils.extentions

import android.content.Context
import android.content.res.Resources

fun Int.toPx(context: Context) = run {
    this * context.resources.displayMetrics.density
}



val Int.toPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()
