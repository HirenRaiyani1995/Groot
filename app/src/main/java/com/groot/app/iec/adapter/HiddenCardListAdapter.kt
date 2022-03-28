package com.groot.app.iec.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.groot.app.iec.R
import com.groot.app.iec.activity.ManageCardsActivity
import com.groot.app.iec.model.payment_method.DataItem
import com.groot.app.iec.utils.AppUtils.doubleToStringNoDecimal
import com.groot.app.iec.utils.MySharedPreferences
import java.util.*

class HiddenCardListAdapter(
    var context: Context,
    private val horizontalList: ArrayList<DataItem>,
    var manageCardsActivity: ManageCardsActivity
) : RecyclerView.Adapter<HiddenCardListAdapter.MyViewHolder>() {
    lateinit var multicolor: IntArray
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.raw_manage_card_horizontal, viewGroup, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, i: Int) {
        multicolor = context.resources.getIntArray(R.array.multicolor)
        val data = horizontalList[i]
        myViewHolder.btnDelete.visibility = View.GONE
        myViewHolder.txtPrice.setText(
            MySharedPreferences.getMySharedPreferences()?.currency
                .toString() + doubleToStringNoDecimal(data.amount.toString())
        )
        myViewHolder.txtDescription.text = data.title
        myViewHolder.btnHideShow.setImageDrawable(context.getDrawable(R.drawable.ic_hide))
        myViewHolder.layout.backgroundTintList = ColorStateList.valueOf(multicolor[i])
        myViewHolder.btnHideShow.setOnClickListener {
            manageCardsActivity.getHideShowButtonClick(
                "0",
                data.id.toString()
            )
        }
        myViewHolder.btnDelete.setOnClickListener { manageCardsActivity.deleteCardButtonClick(data.id.toString()) }

        /* myViewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manageCardsActivity.editCard(data.getId().toString(),data.getTitle());
            }
        });*/
    }

    override fun getItemCount(): Int {
        return horizontalList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var layout: RelativeLayout
        var mainLayout: RelativeLayout
        var txtPrice: AppCompatTextView
        var txtDescription: AppCompatTextView
        var btnHideShow: AppCompatImageView
        var btnEdit: AppCompatImageView
        var btnDelete: AppCompatImageView

        init {
            layout = itemView.findViewById(R.id.layout)
            mainLayout = itemView.findViewById(R.id.mainLayout)
            txtDescription = itemView.findViewById(R.id.txtDescription)
            txtPrice = itemView.findViewById(R.id.txtPrice)
            btnHideShow = itemView.findViewById(R.id.btnHideShow)
            btnEdit = itemView.findViewById(R.id.btnEdit)
            btnDelete = itemView.findViewById(R.id.btnDelete)
        }
    }
}