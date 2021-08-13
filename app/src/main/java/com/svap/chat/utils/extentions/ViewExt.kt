package com.svap.chat.utils.extentions

import android.app.DatePickerDialog
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.svap.chat.R
import com.svap.chat.utils.customView.EndlessRecyclerView
import java.text.SimpleDateFormat
import java.util.*


fun EndlessRecyclerView.addPagerListener(pager: EndlessRecyclerView.Pager) {
    this.setPager(pager)
    this.setProgressView(R.layout.dialog_progress_load_more)
    this.isRefreshing = false
}

fun SwipeRefreshLayout.addOnRefreshListener(listener: () -> Unit) {
    this.setOnRefreshListener {
        listener()
    }
    this.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
    )
}

fun EditText.getString(): String {
    return this.text.toString().trim()
}

fun TextView.getString(): String {
    return this.text.toString().trim()
}

fun View.showSnackbar(message: String) {
    val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_SHORT)
    val view: View = snackbar.view
    val params = view.layoutParams as FrameLayout.LayoutParams
    params.gravity = Gravity.TOP
    view.layoutParams = params

    snackbar.setTextColor(ContextCompat.getColor(this.context, R.color.white))
            .setBackgroundTint(ContextCompat.getColor(this.context, R.color.colorGreen))
            .show()
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.visibilityFilter(predicate: () -> Boolean) {
    if (predicate()) visible() else gone()
}

fun View.invisible() {
    visibility = View.INVISIBLE
}


fun TextView.toDatePicker(setDate: (dob: String) -> Unit = {}) {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.YEAR, -16)
    val datePicker = DatePickerDialog(
            this.context, R.style.DatePicker,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                this.text = formatDate(calendar.time)
                setDate(formatDate(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
    )

    datePicker.datePicker.maxDate = calendar.timeInMillis
    datePicker.show()
}


fun formatDate(date: Date): String = run {
    val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    simpleDateFormat.format(date)
}

