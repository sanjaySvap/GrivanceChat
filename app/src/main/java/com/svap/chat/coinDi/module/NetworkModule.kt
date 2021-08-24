package com.svap.chat.coinDi.module

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import com.google.gson.GsonBuilder
import com.svap.chat.BuildConfig
import com.svap.chat.utils.API_LOCAL
import com.svap.chat.utils.AppPreferencesHelper
import com.svap.chat.utils.networkRequest.ApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

val networkModule = module {

    single {
        val fac = GsonConverterFactory.create(GsonBuilder().setLenient().create())
        Retrofit.Builder()
            .baseUrl(API_LOCAL)
            .client(get())
            .addConverterFactory(fac)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
    }

    single {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        OkHttpClient.Builder().addInterceptor(logging)
            .readTimeout(120, TimeUnit.SECONDS)
            .connectTimeout(120, TimeUnit.SECONDS)
            .addInterceptor(ConnectivityInterceptor(androidContext().applicationContext))
            .addInterceptor { chain ->
                val prehelper:AppPreferencesHelper = get()
                Log.d("device_token"," "+prehelper.deviceToken)
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", prehelper.token)
                    .addHeader("language", "en")
                    .addHeader("version", BuildConfig.VERSION_NAME)
                    .addHeader("platform", "android")
                    .addHeader("device_id", prehelper.deviceId)
                    .addHeader("device_token", prehelper.deviceToken)
                    .addHeader("device_type", "android")

                    .build()
                chain.proceed(request)
            }
            .build()
    }


    single {
        val ret: Retrofit = get()
        ret.create(ApiService::class.java)
    }
}

class ConnectivityInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isOnline(context)) {
            throw NoConnectivityException()
        }
        val builder: Request.Builder = chain.request().newBuilder()
        return chain.proceed(builder.build())
    }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val networkCapabilities =
                connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            val nwInfo = connectivityManager.activeNetworkInfo ?: return false
            return nwInfo.isConnected
        }
    }
}

class NoConnectivityException : IOException() {
    override val message: String?
        get() = "No Internet Connection - Try Again"
}