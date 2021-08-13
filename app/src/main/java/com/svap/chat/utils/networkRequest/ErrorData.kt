package com.svap.chat.utils.networkRequest

import com.svap.chat.utils.app_enum.ErrorType


data class ErrorData(
    val message: String? = "",
    val statusCode: Int = 0,
    val priority: Int = ApiService.PRIORITY_DEFAULT
)


data class ErrorDataModel(
    val message: String,
    val errorType: ErrorType,
)