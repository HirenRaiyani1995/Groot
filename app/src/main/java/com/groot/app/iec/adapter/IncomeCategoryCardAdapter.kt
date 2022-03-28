package com.groot.app.iec.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.groot.app.iec.R
import com.groot.app.iec.activity.IncomeCategoryAnalyticsActivity
import com.groot.app.iec.databinding.RowCategoryCardBinding
import com.groot.app.iec.fragment.PieChartIncomeFragment
import com.groot.app.iec.model.pie_chart.DataItem
import com.groot.app.iec.rest.RestConstant
import com.groot.app.iec.utils.AppUtils.doubleToStringNoDecimal
import com.groot.app.iec.utils.MySharedPreferences
import java.lang.String
import java.util.*

class IncomeCategoryCardAdapter(
    var context: Context?,
    private val incomeCategoryList: ArrayList<DataItem>,
    private val pieChartIncomeFragment: PieChartIncomeFragment
) : RecyclerView.Adapter<IncomeCategoryCardAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        return MyViewHolder(
            RowCategoryCardBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup, false
            )
        )
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, i: Int) {
        val data = incomeCategoryList[i]
        if (data.category.equals("Salary", ignoreCase = true)) {
            myViewHolder.binding.ivCategory.setImageDrawable(context?.resources?.getDrawable(R.drawable.ic_salary))
        } else if (data.category.equals("My Business", ignoreCase = true)) {
            myViewHolder.binding.ivCategory.setImageDrawable(context?.resources?.getDrawable(R.drawable.ic_my_business))
        } else if (data.category.equals("Pocket Money", ignoreCase = true)) {
            myViewHolder.binding.ivCategory.setImageDrawable(context?.resources?.getDrawable(R.drawable.ic_pocket_money))
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
        myViewHolder.binding.item.setOnClickListener {
            RestConstant.START_DATE = ""
            RestConstant.END_DATE = ""
            val intent = Intent(context, IncomeCategoryAnalyticsActivity::class.java)
            intent.putExtra("Source", data.category)
            intent.putExtra("Progress", String.valueOf(data.percentage))
            context?.startActivity(intent)
            Animatoo.animateSlideLeft(context)
        }
    }

    override fun getItemCount(): Int {
        return incomeCategoryList.size
    }

    class MyViewHolder internal constructor(var binding: RowCategoryCardBinding) :
        RecyclerView.ViewHolder(
            binding.root
        )
}