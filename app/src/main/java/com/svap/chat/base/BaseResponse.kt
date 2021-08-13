package com.svap.chat.base


data class BaseDataModel<T>(
    val statusCode: Int = 0,
    val status: Boolean = false,
    val message: String = "0",
    val data: T? = null
)

class EmptyData {

}