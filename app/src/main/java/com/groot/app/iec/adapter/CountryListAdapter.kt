package com.groot.app.iec.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.groot.app.iec.R
import com.groot.app.iec.activity.SelectCountryListActivity
import com.groot.app.iec.model.countryList.DataItem
import java.util.*

class CountryListAdapter(
    var context: Context, var countryList: ArrayList<DataItem>,
    var selectCountryListActivity: SelectCountryListActivity
) : RecyclerView.Adapter<CountryListAdapter.MyViewHolder>() {
    var filterlist: ArrayList<DataItem>

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val iv = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.raw_countrylist, viewGroup, false)
        return MyViewHolder(iv)
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, i: Int) {
        val data = countryList[i]
        myViewHolder.txtName.text = data.country + " (" + data.code + ") - " + data.symbol
        myViewHolder.itemView1?.setOnClickListener {
            for (i in countryList.indices) {
                countryList[i].isSelect = false
            }
            data.isSelect = true
            notifyDataSetChanged()
        }
        if (data.isSelect) {
            myViewHolder.btnSelect.visibility = View.VISIBLE
        } else {
            myViewHolder.btnSelect.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return countryList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class MyViewHolder(iv: View) : RecyclerView.ViewHolder(iv) {
        var txtName: AppCompatTextView
        var itemView1: RelativeLayout? = null
        var btnSelect: AppCompatImageView

        init {
            txtName = iv.findViewById(R.id.txtName)
            itemView1 = iv.findViewById(R.id.itemView)
            btnSelect = iv.findViewById(R.id.btnSelect)
        }
    }

    fun filter(charText: String) {
        var charText = charText
        charText = charText.lowercase(Locale.getDefault())
        countryList.clear()
        if (charText.length == 0) {
            countryList.addAll(filterlist)
        } else {
            for (wp in filterlist) {
                if (wp.symbol!!.lowercase(Locale.getDefault()).contains(charText)
                    || wp.country!!.lowercase(Locale.getDefault()).contains(charText)
                    || wp.currency!!.lowercase(Locale.getDefault()).contains(charText) ||
                    wp.code!!.lowercase(Locale.getDefault()).contains(charText)
                ) {
                    countryList.add(wp)
                }
            }
        }
        notifyDataSetChanged()
    }

    init {
        filterlist = ArrayList()
        filterlist.addAll(countryList)
    }
}