package com.svap.chat.ui.authenticate.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.svap.chat.base.BaseViewModel
import com.svap.chat.ui.authenticate.model.CountryModel
import com.svap.chat.utils.app_enum.ErrorType
import com.svap.chat.utils.app_enum.ProgressAction
import com.svap.chat.utils.country.Country
import okhttp3.RequestBody.Companion.toRequestBody

class AuthViewModel() : BaseViewModel( ) {

    private val _mAllCountries = MutableLiveData<ArrayList<Country>>()
    val mAllCountries: LiveData<ArrayList<Country>> get() = _mAllCountries

    fun getAllCountries() {
        requestData(
            action = { apiService.getCountryList() },
            success = { it.data?.let { result -> _mAllCountries.postValue(result) } },
            progressAction = ProgressAction.PROGRESS_DIALOG,
            errorType = ErrorType.DIALOG,
        )
    }

    fun registerUser(map: HashMap<String, String>,success:()->Unit) {
        requestData(
            action = { apiService.signUp(map) },
            success = {
                it.data?.let { result ->
                    mSharedPresenter.token =result.token
                    mSharedPresenter.saveCurrentUser(result.user)
                    success()
                }
            },
            progressAction = ProgressAction.PROGRESS_DIALOG,
            errorType = ErrorType.DIALOG,
        )
    }

    fun login(map: HashMap<String, String>,success:()->Unit) {
        requestData(
            action = { apiService.login(map) },
            success = {
                it.data?.let { result ->
                    mSharedPresenter.token =result.token
                    mSharedPresenter.chooseCountryId = result.user.country_id
                    mSharedPresenter.chooseCountryName = result.user.residential_country
                    mSharedPresenter.saveCurrentUser(result.user)
                    success()
                }
            },
            progressAction = ProgressAction.PROGRESS_DIALOG,
            errorType = ErrorType.DIALOG,
        )
    }

    fun forgotPassword(email: String,success:(otp:String)->Unit) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("email",email)
        requestData(
            action = { apiService.forgotPassword(email) },
            success = {
                it.data?.let { result ->
                    success(result.OTP)
                }
            },
            progressAction = ProgressAction.PROGRESS_DIALOG,
            errorType = ErrorType.DIALOG,
        )
    }

    fun verifyOtp(email: String,otp:String,success:()->Unit) {
        requestData(
            action = { apiService.verifyOtp(email,otp) },
            success = {
                success()
            },
            progressAction = ProgressAction.PROGRESS_DIALOG,
            errorType = ErrorType.DIALOG,
        )
    }

    fun resetPassword(email: String,password:String,success:(str:String)->Unit) {
        requestData(
            action = { apiService.resetPassword(email,password) },
            success = {
                success(it.message)
            },
            progressAction = ProgressAction.PROGRESS_DIALOG,
            errorType = ErrorType.DIALOG,
        )
    }

}