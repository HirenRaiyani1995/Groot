package com.groot.app.iec.model.pie_chart

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class DataItem {
    @SerializedName("amount")
    var amount: Double? = null

    @SerializedName("percentage")
    var percentage = 0.0

    @SerializedName("id")
    var id: Int? = null

    @SerializedName("category")
    var category: String? = null

    @SerializedName("color")
    var color: String? = null

    companion object {
        // Comparator for sorting the list by percentage
        @kotlin.jvm.JvmField
        var PercentageComparator = java.util.Comparator<DataItem> { s1, s2 ->
            val p1 = s1.percentage.toInt()
            val p2 = s2.percentage.toInt()

            // For ascending order
            p1 - p2

            // For descending order
            // rollno2-rollno1;
        }
    }
}