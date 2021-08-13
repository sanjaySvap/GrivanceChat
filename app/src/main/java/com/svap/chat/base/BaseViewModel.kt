package com.svap.chat.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svap.chat.utils.AppPreferencesHelper
import com.svap.chat.utils.app_enum.ErrorType
import com.svap.chat.utils.app_enum.ProgressAction
import com.svap.chat.utils.networkRequest.ApiService
import com.svap.chat.utils.networkRequest.ErrorDataModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.context.GlobalContext
import org.koin.java.KoinJavaComponent
import retrofit2.Response


abstract class BaseViewModel() : ViewModel() {
    protected val apiService: ApiService by KoinJavaComponent.inject(clazz = ApiService::class.java)
    protected val mSharedPresenter: AppPreferencesHelper = GlobalContext.get().get()

    private val _errorMessage = MutableLiveData<ErrorDataModel>()
    val errorMessage: LiveData<ErrorDataModel> get() = _errorMessage

    private val _progressHandler = MutableLiveData<ProgressAction>()
    val progressHandler: LiveData<ProgressAction> get() = _progressHandler

    fun <T> requestData(
        action: suspend () -> Response<BaseDataModel<T>>,
        success: (m: BaseDataModel<T>) -> Unit,
        progressAction: ProgressAction = ProgressAction.PROGRESS_BAR,
        errorType: ErrorType = ErrorType.NONE,
    ) {
        if (progressAction != ProgressAction.NONE)
            _progressHandler.value = ProgressAction.PROGRESS_DIALOG //todo progressAction

        viewModelScope.launch {
            try {
                val requestResponse = withContext(Dispatchers.IO) { action() }
                if (requestResponse.isSuccessful) {
                    requestResponse.body()?.let {
                        if (it.status) success(it)
                        else handleError(it.message, errorType)
                    }
                } else {
                    handleError(requestResponse.message(), errorType)
                }

            } catch (exception: Exception) {
                exception.printStackTrace()
                handleError(exception.message, errorType)
            } finally {
                if (progressAction != ProgressAction.NONE)
                    _progressHandler.value = ProgressAction.DISMISS
            }
        }
    }

    private fun handleError(message: String?, errorType: ErrorType) {
        _errorMessage.postValue(ErrorDataModel(message ?: "", errorType))
    }

}