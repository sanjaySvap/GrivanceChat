package com.svap.chat.ui.dialog

import android.app.Dialog
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.svap.chat.databinding.DialogAlertBinding

class DialogAlert(view: View, title: String? = null,
                  message: String,
                  isCancelable: Boolean = true,
                  actionText: String,
                  actionNoText: String?="",
                  action: () -> Unit = {}
) : Dialog(view.context) {
    private val viewDataBinding: DialogAlertBinding by lazy {
        DialogAlertBinding.inflate(layoutInflater, view.parent as ViewGroup, false)
    }

    init {
        setContentView(
                viewDataBinding.also {
                    it.title = title
                    it.message = message
                    it.actionText = actionText
                    it.actionNoText = actionNoText

                    it.skipBtn.setOnClickListener {
                        dismiss()
                    }
                    it.actionBtn.setOnClickListener {
                        dismiss()
                        action()
                    }
                }.root
        )

        window?.run {
            setLayout((Resources.getSystem().displayMetrics.widthPixels * 0.8).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCanceledOnTouchOutside(isCancelable)
        }
    }
}