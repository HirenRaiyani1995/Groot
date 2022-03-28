package com.groot.app.iec.model.countryList

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class DataItem {
    @SerializedName("country")
    var country: String? = null

    @SerializedName("symbol")
    var symbol: String? = null

    @SerializedName("code")
    var code: String? = null

    @SerializedName("currency")
    var currency: String? = null

    @SerializedName("id")
    var id = 0
    @kotlin.jvm.JvmField
    var isSelect = false
}