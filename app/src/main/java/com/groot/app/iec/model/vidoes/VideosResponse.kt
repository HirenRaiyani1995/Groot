package com.groot.app.iec.model.vidoes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class VideosResponse(
    @SerializedName("data")
    @Expose
    var data: List<Datum>? = null,

    @SerializedName("status")
    @Expose
    var status: Boolean? = null,

    @SerializedName("code")
    @Expose
    var code: Int? = null,

    @SerializedName("message")
    @Expose
    var message: String? = null
)

data class Datum(
    @SerializedName("id")
    @Expose
    var id: Int? = null,

    @SerializedName("url")
    @Expose
    var url: String? = null,

    @SerializedName("title")
    @Expose
    var title: String? = null,

    @SerializedName("description")
    @Expose
    var description: String? = null,

    @SerializedName("is_show")
    @Expose
    var isShow: String? = null,

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null,

    @SerializedName("updated_at")
    @Expose
    var updatedAt: Any? = null
)