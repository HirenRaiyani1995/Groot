package com.groot.app.iec.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.groot.app.iec.R
import com.groot.app.iec.activity.DashboardActivity
import com.groot.app.iec.model.payment_method.DataItem
import com.groot.app.iec.utils.AppUtils.doubleToStringNoDecimal
import com.groot.app.iec.utils.MySharedPreferences
import java.util.*

class HomeScreenHorizontalListAdapter(
    var context: Context,
    private val horizontalList: ArrayList<DataItem>,
    TotalAmount: Double,
    var dashboardActivity: DashboardActivity
) : RecyclerView.Adapter<HomeScreenHorizontalListAdapter.MyViewHolder>() {
    private var TotalAmount = 0.00
    lateinit var multicolor: IntArray

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.raw_dashboard_horizontal, viewGroup, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(
        myViewHolder: MyViewHolder,
        @SuppressLint("RecyclerView") i: Int
    ) {
        multicolor = context.resources.getIntArray(R.array.multicolor)
        if (i == 0) {
            if(TotalAmount!= null && TotalAmount == 0.0){
                myViewHolder.txtPrice.text = MySharedPreferences.getMySharedPreferences()?.currency
                    .toString() + "0.00"
            }else{
                myViewHolder.txtPrice.text = MySharedPreferences.getMySharedPreferences()?.currency
                    .toString() + doubleToStringNoDecimal(TotalAmount.toString())
            }
            myViewHolder.txtPrice.visibility = View.VISIBLE
            myViewHolder.txtDescription.text = """
                Total Available 
                Balance
                """.trimIndent()
            val face = ResourcesCompat.getFont(context, R.font.gtw_alsheim_pro_medium)
            myViewHolder.txtDescription.typeface = face
            myViewHolder.txtPrice.typeface = face
            myViewHolder.btnAdd.visibility = View.GONE
            myViewHolder.btnDelete.visibility = View.GONE
            myViewHolder.btnEdit.visibility = View.GONE
            myViewHolder.layout.backgroundTintList = ColorStateList.valueOf(multicolor[0])
        } else if (i == horizontalList.size + 1) {
            myViewHolder.txtPrice.text = ""
            myViewHolder.txtPrice.visibility = View.GONE
            myViewHolder.btnDelete.visibility = View.GONE
            myViewHolder.btnEdit.visibility = View.GONE
            myViewHolder.txtDescription.text = """
                Add Bank,
                Card, Wallet, other
                income source
                """.trimIndent()
            myViewHolder.layout.backgroundTintList =
                ColorStateList.valueOf(multicolor[horizontalList.size + 1])
        } else {
            val data = horizontalList[i - 1]
            if (i == 1) {
                myViewHolder.btnDelete.visibility = View.GONE
                if(doubleToStringNoDecimal(data.amount.toString()) == ".00"){
                    myViewHolder.txtPrice.text = MySharedPreferences.getMySharedPreferences()?.currency
                        .toString() + "0.00"
                }else {
                    myViewHolder.txtPrice.text =
                        MySharedPreferences.getMySharedPreferences()?.currency
                            .toString() + doubleToStringNoDecimal(data.amount.toString())
                }
                myViewHolder.txtDescription.text = data.title
                myViewHolder.layout.backgroundTintList = ColorStateList.valueOf(multicolor[i])
            } else {
                try {
                    if(doubleToStringNoDecimal(data.amount.toString()) == ".00"){
                        myViewHolder.txtPrice.text = MySharedPreferences.getMySharedPreferences()?.currency
                            .toString() + "0.00"
                    }else {
                        myViewHolder.txtPrice.text =
                            MySharedPreferences.getMySharedPreferences()?.currency
                                .toString() + doubleToStringNoDecimal(data.amount.toString())
                    }
                    myViewHolder.txtDescription.text = data.title
                    myViewHolder.layout.backgroundTintList = ColorStateList.valueOf(multicolor[i])
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        myViewHolder.btnAdd.setOnClickListener {
            if (i == horizontalList.size + 1) {
                dashboardActivity.cardAddButtonClick()
            } else if (i > 0) {
                dashboardActivity.AddButtonClick()
            }
        }

        myViewHolder.mainLayout.setOnClickListener{
            if (i > 0 && i != horizontalList.size + 1) {
                dashboardActivity.openCardTransactionScreen(horizontalList[i-1].title)
            }
        }

        myViewHolder.btnDelete.setOnClickListener { dashboardActivity.deleteCard(i - 1) }
        myViewHolder.btnEdit.setOnClickListener {
            if (i > 0 && i != horizontalList.size + 1) {
                dashboardActivity.editCard(horizontalList[i - 1].title)
            }
        }
    }

    override fun getItemCount(): Int {
        return horizontalList.size + 2
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var layout: RelativeLayout
        var txtPrice: AppCompatTextView
        var txtDescription: AppCompatTextView
        var btnAdd: AppCompatImageView
        var btnEdit: AppCompatImageView
        var btnDelete: AppCompatImageView
        var mainLayout: RelativeLayout

        init {
            layout = itemView.findViewById(R.id.layout)
            txtDescription = itemView.findViewById(R.id.txtDescription)
            txtPrice = itemView.findViewById(R.id.txtPrice)
            btnAdd = itemView.findViewById(R.id.btnAdd)
            btnEdit = itemView.findViewById(R.id.btnEdit)
            btnDelete = itemView.findViewById(R.id.btnDelete)
            mainLayout = itemView.findViewById(R.id.mainLayout)
        }
    }

    init {
        this.TotalAmount = TotalAmount
    }
}