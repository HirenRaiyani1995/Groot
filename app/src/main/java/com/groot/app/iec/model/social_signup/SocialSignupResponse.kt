package com.groot.app.iec.model.social_signup

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class SocialSignupResponse {
    @SerializedName("code")
    var code = 0

    @SerializedName("message")
    var message: String? = null

    @SerializedName("user")
    var user: User? = null

    @SerializedName("token")
    var token: String? = null
}