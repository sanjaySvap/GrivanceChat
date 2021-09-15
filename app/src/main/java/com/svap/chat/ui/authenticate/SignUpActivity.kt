package com.svap.chat.ui.authenticate

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View.OnTouchListener
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import com.fantasy.utils.extentions.gotoNewTask
import com.svap.chat.R
import com.svap.chat.base.BaseVmActivity
import com.svap.chat.databinding.ActivitySignupBinding
import com.svap.chat.ui.authenticate.viewModel.AuthViewModel
import com.svap.chat.utils.ValidationHelper
import com.svap.chat.utils.country.Country
import com.svap.chat.utils.country.getCountryFlag
import com.svap.chat.utils.extentions.getString
import com.svap.chat.utils.extentions.showSnackbar
import com.svap.chat.utils.extentions.toDatePicker
import java.util.*
import kotlin.collections.ArrayList


class SignUpActivity : BaseVmActivity<ActivitySignupBinding, AuthViewModel>(
        R.layout.activity_signup,
        AuthViewModel::class
) {

    private val mAllCountriesList = ArrayList<Country>()
    private val mSheet: CountryBottomSheet by lazy {
        CountryBottomSheet(
                mAllCountriesList
        ) {
            mBinding.tvCountry.text = it.Name
            mCountry = it.Name
        }
    }

    private var mCountry: String = ""
    private var mDob: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observeData()
        mViewModel.getAllCountries()
    }

    fun observeData() {
        mViewModel.mAllCountries.observe(this, Observer {
            mAllCountriesList.clear()
            mAllCountriesList.addAll(it)
            mAllCountriesList.map {
                it.Flag = getCountryFlag(it.SortName)
            }
        })
    }

    override fun initListener() {
        super.initListener()

        mBinding.etBio.setOnTouchListener(OnTouchListener { v, event ->
            if (mBinding.etBio.hasFocus()) {
                v.parent.requestDisallowInterceptTouchEvent(true)
                when (event.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_SCROLL -> {
                        v.parent.requestDisallowInterceptTouchEvent(false)
                        return@OnTouchListener true
                    }
                }
            }
            false
        })

        mBinding.tvSignUp.setOnClickListener {
            signUp()
        }
        mBinding.tvLogin.setOnClickListener {
            onBackPressed()
        }

        mBinding.etDob.setOnClickListener {
            mBinding.etDob.toDatePicker { mDob = it }
        }

        mBinding.tvCountry.setOnClickListener {
            mSheet.show(supportFragmentManager, "")
        }

        mBinding.etBio.doAfterTextChanged {
            mBinding.tvWord.text = "${it.toString().length}"
        }
    }

    private fun signUp() {
        mBinding.run {
            val firstName = mBinding.etFirstName.getString()
            val lastName = mBinding.etLastName.getString()
            val email = mBinding.etEmail.getString()
            val mobile = mBinding.etMobie.getString()
            val password = mBinding.etPassword.getString()
            val bio = mBinding.etBio.getString()


            if (firstName.isEmpty()) {
                mBinding.root.showSnackbar("Please Enter your first name")
                return@run
            } else if (!ValidationHelper.validatePersonName(firstName)) {
                mBinding.root.showSnackbar("Please Enter valid first name")
                return@run
            }

            if (lastName.isEmpty()) {
                mBinding.root.showSnackbar("Please Enter your last name")
                return@run
            } else if (!ValidationHelper.validatePersonName(lastName)) {
                mBinding.root.showSnackbar("Please Enter valid last name")
                return@run
            }

            if (email.isEmpty()) {
                mBinding.root.showSnackbar("Please Enter your Email ID")
                return@run
            } else if (!ValidationHelper.validateEmail(email)) {
                mBinding.root.showSnackbar("Please Enter valid Email")
                return@run
            }

            if (mobile.isEmpty()) {
                mBinding.root.showSnackbar("Please Enter your mobile number")
                return@run
            }

            if (mDob.isEmpty()) {
                mBinding.root.showSnackbar("Please Enter your date of birth")
                return@run
            }

            if (mCountry.isEmpty()) {
                mBinding.root.showSnackbar("Please Select your country")
                return@run
            }

            if (password.isEmpty() || password.length < 8) {
                mBinding.root.showSnackbar("Please Enter valid password")
                return@run
            }
            val msg = ValidationHelper.validatePassword(password)
            if (!TextUtils.isEmpty(msg)) {
                mBinding.root.showSnackbar(msg ?: "")
                return@run
            }

            if (password != etCnfPassword.getString()) {
                mBinding.root.showSnackbar("Password should be matched")
                return@run
            }

            mViewModel.registerUser(
                    hashMapOf(
                            "user_name" to firstName + lastName + "_" + UUID.randomUUID().toString(),
                            "first_name" to firstName,
                            "last_name" to lastName,
                            "email" to email,
                            "mobile_no" to mobile,
                            "dob" to mDob,
                            "bio" to bio,
                            "residential_Country" to mCountry,
                            "password" to password
                    )
            ) {
                gotoNewTask(
                        Intent(this@SignUpActivity, LoginActivity::class.java).apply {
                            putExtra("show", true)
                        }
                )
            }
        }
    }

}
