package com.groot.app.iec.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.groot.app.iec.R
import com.groot.app.iec.activity.ExpenseCategoryAnalyticsActivity
import com.groot.app.iec.databinding.RowCategoryCardBinding
import com.groot.app.iec.fragment.PieChartExpenseFragment
import com.groot.app.iec.model.pie_chart.DataItem
import com.groot.app.iec.rest.RestConstant
import com.groot.app.iec.utils.AppUtils.doubleToStringNoDecimal
import com.groot.app.iec.utils.MySharedPreferences
import java.lang.String
import java.util.*

class ExpenseCategoryCardAdapter(
    var context: Context?,
    private val expenseCategoryList: ArrayList<DataItem>,
    private val pieChartExpenseFragment: PieChartExpenseFragment
) : RecyclerView.Adapter<ExpenseCategoryCardAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        return MyViewHolder(
            RowCategoryCardBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup, false
            )
        )
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, i: Int) {
        val data = expenseCategoryList[i]
        if (data.category.equals("Food & Drinks", ignoreCase = true)) {
            myViewHolder.binding.ivCategory.setImageDrawable(context?.resources?.getDrawable(R.drawable.ic_food___drinks))
        } else if (data.category.equals("Transportation", ignoreCase = true)) {
            myViewHolder.binding.ivCategory.setImageDrawable(context?.resources?.getDrawable(R.drawable.ic_transportation))
        } else if (data.category.equals("Uilities", ignoreCase = true)) {
            myViewHolder.binding.ivCategory.setImageDrawable(context?.resources?.getDrawable(R.drawable.ic_utilities))
        } else if (data.category.equals("Shopping", ignoreCase = true)) {
            myViewHolder.binding.ivCategory.setImageDrawable(context?.resources?.getDrawable(R.drawable.ic_shopping))
        } else if (data.category.equals("Medical/Healthcare", ignoreCase = true)) {
            myViewHolder.binding.ivCategory.setImageDrawable(context?.resources?.getDrawable(R.drawable.ic_medical_healthcare))
        } else if (data.category.equals("Insurance", ignoreCase = true)) {
            myViewHolder.binding.ivCategory.setImageDrawable(context?.resources?.getDrawable(R.drawable.ic_insurance))
        } else if (data.category.equals("Education", ignoreCase = true)) {
            myViewHolder.binding.ivCategory.setImageDrawable(context?.resources?.getDrawable(R.drawable.ic_education))
        } else if (data.category.equals("Entertainment", ignoreCase = true)) {
            myViewHolder.binding.ivCategory.setImageDrawable(context?.resources?.getDrawable(R.drawable.ic_entertainment))
        } else if (data.category.equals("Groceries", ignoreCase = true)) {
            myViewHolder.binding.ivCategory.setImageDrawable(context?.resources?.getDrawable(R.drawable.ic_groceries))
        } else {
            myViewHolder.binding.ivCategory.setImageDrawable(context?.resources?.getDrawable(R.drawable.ic_custom))
        }
        myViewHolder.binding.txtName.text = data.category
        myViewHolder.binding.progressBar.progress = data.percentage.toInt()
        myViewHolder.binding.tvProgress.text = String.valueOf(data.percentage) + "%"
        myViewHolder.binding.txtAmount.setText(
            MySharedPreferences.getMySharedPreferences()?.currency
                .toString() + doubleToStringNoDecimal(data.amount.toString())
        )
        context?.resources?.let { myViewHolder.binding.txtAmount.setTextColor(it.getColor(R.color.expense_orange)) }
        myViewHolder.binding.item.setOnClickListener {
            RestConstant.START_DATE = ""
            RestConstant.END_DATE = ""
            val intent = Intent(context, ExpenseCategoryAnalyticsActivity::class.java)
            intent.putExtra("Source", data.category)
            intent.putExtra("Progress", String.valueOf(data.percentage))
            context?.startActivity(intent)
            Animatoo.animateSlideLeft(context)
        }
    }

    override fun getItemCount(): Int {
        return expenseCategoryList.size
    }

    class MyViewHolder(val binding: RowCategoryCardBinding) : RecyclerView.ViewHolder(
        binding.root
    )
}