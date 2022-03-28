package com.groot.app.iec.model.googleAds

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AdmobAdsResponse(
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
    @SerializedName("ads_id")
    @Expose
    var adsId: String? = null,

    @SerializedName("ads_type")
    @Expose
    var adsType: String? = null,

    @SerializedName("ads_key")
    @Expose
    var adsKey: String? = null,

    @SerializedName("enable")
    @Expose
    var enable: String? = null,

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null,

    @SerializedName("updated_at")
    @Expose
    var updatedAt: Any? = null
)