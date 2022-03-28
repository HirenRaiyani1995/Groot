package com.groot.app.iec.model.payment_method

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class PaymentMethodResponse {
    @SerializedName("code")
    var code: Int? = null

    @SerializedName("data")
    var data: ArrayList<DataItem>? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("status")
    var isStatus = false
}