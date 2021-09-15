package com.svap.chat.ui.dialog

import android.app.Dialog
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.svap.chat.databinding.DialogBioBinding

class DialogBio(view: View, message: String) : Dialog(view.context) {
    private val viewDataBinding: DialogBioBinding by lazy {
        DialogBioBinding.inflate(layoutInflater, view.parent as ViewGroup, false)
    }

    init {
        setContentView(
            viewDataBinding.also {
                it.message = message
                it.closeBtn.setOnClickListener { dismiss() }
            }.root
        )

        window?.run {
            setLayout((Resources.getSystem().displayMetrics.widthPixels * 0.8).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }
}