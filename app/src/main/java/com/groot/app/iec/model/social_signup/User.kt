package com.groot.app.iec.model.social_signup

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class User {
    @SerializedName("image")
    var image: String? = null

    @SerializedName("device_id")
    var deviceId: String? = null

    @SerializedName("updated_at")
    var updatedAt: String? = null

    @SerializedName("phone")
    var phone: String? = null

    @SerializedName("currency")
    var currency: String? = null

    @SerializedName("country")
    var country: String? = null

    @SerializedName("login_type")
    var loginType: String? = null

    @SerializedName("user_name")
    var userName: String? = null

    @SerializedName("created_at")
    var createdAt: String? = null

    @SerializedName("id")
    var id = 0

    @SerializedName("first_time_login")
    var firstTimeLogin: String? = null

    @SerializedName("app_id")
    var appId: String? = null

    @SerializedName("default_card_created")
    var defaultCardCreated: String? = null

    @SerializedName("email")
    var email: String? = null
}