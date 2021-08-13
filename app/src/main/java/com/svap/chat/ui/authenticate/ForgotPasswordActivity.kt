package com.svap.chat.ui.authenticate

import android.content.Intent
import android.text.TextUtils
import com.fantasy.utils.extentions.goto
import com.svap.chat.R
import com.svap.chat.base.BaseVmActivity
import com.svap.chat.databinding.ActivityForgotPasswordBinding
import com.svap.chat.ui.authenticate.viewModel.AuthViewModel
import com.svap.chat.utils.app_enum.Extra
import com.svap.chat.utils.extentions.getString
import com.svap.chat.utils.extentions.showToast

class ForgotPasswordActivity : BaseVmActivity<ActivityForgotPasswordBinding, AuthViewModel>
    (R.layout.activity_forgot_password, AuthViewModel::class) {

    override fun initListener() {
        super.initListener()

        mBinding.btnNext.setOnClickListener {
            val email = mBinding.etEmail.getString()
            if (TextUtils.isEmpty(email)) {
                showToast("Enter valid email")
                return@setOnClickListener
            }
            mViewModel.forgotPassword(email) {
                showToast(it)
                goto(Intent(this,
                    OtpVerificationActivity::class.java).apply {
                    putExtra(Extra.EMAIL, email)
                })
            }
        }
    }

}
