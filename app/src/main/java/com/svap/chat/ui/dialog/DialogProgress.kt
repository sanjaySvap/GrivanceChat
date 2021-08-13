package com.svap.chat.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.svap.chat.databinding.DialogProgressViewBinding

class DialogProgress(context: Context, message: String) : Dialog(context) {
    private val viewDataBinding: DialogProgressViewBinding by lazy {
        DialogProgressViewBinding.inflate(layoutInflater)
    }

    init {
        setContentView(
            viewDataBinding.also {
                it.message = message
            }.root
        )

        window?.run {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCanceledOnTouchOutside(false)
        }
    }
}