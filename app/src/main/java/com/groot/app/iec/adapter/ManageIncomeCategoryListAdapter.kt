package com.groot.app.iec.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.groot.app.iec.R
import com.groot.app.iec.databinding.RowManageCategoryBinding
import com.groot.app.iec.fragment.ManageCategoryIncomeFragment
import com.groot.app.iec.model.income_source.DataItem
import java.util.*

class ManageIncomeCategoryListAdapter(
    var context: Context,
    private val incomeSourceList: ArrayList<DataItem>,
    private val manageCategoryIncomeFragment: ManageCategoryIncomeFragment
) : RecyclerView.Adapter<ManageIncomeCategoryListAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        return MyViewHolder(
            RowManageCategoryBinding.inflate(
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
        } else {
            myViewHolder.binding.ivCategory.setImageDrawable(context.resources.getDrawable(R.drawable.ic_custom))
        }
        myViewHolder.binding.tvTitle.text = data.name
        if (data.isEditable == 0) {
            myViewHolder.binding.btnEdit.visibility = View.GONE
        } else {
            myViewHolder.binding.btnEdit.visibility = View.VISIBLE
        }
        if (data.isHide == 0) {
            myViewHolder.binding.btnHideShow.setImageDrawable(context.resources.getDrawable(R.drawable.ic_show))
        } else {
            myViewHolder.binding.btnHideShow.setImageDrawable(context.resources.getDrawable(R.drawable.ic_hide))
        }
        myViewHolder.binding.btnHideShow.setOnClickListener {
            manageCategoryIncomeFragment.editIncomeCategory(
                i,
                "HideShowClick"
            )
        }
        myViewHolder.binding.btnEdit.setOnClickListener {
            manageCategoryIncomeFragment.editIncomeCategory(
                i,
                "EditClick"
            )
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

    class MyViewHolder(val binding: RowManageCategoryBinding) : RecyclerView.ViewHolder(
        binding.root
    )
}