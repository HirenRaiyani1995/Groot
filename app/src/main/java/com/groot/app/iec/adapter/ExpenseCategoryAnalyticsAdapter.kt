package com.groot.app.iec.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.groot.app.iec.R
import com.groot.app.iec.activity.ExpenseCategoryAnalyticsActivity
import com.groot.app.iec.model.analytics.DataItem
import com.groot.app.iec.utils.AppUtils.doubleToStringNoDecimal
import com.groot.app.iec.utils.MySharedPreferences
import com.groot.app.iec.utils.full_screen_image.PhotoFullPopupWindow
import com.makeramen.roundedimageview.RoundedImageView
import java.util.*

class ExpenseCategoryAnalyticsAdapter(
    var context: Context, var analyticsList: ArrayList<DataItem>?,
    var expenseCategoryAnalyticsActivity: ExpenseCategoryAnalyticsActivity
) : RecyclerView.Adapter<ExpenseCategoryAnalyticsAdapter.MyViewHolder>() {

    var filterlist: ArrayList<DataItem>
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.raw_paid_history, viewGroup, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, i: Int) {
        val data = analyticsList?.get(i)
        myViewHolder.txtDate.text = data?.date.toString()
        myViewHolder.txtDescription.text = data?.title.toString()
        myViewHolder.txtPaidBy.isSelected = true
        try {
            if (data?.type.equals("income", ignoreCase = true)) {
                myViewHolder.iv_status.setImageResource(R.drawable.ic_down)
            } else {
                myViewHolder.iv_status.setImageResource(R.drawable.ic_up)
                myViewHolder.background.setBackgroundResource(R.drawable.bg_rounded_cornor_orange)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            if (data?.image != null) {
                Glide.with(context).load(data?.image).into(myViewHolder.ivImage)
                myViewHolder.ivImage.visibility = View.VISIBLE
            } else {
                myViewHolder.ivImage.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        myViewHolder.ivImage.setOnClickListener { view: View? ->
            PhotoFullPopupWindow(
                context, R.layout.popup_photo_full, myViewHolder.ivImage, data?.image, null
            )
        }
        try {
            if (data?.type.equals("income", ignoreCase = true)) {
                myViewHolder.txtCategoryName.setTextColor(context.resources.getColor(R.color.light_yellow))
                myViewHolder.txtCategoryName.text = "Income to " + data?.bankId.toString()
            } else {
                myViewHolder.txtCategoryName.setTextColor(context.resources.getColor(R.color.tomato))
                myViewHolder.txtCategoryName.text = data?.category.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            if (data?.type.equals("income", ignoreCase = true)) {
                myViewHolder.txtAmount.setTextColor(context.resources.getColor(R.color.light_yellow))
                myViewHolder.txtAmount.setText(
                    MySharedPreferences.getMySharedPreferences()?.currency
                        .toString() + doubleToStringNoDecimal(data?.amount.toString())
                )
            } else {
                myViewHolder.txtAmount.setTextColor(context.resources.getColor(R.color.tomato))
                myViewHolder.txtAmount.setText(
                    MySharedPreferences.getMySharedPreferences()?.currency
                        .toString() + doubleToStringNoDecimal(data?.amount.toString())
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            if (data?.type.equals("income", ignoreCase = true)) {
                myViewHolder.txtPaidBy.text = "Income From " + data?.category.toString()
            } else {
                myViewHolder.txtPaidBy.text = "Paid by " + data?.bankId.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        myViewHolder.rowFG.setOnClickListener {
            expenseCategoryAnalyticsActivity.transactionListClick(
                i
            )
        }
    }

    override fun getItemCount(): Int {
        return analyticsList?.size!!
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtAmount: AppCompatTextView
        var txtPaidBy: AppCompatTextView
        var txtDescription: AppCompatTextView
        var txtDate: AppCompatTextView
        var txtCategoryName: AppCompatTextView
        var iv_status: AppCompatImageView
        var background: LinearLayout
        var rowFG: CardView
        var ivImage: RoundedImageView

        init {
            txtAmount = itemView.findViewById(R.id.txtAmount)
            txtPaidBy = itemView.findViewById(R.id.txtPaidBy)
            txtDescription = itemView.findViewById(R.id.txtDescription)
            txtDate = itemView.findViewById(R.id.txtDate)
            txtCategoryName = itemView.findViewById(R.id.txtCategoryName)
            iv_status = itemView.findViewById(R.id.iv_status)
            background = itemView.findViewById(R.id.background)
            rowFG = itemView.findViewById(R.id.rowFG)
            ivImage = itemView.findViewById(R.id.ivImage)
        }
    }

    fun filter(charText: String) {
        var charText = charText
        charText = charText.lowercase(Locale.getDefault())
        analyticsList?.clear()
        if (charText.length == 0) {
            analyticsList?.addAll(filterlist)
        } else {
            for (wp in filterlist) {
                if (wp.title!!.lowercase(Locale.getDefault()).contains(charText)) {
                    analyticsList?.add(wp)
                }
            }
        }
        notifyDataSetChanged()
    }

    init {
        filterlist = ArrayList()
        filterlist.addAll(analyticsList!!)
    }
}