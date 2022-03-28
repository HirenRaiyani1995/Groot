package com.groot.app.iec.model.payment_method

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class DataItem(
    @field:SerializedName("amount") var amount: Double,
    @field:SerializedName(
        "updated_at"
    ) var updatedAt: String,
    @field:SerializedName("color") var color: String,
    @field:SerializedName(
        "user_id"
    ) var userId: Int,
    @field:SerializedName("created_at") var createdAt: String,
    @field:SerializedName(
        "id"
    ) var id: Int,
    @field:SerializedName("category") var category: String,
    @field:SerializedName(
        "title"
    ) var title: String,
    isSelect: Boolean
) {

    @SerializedName("is_hide")
    var isHide: Int? = null

    @kotlin.jvm.JvmField
    var isSelect = false

    init {
        this.isSelect = isSelect
    }
}