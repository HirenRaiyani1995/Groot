package com.groot.app.iec.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.groot.app.iec.R
import com.groot.app.iec.databinding.RowManageCategoryBinding
import com.groot.app.iec.fragment.ManageCategoryExpenseFragment
import com.groot.app.iec.model.category_list.DataItem
import java.util.*

class ManageExpenseCategoryListAdapter(
    var context: Context,
    private val expenseCategoryList: ArrayList<DataItem>,
    private val manageCategoryExpenseFragment: ManageCategoryExpenseFragment
) : RecyclerView.Adapter<ManageExpenseCategoryListAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        return MyViewHolder(
            RowManageCategoryBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup, false
            )
        )
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, i: Int) {
        val data = expenseCategoryList[i]
        myViewHolder.binding.tvTitle.text = data.name
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
        } else {
            myViewHolder.binding.ivCategory.setImageDrawable(context.resources.getDrawable(R.drawable.ic_custom))
        }
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
            manageCategoryExpenseFragment.editExpenseCategory(
                i,
                "HideShowClick"
            )
        }
        myViewHolder.binding.btnEdit.setOnClickListener {
            manageCategoryExpenseFragment.editExpenseCategory(
                i,
                "EditClick"
            )
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

    class MyViewHolder(val binding: RowManageCategoryBinding) : RecyclerView.ViewHolder(
        binding.root
    )
}