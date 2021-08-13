package com.svap.chat.coinDi.module

import android.content.Context
import com.svap.chat.utils.AppPreferencesHelper
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val appModule = module {
    single {
        val PREF_NAME = "com.chat"
        val MODE = Context.MODE_PRIVATE
        androidContext().getSharedPreferences(PREF_NAME, MODE)
    }

    single {
        AppPreferencesHelper(get())
    }

}