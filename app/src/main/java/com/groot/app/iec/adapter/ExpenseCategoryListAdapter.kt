package com.groot.app.iec.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.groot.app.iec.R
import com.groot.app.iec.activity.ManageCategoryActivity
import com.groot.app.iec.databinding.RowCategoryBinding
import com.groot.app.iec.fragment.AddExpenseFragment
import com.groot.app.iec.model.category_list.DataItem
import com.groot.app.iec.rest.RestConstant
import java.util.*

class ExpenseCategoryListAdapter(
    var context: Context,
    private val expenseCategoryList: ArrayList<DataItem>,
    private val addExpenseFragment: AddExpenseFragment
) : RecyclerView.Adapter<ExpenseCategoryListAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        return MyViewHolder(
            RowCategoryBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup, false
            )
        )
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, i: Int) {
        val data = expenseCategoryList[i]
        if (data.name.equals("Food & Drinks", ignoreCase = true)) {
            myViewHolder.binding.ivCategory.setImageDrawable(context.resources.getDrawable(R.drawable.ic_food___drinks))
        } else if (data.name.equals("Transportation", ignoreCase = true)) {
            myViewHolder.binding.ivCategory.setImageDrawable(context.resources.getDrawable(R.drawable.ic_transportation))
        } else if (data.name.equals("Uilities", ignoreCase = true)) {
            myViewHolder.binding.ivCategory.setImageDrawable(context.resources.getDrawable(R.drawable.ic_utilities))
        } else if (data.name.equals("Shopping", ignoreCase = true)) {
            myViewHolder.binding.ivCategory.setImageDrawable(context.resources.getDrawable(R.drawable.ic_shopping))
        } else if (data.name.equals("Medical/Healthcare", ignoreCase = true)) {
            myViewHolder.binding.ivCategory.setImageDrawable(context.resources.getDrawable(R.drawable.ic_medical_healthcare))
        } else if (data.name.equals("Insurance", ignoreCase = true)) {
            myViewHolder.binding.ivCategory.setImageDrawable(context.resources.getDrawable(R.drawable.ic_insurance))
        } else if (data.name.equals("Education", ignoreCase = true)) {
            myViewHolder.binding.ivCategory.setImageDrawable(context.resources.getDrawable(R.drawable.ic_education))
        } else if (data.name.equals("Entertainment", ignoreCase = true)) {
            myViewHolder.binding.ivCategory.setImageDrawable(context.resources.getDrawable(R.drawable.ic_entertainment))
        } else if (data.name.equals("Groceries", ignoreCase = true)) {
            myViewHolder.binding.ivCategory.setImageDrawable(context.resources.getDrawable(R.drawable.ic_groceries))
        } else if (data.name.equals("Add New", ignoreCase = true)) {
            myViewHolder.binding.ivCategory.setImageDrawable(context.resources.getDrawable(R.drawable.ic_add_expense))
        } else {
            myViewHolder.binding.ivCategory.setImageDrawable(context.resources.getDrawable(R.drawable.ic_custom))
        }
        if (data.isHide == 1) {
            myViewHolder.binding.cardView.visibility = View.GONE
        } else {
            myViewHolder.binding.cardView.visibility = View.VISIBLE
        }
        myViewHolder.binding.categoryIconLayout.visibility = View.VISIBLE
        myViewHolder.binding.tvTitle.text = data.name
        myViewHolder.binding.background.setOnClickListener {
            if (i == expenseCategoryList.size - 1) {
                RestConstant.OPEN_ADD_EXPENSE_CATEGORY_SCREEN = true
                context.startActivity(
                    Intent(context, ManageCategoryActivity::class.java)
                        .putExtra("SCREEN", "EXPENCE")
                )
                Animatoo.animateSlideLeft(context)
            } else {
                for (i in expenseCategoryList.indices) {
                    expenseCategoryList[i].isSelect = false
                }
                data.isSelect = true
                RestConstant.EXCategory = data.name
                RestConstant.UICCategory = data.name
                notifyDataSetChanged()
            }
        }
        if (data.isSelect) {
            myViewHolder.binding.background.setBackgroundResource(R.color.expense_orange)
        } else {
            myViewHolder.binding.background.setBackgroundResource(R.color.dark_trans)
        }
    }

    override fun getItemCount(): Int {
        return expenseCategoryList.size
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