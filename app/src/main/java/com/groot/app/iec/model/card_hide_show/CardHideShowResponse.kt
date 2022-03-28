package com.groot.app.iec.model.card_hide_show

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class CardHideShowResponse {
    @SerializedName("is_hide")
    var isHide: String? = null

    @SerializedName("code")
    var code = 0

    @SerializedName("message")
    var message: String? = null

    @SerializedName("status")
    var isStatus = false
}