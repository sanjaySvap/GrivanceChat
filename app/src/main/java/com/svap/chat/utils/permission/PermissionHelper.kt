package com.svap.chat.utils.permission

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import java.util.ArrayList

class PermissionHelper(private val mActivity: Activity) {

    val mTAG = "checkPermissions"

    fun checkPermissions(permissions: Array<String>, success: (isAllow: Boolean) -> Unit = {}) {
        val permissionHandler = object : PermissionCallback() {
            override fun onGranted() {
                success(true)
            }

            override fun onDenied(context: Context?, deniedPermissions: ArrayList<String>) {
                super.onDenied(context, deniedPermissions)
                success(false)
            }

            override fun onJustBlocked(
                context: Context?,
                justBlockedList: ArrayList<String>,
                deniedPermissions: ArrayList<String>
            ) {
                super.onJustBlocked(context, justBlockedList, deniedPermissions)
                success(false)
            }
        }

        Permissions.check(
            mActivity,
            permissions,
            null,
            null,
            permissionHandler
        )

    }
}