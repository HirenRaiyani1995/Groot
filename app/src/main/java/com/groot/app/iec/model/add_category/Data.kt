package com.groot.app.iec.model.add_category

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class Data {
    @SerializedName("updated_at")
    var updatedAt: String? = null

    @SerializedName("user_id")
    var userId = 0

    @SerializedName("name")
    var name: String? = null

    @SerializedName("created_at")
    var createdAt: String? = null

    @SerializedName("id")
    var id = 0
}