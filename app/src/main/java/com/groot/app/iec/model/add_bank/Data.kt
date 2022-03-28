package com.groot.app.iec.model.add_bank

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class Data {
    @SerializedName("amount")
    var amount: String? = null

    @SerializedName("updated_at")
    var updatedAt: String? = null

    @SerializedName("user_id")
    var userId: Int? = null

    @SerializedName("created_at")
    var createdAt: String? = null

    @SerializedName("id")
    var id: Int? = null

    @SerializedName("category")
    var category: String? = null

    @SerializedName("title")
    var title: String? = null
}