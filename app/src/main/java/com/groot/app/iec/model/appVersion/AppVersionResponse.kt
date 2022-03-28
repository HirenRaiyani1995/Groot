package com.groot.app.iec.model.appVersion

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AppVersionResponse(
    @SerializedName("code")
    @Expose
    var code: Int? = null,

    @SerializedName("status")
    @Expose
    var status: Boolean? = null,

    @SerializedName("message")
    @Expose
    var message: String? = null,

    @SerializedName("data")
    @Expose
    var data: List<Datum>? = null
)

data class Datum(
    @SerializedName("id")
    @Expose
    var id: Int? = null,

    @SerializedName("version")
    @Expose
    var version: String? = null,

    @SerializedName("is_update_required")
    @Expose
    var isUpdateRequired: String? = null,

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null,

    @SerializedName("updated_at")
    @Expose
    var updatedAt: Any? = null
)
