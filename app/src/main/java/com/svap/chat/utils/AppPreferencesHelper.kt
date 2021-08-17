package com.svap.chat.utils

import android.content.SharedPreferences
import com.google.gson.Gson
import com.svap.chat.ui.authenticate.model.UserData

class AppPreferencesHelper
internal constructor(val mPrefs: SharedPreferences) {

    fun clearData() {
        mPrefs.edit().clear().apply()
    }

    var deviceId: String
        get() = mPrefs.getString("deviceId", "") ?: ""
        set(value) = mPrefs.edit().putString("deviceId", value).apply()

    var deviceToken: String
        get() = mPrefs.getString("deviceToken", "") ?: ""
        set(value) = mPrefs.edit().putString("deviceToken", value).apply()

    var chooseCountry: Boolean
        get() = mPrefs.getBoolean("chooseCountry", false)
        set(value) = mPrefs.edit().putBoolean("chooseCountry", value).apply()


    var chooseCountryId: String
        get() = mPrefs.getString("chooseCountryId", "") ?: ""
        set(value) = mPrefs.edit().putString("chooseCountryId", value).apply()

    var chooseCountryName: String
        get() = mPrefs.getString("chooseCountryName", "") ?: ""
        set(value) = mPrefs.edit().putString("chooseCountryName", value).apply()

    var chooseCountryFlag: Int
        get() = mPrefs.getInt("chooseCountryFlag", 0)
        set(value) = mPrefs.edit().putInt("chooseCountryFlag", value).apply()


    var isLoggedIn: Boolean
        get() = mPrefs.getBoolean("isLogged", false)
        set(value) = mPrefs.edit().putBoolean("isLogged", value).apply()

    var token: String
        get() = mPrefs.getString("token", "") ?: ""
        set(value) = mPrefs.edit().putString("token", value).apply()

    var allCountry: String
        get() = mPrefs.getString("allCountry", "[]") ?: ""
        set(value) = mPrefs.edit().putString("allCountry", value).apply()

    var name: String
        get() = mPrefs.getString("name", "") ?: ""
        set(value) = mPrefs.edit().putString("name", value).apply()

    var currentUser: String
        get() = mPrefs.getString("currentUser", "{}") ?: ""
        set(value) = mPrefs.edit().putString("currentUser", value).apply()

    var imagePath: String
        get() = mPrefs.getString("imagePath", "") ?: ""
        set(value) = mPrefs.edit().putString("imagePath", value).apply()

    fun saveCurrentUser(userData: UserData) {
        currentUser = Gson().toJson(userData)
    }

    fun getCurrentUser(): UserData {
        return Gson().fromJson(currentUser, UserData::class.java)
    }
}