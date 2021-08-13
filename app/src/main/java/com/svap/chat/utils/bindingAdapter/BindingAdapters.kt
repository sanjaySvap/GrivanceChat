package com.svap.chat.utils.bindingAdapter

import android.net.Uri
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.svap.chat.R
import com.svap.chat.utils.extentions.openLink
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


@BindingAdapter("setProfileImageUrl")
fun setProfileImageUrl(view: ImageView, imageUrl: String?) {

    val url = imageUrl?.let {
        if(!TextUtils.isEmpty(it) && !it.startsWith("http")) "http://15.207.148.33:4001/image/$it" else it
    }?:""
    try {
        Log.d("ReceiverUserImage", "setProfileImageUrl view $url ")
        Glide.with(view.context)
            .load(url)
            .placeholder(R.drawable.dummy_profile)
            .into(view)
    } catch (e: Exception) {
        view.setImageResource(R.drawable.dummy_profile)
    }
}

@BindingAdapter("setImageUrl")
fun setImageUrl(view: ImageView, imageUrl: String?) {
    try {
        val url = imageUrl?.let {
            if(!TextUtils.isEmpty(it) && !it.startsWith("http")) "http://15.207.148.33:4001/image/$it" else it
        }?:""
        Log.d("ReceiverUserImage", "setProfileImageUrl $url ")
        Glide.with(view.context).load(url).placeholder(R.drawable.dummy_home).into(view)
    } catch (e: Exception) {
        view.setImageResource(R.drawable.dummy_home)
    }
}

@BindingAdapter("setImageUrlUri")
fun setImageUrlUri(view: ImageView, imageUrl: Uri) {
    try {
        Glide.with(view.context).load(imageUrl).placeholder(R.drawable.dummy_home).into(view)
    } catch (e: Exception) {
        view.setImageResource(R.drawable.dummy_home)
    }
}

fun setUrlSan(view: TextView?, body: String? = "") {
    val urlPattern: Pattern = Pattern.compile(
        "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
        Pattern.CASE_INSENSITIVE or Pattern.MULTILINE or Pattern.DOTALL
    )
    if (TextUtils.isEmpty(body)) {
        view?.text = ""
        return
    }
    val span = SpannableString(body)

    view?.let { textView ->
        val matcher = urlPattern.matcher(body)

        while (matcher.find()) {
            val url = matcher.group()
            val matchStart = matcher.start(1);
            val matchEnd = matcher.end()
            span.setSpan(object : ClickableSpan() {
                override fun updateDrawState(ds: TextPaint) {
                    ds.isUnderlineText = false
                }

                override fun onClick(p0: View) {
                    Log.d("setUrlSan", "onClick $url")
                    view.context.openLink(url)
                }

            }, matchStart, matchEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            span.setSpan(
                ForegroundColorSpan(
                    ContextCompat.getColor(
                        textView.context,
                        R.color.svgTintPrimary
                    )
                ), matchStart, matchEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

        }
    }

    view?.movementMethod = LinkMovementMethod()
    view?.setText(
        span,
        TextView.BufferType.SPANNABLE
    )
}


fun getAge(date: String): Int {
    var age = 0
    try {
        val dateFormat = SimpleDateFormat("dd-mm-yyyy", Locale.getDefault())
        val date1: Date = dateFormat.parse(date)?:Date()
        val now: Calendar = Calendar.getInstance()
        val dob: Calendar = Calendar.getInstance()
        dob.setTime(date1)
        val year1: Int = now.get(Calendar.YEAR)
        val year2: Int = dob.get(Calendar.YEAR)
        age = year1 - year2
        val month1: Int = now.get(Calendar.MONTH)
        val month2: Int = dob.get(Calendar.MONTH)
        if (month2 > month1) {
            age--
        } else if (month1 == month2) {
            val day1: Int = now.get(Calendar.DAY_OF_MONTH)
            val day2: Int = dob.get(Calendar.DAY_OF_MONTH)
            if (day2 > day1) {
                age--
            }
        }
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return age
}