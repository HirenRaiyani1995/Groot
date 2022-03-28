package com.groot.app.iec.model.category_list

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class CategoryListResponse {
    @SerializedName("code")
    var code: Int? = null

    @SerializedName("data")
    var data: List<DataItem>? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("status")
    var isStatus = false
}