package com.groot.app.iec.model.add_income

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class Data {
    @SerializedName("date")
    var date: String? = null

    @SerializedName("amount")
    var amount: String? = null

    @SerializedName("updated_at")
    var updatedAt: String? = null

    @SerializedName("user_id")
    var userId: Int? = null

    @SerializedName("bank_id")
    var bankId: String? = null

    @SerializedName("created_at")
    var createdAt: String? = null

    @SerializedName("source")
    var source: String? = null

    @SerializedName("id")
    var id: Int? = null

    @SerializedName("title")
    var title: String? = null
}