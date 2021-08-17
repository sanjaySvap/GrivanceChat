package com.svap.chat.ui.authenticate

import android.content.Intent
import android.os.Bundle
import com.fantasy.utils.extentions.goto
import com.fantasy.utils.extentions.gotoNewTask
import com.svap.chat.R
import com.svap.chat.base.BaseVmActivity
import com.svap.chat.databinding.ActivityLoginBinding
import com.svap.chat.ui.authenticate.viewModel.AuthViewModel
import com.svap.chat.ui.home.HomeActivity
import com.svap.chat.utils.ValidationHelper
import com.svap.chat.utils.extentions.getString
import com.svap.chat.utils.extentions.showSnackbar

class LoginActivity : BaseVmActivity<ActivityLoginBinding, AuthViewModel>(
        R.layout.activity_login,
        AuthViewModel::class
) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.viewModel = mViewModel
    }

    override fun initListener() {
        super.initListener()
        mBinding.tvLogin.setOnClickListener {
            login()
        }
        mBinding.tvRegister.setOnClickListener {
            goto(SignUpActivity::class.java)
        }

        mBinding.tvForgot.setOnClickListener {
            goto(ForgotPasswordActivity::class.java)
        }
    }

    private fun login() {
        mBinding.run {
            val userName = mBinding.et1.getString()
            val password = mBinding.et2.getString()
            if (userName.isEmpty() ) {
                return@run
            }else if(!ValidationHelper.validateEmail(userName)){
                mBinding.root.showSnackbar("Please Enter valid Email")
                return@run
            }
            if (password.isEmpty()) {
                return@run
            }

            mViewModel.login(
                    hashMapOf(
                            "user_name" to userName,
                            "password" to password,
                            "device_type" to "android",
                            "device_token" to mSharePresenter.deviceToken,
                    )
            ) {
                gotoNewTask(
                        Intent(this@LoginActivity, HomeActivity::class.java).apply {
                            putExtra("show", intent.getBooleanExtra("show", false))
                        }
                )
            }
        }
    }

}
