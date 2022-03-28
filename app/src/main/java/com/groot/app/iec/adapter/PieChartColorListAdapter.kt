package com.groot.app.iec.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.groot.app.iec.R
import com.groot.app.iec.databinding.RowPieChartColorBinding
import com.groot.app.iec.model.pie_chart.DataItem
import java.util.*

class PieChartColorListAdapter(
    var context: Context?,
    private val incomeCategoryList: ArrayList<DataItem>
) : RecyclerView.Adapter<PieChartColorListAdapter.MyViewHolder>() {
    private lateinit var multicolor: IntArray

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        return MyViewHolder(
            RowPieChartColorBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup, false
            )
        )
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, i: Int) {
        try {
            multicolor = context?.resources?.getIntArray(R.array.multicolor)!!
        }catch (e: Exception ){
            e.printStackTrace()
        }
        val data = incomeCategoryList[i]
        myViewHolder.binding.txtName.text = data.category
        myViewHolder.binding.txtName.isSelected = true
        myViewHolder.binding.ivColor.setColorFilter(multicolor[i])
    }

    override fun getItemCount(): Int {
        return incomeCategoryList.size
    }

    class MyViewHolder(var binding: RowPieChartColorBinding) : RecyclerView.ViewHolder(
        binding.root
    )
}