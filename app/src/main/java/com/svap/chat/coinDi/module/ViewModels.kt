package com.svap.chat.coinDi.module

import com.svap.chat.ui.authenticate.viewModel.AuthViewModel
import com.svap.chat.ui.home.viewModel.HomeViewModel
import com.svap.chat.ui.home.viewModel.ProfileViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val myViewModel = module {

    viewModel { AuthViewModel() }
    viewModel { HomeViewModel() }
    viewModel { ProfileViewModel() }

}