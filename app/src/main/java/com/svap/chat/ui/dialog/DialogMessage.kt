package com.svap.chat.ui.dialog

import android.app.Dialog
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.svap.chat.databinding.DialogMessageBinding

class DialogMessage(view: View, title: String, message: String, actionText: String, action: () -> Unit) : Dialog(view.context) {
    private val viewDataBinding: DialogMessageBinding by lazy {
        DialogMessageBinding.inflate(layoutInflater, view.parent as ViewGroup, false)
    }

    init {
        setContentView(
            viewDataBinding.also {
                it.title = title
                it.message = message
                it.actionText = actionText

                it.closeBtn.setOnClickListener { dismiss() }
                it.actionBtn.setOnClickListener {
                    dismiss()
                    action()
                }
            }.root
        )

        window?.run {
            setLayout((Resources.getSystem().displayMetrics.widthPixels * 0.8).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }
}