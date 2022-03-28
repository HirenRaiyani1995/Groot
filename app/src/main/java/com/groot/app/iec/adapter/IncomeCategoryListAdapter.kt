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
import com.groot.app.iec.fragment.AddIncomeFragment
import com.groot.app.iec.model.income_source.DataItem
import com.groot.app.iec.rest.RestConstant
import java.util.*

class IncomeCategoryListAdapter(
    var context: Context,
    private val incomeSourceList: ArrayList<DataItem>,
    private val addIncomeFragment: AddIncomeFragment
) : RecyclerView.Adapter<IncomeCategoryListAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        return MyViewHolder(
            RowCategoryBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup, false
            )
        )
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, i: Int) {
        val data = incomeSourceList[i]
        if (data.name.equals("Salary", ignoreCase = true)) {
            myViewHolder.binding.ivCategory.setImageDrawable(context.resources.getDrawable(R.drawable.ic_salary))
        } else if (data.name.equals("My Business", ignoreCase = true)) {
            myViewHolder.binding.ivCategory.setImageDrawable(context.resources.getDrawable(R.drawable.ic_my_business))
        } else if (data.name.equals("Pocket Money", ignoreCase = true)) {
            myViewHolder.binding.ivCategory.setImageDrawable(context.resources.getDrawable(R.drawable.ic_pocket_money))
        } else if (data.name.equals("Add New", ignoreCase = true)) {
            myViewHolder.binding.ivCategory.setImageDrawable(context.resources.getDrawable(R.drawable.ic_add_income))
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
            if (i == incomeSourceList.size - 1) {
                RestConstant.OPEN_ADD_INCOME_CATEGORY_SCREEN = true
                context.startActivity(
                    Intent(context, ManageCategoryActivity::class.java)
                        .putExtra("SCREEN", "INCOME")
                )
                Animatoo.animateSlideLeft(context)
            } else {
                for (i in incomeSourceList.indices) {
                    incomeSourceList[i].isSelect = false
                }
                data.isSelect = true
                notifyDataSetChanged()
                RestConstant.UICCategory = data.name
                addIncomeFragment.getSelectedCategory(data.name)
            }
        }
        if (data.isSelect) {
            myViewHolder.binding.background.setBackgroundResource(R.color.facebook)
        } else {
            myViewHolder.binding.background.setBackgroundResource(R.color.dark_trans)
        }
    }

    override fun getItemCount(): Int {
        return incomeSourceList.size
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