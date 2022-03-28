package com.groot.app.iec.model.cmsResponse

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class CMSResponse {
    @SerializedName("data")
    @Expose
    var data: Data? = null

    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("status")
    @Expose
    var status: Boolean? = null
}