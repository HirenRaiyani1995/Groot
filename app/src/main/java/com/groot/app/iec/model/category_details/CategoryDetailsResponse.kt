package com.groot.app.iec.model.category_details

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class CategoryDetailsResponse {
    @SerializedName("code")
    var code = 0

    @SerializedName("data")
    var data: List<DataItem>? = null

    @SerializedName("category_percentage")
    var categoryPercentage = 0.0

    @SerializedName("message")
    var message: String? = null

    @SerializedName("status")
    var isStatus = false

    @SerializedName("category_amount")
    var categoryAmount: String? = null
}