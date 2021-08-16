package com.svap.chat.utils

import android.util.Log
import java.util.regex.Pattern

object ValidationHelper {
    val NAME_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z\\s]+")
    private val EMAIL_PATTERN = Pattern.compile("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,20}")
    private val PASSWORD_PATTERN =
        Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])$")
    val NAME_TEAM_NAME = Pattern.compile("^[a-zA-Z0-9]*$")
    val MOBILE = Pattern.compile("[6-9][0-9]{9}")
    val GST_NUMER_PATTERN =
        Pattern.compile("^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}\$")

    fun validateGST(email: String): Boolean {
        return GST_NUMER_PATTERN.matcher(email).matches()
    }

    fun validateEmail(email: String): Boolean {
        return EMAIL_PATTERN.matcher(email).matches()
    }

    fun validateMobile(mobile: String): Boolean {
        return MOBILE.matcher(mobile).matches()
    }

    fun validateOTP(mobile: String): Boolean {
        return MOBILE.matcher(mobile).matches()
    }

    fun validatePassword(password: String): String? {
        var msg:String?=null
        if(password.length < 8){
            msg = "Please enter valid password"
        }
        if(!containsNumber(password)){
            msg = "Password must contain at least one number."
            Log.d("validatePassword", "msg "+msg)

        }
        if(!containsAlphabet(password)){
            msg = "Password must contain at least one alphabet."
            Log.d("validatePassword", "msg "+msg)

        }


       return msg
    }

    fun validatePersonName(name: String): Boolean {
        return NAME_PATTERN.matcher(name).matches()
    }

    private fun containsNumber(string: String): Boolean {
        return string.matches(".*\\d+.*".toRegex())
    }

    private fun containsAlphabet(string: String): Boolean {
        return string.matches(".*[a-zA-Z]+.*".toRegex())
    }
}