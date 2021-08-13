package com.svap.chat.ui.authenticate

import android.os.Bundle
import android.text.TextUtils
import com.fantasy.utils.extentions.goto
import com.fantasy.utils.extentions.gotoNewTask
import com.svap.chat.R
import com.svap.chat.base.BaseVmActivity
import com.svap.chat.databinding.ActivityChangePasswordBinding
import com.svap.chat.ui.authenticate.viewModel.AuthViewModel
import com.svap.chat.ui.home.HomeActivity
import com.svap.chat.utils.app_enum.Extra
import com.svap.chat.utils.extentions.getString
import com.svap.chat.utils.extentions.showSnackbar
import com.svap.chat.utils.extentions.showToast

class ChangePasswordActivity : BaseVmActivity<ActivityChangePasswordBinding, AuthViewModel>(
        R.layout.activity_change_password,
    AuthViewModel::class
    ) {

    private val mEmail: String by lazy { intent.getStringExtra(Extra.EMAIL) ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.viewModel = mViewModel
    }

    override fun initListener() {
        super.initListener()
        mBinding.tvLogin.setOnClickListener {
            val password = mBinding.etP1.getString()
            val password2 = mBinding.etP2.getString()
            if (TextUtils.isEmpty(password) || password.length <8) {
                showToast("Enter valid password")
                return@setOnClickListener
            }
            if(password != password2){
                showToast("Password should be matched")
                return@setOnClickListener
            }

            mViewModel.resetPassword(mEmail,password){
                showToast(it)
                gotoNewTask(LoginActivity::class.java)
            }
        }
    }

}
