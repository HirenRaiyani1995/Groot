package com.groot.app.iec.model

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class BasicModelResponse {
    @SerializedName("code")
    var code = 0

    @SerializedName("message")
    var message: String? = null
}