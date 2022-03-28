package com.groot.app.iec.model.analytics

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class AnalyticsResponse {
    @SerializedName("code")
    var code: Int? = null

    @SerializedName("expense_amount")
    var expenseAmount: Double? = null

    @SerializedName("data")
    var data: ArrayList<DataItem>? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("income_amount")
    var incomeAmount: Double? = null

    @SerializedName("status")
    var isStatus = false
}