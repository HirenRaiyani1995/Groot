package com.groot.app.iec.model.add_income

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class AddIncomeResponse {
    @SerializedName("code")
    var code: Int? = null

    @SerializedName("data")
    var data: Data? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("status")
    var isStatus = false
}