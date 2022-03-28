package com.groot.app.iec.model.pie_chart

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class PieChartResponse {
    @SerializedName("total_income_amount")
    var totalIncomeAmount: Double? = null

    @SerializedName("code")
    var code: Int? = null

    @SerializedName("data")
    var data: List<DataItem>? = null

    @SerializedName("total_expense_amount")
    var totalExpenseAmount: Double? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("status")
    var isStatus = false
}