package com.svap.chat.ui.home.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.svap.chat.base.BaseViewModel
import com.svap.chat.ui.authenticate.model.CountryModel
import com.svap.chat.utils.app_enum.ErrorType
import com.svap.chat.utils.app_enum.ProgressAction
import com.svap.chat.utils.country.Country

class HomeViewModel() : BaseViewModel( ) {

    private val _mAllCountries = MutableLiveData<ArrayList<Country>>()
    val mAllCountries: LiveData<ArrayList<Country>> get() = _mAllCountries

    fun getAllCountries() {
        requestData(
            action = { apiService.getCountryList() },
            success = { it.data?.let { result -> _mAllCountries.postValue(result) } },
            progressAction = ProgressAction.PROGRESS_BAR.apply { message = "Please Wait..." },
            errorType = ErrorType.TOAST,
        )
    }

}