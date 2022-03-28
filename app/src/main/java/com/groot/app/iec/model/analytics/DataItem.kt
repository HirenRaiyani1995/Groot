package com.groot.app.iec.model.analytics

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class DataItem {
    @SerializedName("date")
    var date: String? = null

    @SerializedName("amount")
    var amount: Double? = null

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

    @SerializedName("type")
    var type: String? = null

    @SerializedName("bank")
    var bankId: String? = null
        get() = field
        set(bank) {
            field = bank
        }

    @SerializedName("source")
    var source: String? = null

    @SerializedName("image")
    var image: String? = null
}