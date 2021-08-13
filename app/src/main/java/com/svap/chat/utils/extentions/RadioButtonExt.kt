package com.svap.chat.utils.extentions

import android.widget.RadioButton
import androidx.core.content.ContextCompat

fun RadioButton.handleCheckBoxColor(textColor: Int, activeTextColor: Int) {
    this.setOnCheckedChangeListener { _, isChecked ->
        this.setTextColor(
            ContextCompat.getColor(
            this.context,
            if(isChecked) activeTextColor else textColor
        ))
    }
}