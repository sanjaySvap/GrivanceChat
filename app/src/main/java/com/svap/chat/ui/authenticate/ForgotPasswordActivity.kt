package com.svap.chat.ui.authenticate

import android.content.Intent
import android.text.TextUtils
import com.fantasy.utils.extentions.goto
import com.svap.chat.R
import com.svap.chat.base.BaseVmActivity
import com.svap.chat.databinding.ActivityForgotPasswordBinding
import com.svap.chat.ui.authenticate.viewModel.AuthViewModel
import com.svap.chat.utils.ValidationHelper
import com.svap.chat.utils.app_enum.Extra
import com.svap.chat.utils.extentions.getString
import com.svap.chat.utils.extentions.showSnackbar
import com.svap.chat.utils.extentions.showToast

class ForgotPasswordActivity : BaseVmActivity<ActivityForgotPasswordBinding, AuthViewModel>
    (R.layout.activity_forgot_password, AuthViewModel::class) {

    override fun initListener() {
        super.initListener()

        mBinding.btnNext.setOnClickListener {
            val email = mBinding.etEmail.getString()
            if (email.isEmpty()) {
                mBinding.root.showSnackbar("Please Enter your Email ID")
                return@setOnClickListener
            } else if (!ValidationHelper.validateEmail(email)) {
                mBinding.root.showSnackbar("Please Enter valid Email")
                return@setOnClickListener
            }

            mViewModel.forgotPassword(email) {
                goto(Intent(this,
                    OtpVerificationActivity::class.java).apply {
                    putExtra(Extra.EMAIL, email)
                })
            }
        }
    }

}
