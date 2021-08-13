package com.mf.utils.networkRequest

import android.util.Log
import com.google.gson.Gson
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import org.json.JSONException
import org.json.JSONObject

/**
 * Will handel all api response and error
 */
abstract class CallbackWrapper<T> : Observer<T> {
    abstract fun onSuccess(t: T)
    abstract fun onError(message: String?, statusCode: Int)
    fun onCompleted() {}

    override fun onError(e: Throwable) {
        if (e.message!!.contains("No Internet Connection")) {
            onError(e.message, -200)
        } else {
            onError(e.message, 0)
        }
        Log.e("error", e.message!!)
    }

    override fun onNext(t: T) {
        val errorCheckModel = checkError(t)
        Log.e("callbackresponse", Gson().toJson(t))
        if (errorCheckModel.status) {
            onSuccess(t)
        } else {
            onError(errorCheckModel.message, errorCheckModel.statusCode)
        }
    }

    private fun <T> checkError(data: T): ErrorCheckModel {
        val errorCheckModel = ErrorCheckModel()
        try {
            val `object` = JSONObject(Gson().toJson(data))
            if (`object`.has("status")) {
                errorCheckModel.status = `object`.getBoolean("status")
            } else {
                if (`object`.has("error")) {
                    errorCheckModel.status = !`object`.getBoolean("error")
                }
            }
            if (`object`.has("message")) {
                errorCheckModel.message = `object`.getString("message")
            }
            if (`object`.has("statusCode")) {
                errorCheckModel.statusCode = `object`.getInt("statusCode")
            } else {
                if (`object`.has("status_code")) {
                    errorCheckModel.statusCode = `object`.getInt("status_code")
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return errorCheckModel
    }

    private inner class ErrorCheckModel {
        var status = false
        var statusCode = 0
        var message = ""
    }

    override fun onComplete() {
    }

    override fun onSubscribe(d: Disposable?) {
    }
}