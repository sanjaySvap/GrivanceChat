package com.svap.chat.ui.home.viewModel

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.svap.chat.base.BaseViewModel
import com.svap.chat.ui.authenticate.model.UserData
import com.svap.chat.utils.app_enum.ErrorType
import com.svap.chat.utils.app_enum.ProgressAction
import com.svap.chat.utils.extentions.toMultiPartFile
import okhttp3.RequestBody
import java.io.File

class ProfileViewModel() : BaseViewModel() {

    private val _mUser = MutableLiveData<UserData>()
    val mUser: LiveData<UserData> = _mUser


    fun  getProfile(){
        requestData(
            action = {
               apiService.getProfile()
            },
            success = {
                it.data?.info?.let {user->
                    _mUser.postValue(user)
                }
            },
            progressAction = ProgressAction.NONE,
            errorType = ErrorType.DIALOG,
        )
    }
    fun editProfile(
        map: HashMap<String, RequestBody>,
        imagePath: String,
        success: (str: String) -> Unit
    ) {
        requestData(
            action = {
                if (TextUtils.isEmpty(imagePath)) {
                    apiService.editProfile(map,null)
                } else {
                    apiService.editProfile(
                        map, File(imagePath).toMultiPartFile(
                            "file",
                            type = "image/jpeg")
                    )
                }
            },
            success = {
                success(it.message)
                getProfile()
            },
            progressAction = ProgressAction.PROGRESS_DIALOG,
            errorType = ErrorType.DIALOG,
        )
    }



}