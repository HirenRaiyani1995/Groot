package com.groot.app.iec.model.add_expanse

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class Data {
    @SerializedName("date")
    var date: String? = null

    @SerializedName("payment_method_id")
    var paymentMethodId: String? = null

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