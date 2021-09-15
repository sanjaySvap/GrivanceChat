package com.svap.chat.ui.chat.model

import com.svap.chat.ui.authenticate.model.UserData
import com.svap.chat.ui.authenticate.model.UserDataModel


data class MessageDataModel(
        val getmsgreceived:ArrayList<MessageModel> = arrayListOf()
)


data class MessageModel(
        val id: String = "",
        val from: String = "",
        val to: String = "",
        val message: String = "",
        val message_type: String = "",
        val type: String = "",
        val isRead: Int = 0,
        var file: String = "",
        var timestamp: String = "0",
        var created_at: String = "0",
        var updated_at: String = "0"
)



data class UserResponse(
        val userChatList: ArrayList<HomeUser> ?= ArrayList(),
        val error: Boolean
)

data class ChatUserResponse(
        val recentChatList: ArrayList<UsersResult> ?= ArrayList(),
        val error: Boolean
)

data class AllMessageResponse(
        val userChat: ArrayList<MessageModel> ?= ArrayList(),
        val error: Boolean
)

data class UsersResult(
        val id:String = "",
        val first_name:String = "",
        val last_name:String = "",
        val mobile_no:String = "",
        val email:String = "",
        val last_sent_date:String = "",
        val user_name:String = "",
        val image_name:String = "",
        var socket_id: String? = "",
        var bio: String? = "",
        var is_online: Int? = 0,
        var is_read: Int? = 0,
        val message:String = "",
)

data class HomeUser(
        var id: String? = "",
        var user_name: String? = "",
        var first_name: String? = "",
        var image_name: String? = "",
        var age: String? = "",
        var bio: String? = "",
        var is_online: Int? = 0,
        var socket_id: String? = "",
        var Preference: String? = "",
)
