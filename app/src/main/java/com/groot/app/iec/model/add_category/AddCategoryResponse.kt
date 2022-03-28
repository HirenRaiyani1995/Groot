package com.groot.app.iec.model.add_category

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class AddCategoryResponse {
    @SerializedName("code")
    var code = 0

    @SerializedName("data")
    var data: Data? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("status")
    var isStatus = false
}