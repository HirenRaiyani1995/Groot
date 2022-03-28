package com.groot.app.iec.model.add_bank

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class AddBankResponse {
    @SerializedName("code")
    var code: Int? = null

    @SerializedName("data")
    var data: Data? = null

    @SerializedName("message")
    var message: String? = null
}