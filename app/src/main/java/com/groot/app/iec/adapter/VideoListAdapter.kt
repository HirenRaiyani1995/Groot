package com.groot.app.iec.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.groot.app.iec.activity.HowItWorksActivity
import com.groot.app.iec.databinding.ActivityHowItWorksBinding
import com.groot.app.iec.databinding.RowHowItWorksBinding
import com.groot.app.iec.model.vidoes.Datum
import java.util.*

class VideoListAdapter(
    var context: Context,
    private val videoList: ArrayList<Datum>,
    private val howItWorksActivity: HowItWorksActivity
) : RecyclerView.Adapter<VideoListAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        return MyViewHolder(
            RowHowItWorksBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup, false
            )
        )
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, i: Int) {
        val data = videoList[i]
        try {
            myViewHolder.binding.tvTitle.text = data.title
            myViewHolder.binding.tvDescription.text = data.description
        } catch (e: Exception) {
            e.printStackTrace()
        }

        myViewHolder.binding.item.setOnClickListener {
            howItWorksActivity.videoListClick(data.url,data.title,data.description)
        }
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class MyViewHolder(val binding: RowHowItWorksBinding) : RecyclerView.ViewHolder(
        binding.root
    )
}