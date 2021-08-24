package com.svap.chat.ui.authenticate.model



data class CountryModel(
    var Id: String = "",
    var Name: String = "",
    var Flag:Int=0,
    var ShortName: String = "",
    var IsActive: Boolean = false,
    var IsSelected: Boolean = false
)

class ForgotPasswordModel {
    val OTP:String = ""
}

class UserDataModel {
    val token:String = ""
    val user:UserData=UserData()
}

class ProfileDataModel {
    val info:UserData=UserData()
}

class UserData(
    val id:String = "",
    val first_name:String = "",
    val last_name:String = "",
    val mobile_no:String = "",
    val email:String = "",
    val dob:String = "",
    val user_name:String = "",
    val image_name:String = "",
    val residential_country:String = "",
    val country_id:String = "",
    val gender:String = "",
    val device_type:String = "",
    val device_token:String = "",
    val lat:String = "",
    val lng:String = "",
    val address:String = "",
    val city:String = "",
    val social_id:String = "",
    val social_type:String = "",
    val is_email_verified:String = "",
    val is_mobile_no_verified:String = "",
    val is_active:String = "",
    val Preference:String = "",
    val last_sent_msg:String = "",
    val created_at:String = "",
    val updated_at:String = "",
)


class BlockModel {
    val is_block:Boolean = false
}


