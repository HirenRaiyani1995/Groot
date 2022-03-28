package com.groot.app.iec.model.income_source

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class DataItem(
    @field:SerializedName("updated_at") var updatedAt: Any,
    @field:SerializedName(
        "name"
    ) var name: String,
    @field:SerializedName("created_at") var createdAt: String,
    @field:SerializedName(
        "id"
    ) var id: Int,
    @field:SerializedName("is_editable") var isEditable: Int,
    @field:SerializedName(
        "is_hide"
    ) var isHide: Int,
    isSelect: Boolean
) {

    @kotlin.jvm.JvmField
    var isSelect = false

    init {
        this.isSelect = isSelect
    }
}