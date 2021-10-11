package com.fantasy.utils.extentions

import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.DisplayMetrics
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.svap.chat.R
import com.svap.chat.databinding.DialogOptionsBinding
import com.svap.chat.ui.authenticate.model.CountryModel
import com.svap.chat.utils.country.Country
import com.svap.chat.utils.extentions.gone
import java.util.ArrayList

fun Activity.goto(intent: Intent, rq: Int = -1) =
        if (rq == -1) startActivity(intent) else startActivityForResult(intent, rq)

fun Activity.gotoFinish(intent: Intent) {
    startActivity(intent)
    finish()
}

fun Activity.gotoNewTask(intent: Intent) {
    startActivity(intent.apply {
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
    })
    finish()
}

fun Activity.goto(cls: Class<*>, rq: Int = -1) =
        if (rq == -1) startActivity(Intent(this, cls)) else startActivityForResult(
                Intent(this, cls),
                rq
        )

fun Activity.gotoFinish(cls: Class<*>) {
    startActivity(Intent(this, cls))
    finish()
}

fun Activity.gotoNewTask(cls: Class<*>) {
    startActivity(Intent(this, cls).apply {
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
    })
    finish()
}

fun Activity.showKeyboard() {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view = currentFocus
    if (view == null) {
        view = View(this)
    }
    imm.showSoftInput(view, 0)
}

fun Activity.hideKeyboard() {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view = currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(this)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Activity.getDisplayMatrix(): DisplayMetrics {
    val displayMatrix = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(displayMatrix)
    return displayMatrix
}

fun Activity.copyToClipboard(message: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
    clipboard?.setPrimaryClip(ClipData.newPlainText("Referral Code", message))
    Toast.makeText(this, "Referral Code Copied", Toast.LENGTH_SHORT).show()
}


fun <D : ViewDataBinding> Context.getDialogWithBinding(
        layout: Int, cancelAble: Boolean = true, callBack: (
                dialog: Dialog, binding: D
        ) -> Unit
) {
    val binding: D = DataBindingUtil.inflate(
            LayoutInflater.from(this),
            layout,
            null,
            false
    )
    callBack(
            Dialog(this).apply {
                window?.requestFeature(Window.FEATURE_NO_TITLE)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setCancelable(cancelAble)
                setCanceledOnTouchOutside(cancelAble)
                setContentView(binding.root)

            }, binding
    )

}

fun Activity.hideSystemUI() {
    window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
            // Set the content to appear under the system bars so that the
            // content doesn't resize when the system bars hide and show.
            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            // Hide the nav bar and status bar
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN)
}

// Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
fun Activity.showSystemUI() {
    window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
}



fun Activity.makeStatusBarTransparent(isLightStatusBar: Boolean = true) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = if (isLightStatusBar){
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }else {
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                }
            } else {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
            statusBarColor = Color.TRANSPARENT
        }
    }
}

fun Activity.changeStatusBarColor(color:Int){
   /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this,color)
    }*/
}


fun Activity.getAllCountry(str: String): ArrayList<Country> {
    val myType = object : TypeToken<ArrayList<Country>>() {}.type
    return Gson().fromJson(str, myType)
}


fun Context.optionDialog(
    message: String,
    cancelAble: Boolean = true,
    primaryKey: String = "Yes",
    secondaryKey: String = "No",
    primaryKeyAction: () -> Unit,
    secondaryKeyAction: () -> Unit = {},
): Dialog {

    val binding: DialogOptionsBinding = DataBindingUtil.inflate(
        LayoutInflater.from(this),
        R.layout.dialog_options,
        null,
        false
    )

    return Dialog(this).apply {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(cancelAble)
        setCanceledOnTouchOutside(cancelAble)
        setContentView(binding.root)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        lp.gravity = Gravity.CENTER
        window?.attributes = lp
        window?.setBackgroundDrawable(
            ColorDrawable(
                Color.TRANSPARENT
            )
        )
        binding.run {
            actionBtn.text = primaryKey
            actionBtnSkip.text = secondaryKey
            if (secondaryKey == "") {
                actionBtnSkip.gone()
            }
            tvMsg.text = message
            actionBtn.setOnClickListener{
                dismiss()
                primaryKeyAction()
            }
            actionBtnSkip.setOnClickListener {
                dismiss()
                secondaryKeyAction()
            }
        }

        show()
    }
}

fun Activity.shareApp(shareText: String) {
    ShareCompat.IntentBuilder.from(this)
        .setType("text/plain")
        .setChooserTitle("Share By")
        .setText(shareText)
        .startChooser()
}



