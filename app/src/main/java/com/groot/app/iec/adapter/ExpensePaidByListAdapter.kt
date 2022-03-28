package com.groot.app.iec.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.groot.app.iec.R
import com.groot.app.iec.activity.ExpenseCategoryAnalyticsActivity
import com.groot.app.iec.databinding.RowCategoryBinding
import com.groot.app.iec.fragment.AddExpenseFragment
import com.groot.app.iec.model.payment_method.DataItem
import com.groot.app.iec.rest.RestConstant
import java.util.*

class ExpensePaidByListAdapter(
    var context: Context, private val paidByList: ArrayList<DataItem>?,
    private val expenseCategoryAnalyticsActivity: ExpenseCategoryAnalyticsActivity?,
    private val addExpenseFragment: AddExpenseFragment?
) : RecyclerView.Adapter<ExpensePaidByListAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        return MyViewHolder(
            RowCategoryBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup, false
            )
        )
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, i: Int) {
        val data = paidByList?.get(i)
        if(data?.isHide == 0) {
            myViewHolder.binding.categoryIconLayout.visibility = View.GONE
            myViewHolder.binding.tvTitle.text = data.title
            myViewHolder.binding.background.setOnClickListener {
                for (i in paidByList!!.indices) {
                    paidByList[i]?.isSelect = false
                }
                data?.isSelect = true
                RestConstant.EXPaidBy = data?.title.toString()
                notifyDataSetChanged()
                if (data?.title.equals("All", ignoreCase = true)) {
                    RestConstant.EXPaidBy = ""
                    RestConstant.UICMoneyIn = ""
                } else {
                    RestConstant.EXPaidBy = data?.title.toString()
                    RestConstant.UICMoneyIn = data?.title.toString()
                }
                expenseCategoryAnalyticsActivity?.incomeInClick
                addExpenseFragment?.paidByclick(i)
            }
            if (data!!.isSelect) {
                myViewHolder.binding.background.setBackgroundResource(R.color.expense_orange)
            } else {
                myViewHolder.binding.background.setBackgroundResource(R.color.dark_trans)
            }
        }else{
            myViewHolder.binding.cardView.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return paidByList!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class MyViewHolder(val binding: RowCategoryBinding) : RecyclerView.ViewHolder(
        binding.root
    )
}