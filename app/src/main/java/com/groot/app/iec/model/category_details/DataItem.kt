package com.groot.app.iec.model.category_details

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class DataItem {
    @SerializedName("amount")
    var amount: String? = null

    @SerializedName("bank")
    var bank: String? = null

    @SerializedName("created_at")
    var createdAt: String? = null

    @SerializedName("id")
    var id = 0

    @SerializedName("category")
    var category: String? = null

    @SerializedName("title")
    var title: String? = null
}