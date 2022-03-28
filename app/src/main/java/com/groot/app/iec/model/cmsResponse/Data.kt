package com.groot.app.iec.model.cmsResponse

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class Data {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("slug")
    @Expose
    var slug: String? = null

    @SerializedName("content")
    @Expose
    var content: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: Any? = null
}