package com.svap.chat.utils.networkRequest

import com.google.gson.JsonObject
import com.svap.chat.base.BaseDataModel
import com.svap.chat.ui.authenticate.model.*
import com.svap.chat.utils.country.Country
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.*


interface ApiService {
    companion object {
        const val PRIORITY_DEFAULT = 0
        const val PRIORITY_HIGH = 1
        const val PRIORITY_MEDIUM = 2
        const val PRIORITY_LOW = 3
    }

    @GET("auth/country_list")
    suspend fun getCountryList(): Response<BaseDataModel<ArrayList<Country>>>

    @FormUrlEncoded
    @POST("auth/signup")
    suspend fun signUp(
        @FieldMap map: HashMap<String, String>,
    ): Response<BaseDataModel<UserDataModel>>

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @FieldMap map: HashMap<String, String>,
    ): Response<BaseDataModel<UserDataModel>>

    @Multipart
    @PUT("user/edit_profile")
    suspend fun editProfile(
        @PartMap map: HashMap<String, RequestBody>,
        @Part file: MultipartBody.Part?
    ): Response<BaseDataModel<Any>>

    @PUT("user/edit_profile")
    suspend fun editProfile(
        @QueryMap map: HashMap<String, RequestBody>,
    ): Response<BaseDataModel<Any>>


    @GET("user/get_profile")
    suspend fun getProfile(): Response<BaseDataModel<ProfileDataModel>>


    @POST("auth/forgotPassword")
    suspend fun forgotPassword(
        @Query("email") email: String
    ): Response<BaseDataModel<ForgotPasswordModel>>

    @PUT("auth/resetPassword/")
    suspend fun resetPassword(
        @Query("email") email: String,
        @Query("password") password: String
    ): Response<BaseDataModel<Any>>


    @PUT("auth/reset-password/verify-otp/")
    suspend fun verifyOtp(
        @Query("email")email:String,
        @Query("otp")otp:String,
    ): Response<BaseDataModel<Any>>


}