package com.groot.app.iec.model.countryList

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class CountryListResponse {
    @SerializedName("code")
    var code = 0

    @SerializedName("data")
    var data: List<DataItem>? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("status")
    var isStatus = false
}