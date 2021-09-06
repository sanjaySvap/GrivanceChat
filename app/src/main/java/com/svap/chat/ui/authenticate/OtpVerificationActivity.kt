package com.svap.chat.ui.authenticate

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.fantasy.utils.extentions.goto
import com.svap.chat.R
import com.svap.chat.base.BaseVmActivity
import com.svap.chat.databinding.ActivityOtpVerificationBinding
import com.svap.chat.ui.authenticate.viewModel.AuthViewModel
import com.svap.chat.utils.app_enum.Extra
import com.svap.chat.utils.extentions.showSnackbar
import com.svap.chat.utils.extentions.showToast

class OtpVerificationActivity : BaseVmActivity<ActivityOtpVerificationBinding, AuthViewModel>(
    R.layout.activity_otp_verification,
    AuthViewModel::class
) {

    private val mEmail: String by lazy { intent.getStringExtra(Extra.EMAIL) ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding.root.postDelayed({
            mBinding.root.showSnackbar("An OTP has been  shared on registered email")
        },500)
    }

    override fun initClicks() {
        super.initClicks()
        mBinding.verifyBtn.setOnClickListener {
            val otp = mBinding.otpView.otp
            if (TextUtils.isEmpty(otp)) {
                showToast("Please Enter OTP")
                return@setOnClickListener
            }

            mViewModel.verifyOtp(mEmail, otp ?: "") {
                val intent = Intent(this, ChangePasswordActivity::class.java)
                intent.putExtra(Extra.EMAIL, mEmail)
                goto(intent)
            }
        }
    }
}
