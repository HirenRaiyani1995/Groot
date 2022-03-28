package com.groot.app.iec.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.groot.app.iec.R
import com.groot.app.iec.adapter.VideoListAdapter
import com.groot.app.iec.databinding.ActivityHowItWorksBinding
import com.groot.app.iec.model.vidoes.Datum
import com.groot.app.iec.model.vidoes.VideosResponse
import com.groot.app.iec.rest.RetrofitRestClient
import com.groot.app.iec.utils.AppUtils
import com.groot.app.iec.utils.BaseActivity
import com.groot.app.iec.utils.MySharedPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class HowItWorksActivity : BaseActivity(), View.OnClickListener {
    private var binding: ActivityHowItWorksBinding? = null
    private var videoListAdapter: VideoListAdapter? = null
    private var videoList: ArrayList<Datum>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        super.onCreate(savedInstanceState)
        binding = ActivityHowItWorksBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        binding!!.btnBack.setOnClickListener(this)

        videoList = ArrayList()

        getVideoListAPICall()
    }

    @SuppressLint("WrongConstant")
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnBack -> onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Animatoo.animateSlideRight(activity)
    }

    private fun getVideoListAPICall() {
        try {
            if (AppUtils.isConnectedToInternet(activity)) {
                showProgressDialog(activity)
                val call: Call<VideosResponse> = RetrofitRestClient.instance?.getVideoListData(
                    MySharedPreferences.getMySharedPreferences()?.accessToken
                ) ?: return
                call.enqueue(object : Callback<VideosResponse?> {
                    override fun onResponse(
                        call: Call<VideosResponse?>,
                        response: Response<VideosResponse?>
                    ) {
                        hideProgressDialog()
                        val videosResponse: VideosResponse?
                        if (response.isSuccessful) {
                            videosResponse = response.body()
                            if (videosResponse?.code == 200) {
                                try {
                                    videoList?.clear()
                                    videoList?.addAll(videosResponse.data!!)

                                    if (videoList != null && videoList!!.size > 0) {
                                        binding!!.recyclerviewHowItWorks.layoutManager =
                                            LinearLayoutManager(
                                                activity, LinearLayoutManager.VERTICAL, false
                                            )
                                        videoListAdapter =
                                            VideoListAdapter(
                                                activity,
                                                videoList!!,
                                                this@HowItWorksActivity
                                            )
                                        binding!!.recyclerviewHowItWorks.adapter =
                                            videoListAdapter
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            } else if (videosResponse?.code == 999) {
                                logout(activity)
                            } else {
                                hideProgressDialog()
                                showSnackBar(activity, videosResponse!!.message)
                            }
                        } else {
                            showSnackBar(activity, response.message())
                        }
                    }

                    override fun onFailure(call: Call<VideosResponse?>, t: Throwable) {
                        onFailureCall(activity, t)
                    }
                })
            } else {
                showSnackBar(activity, getString(R.string.no_internet))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun videoListClick(videoUrl: String?,videoTitle: String?,videoDesc: String?){
        startActivity(Intent(activity, YoutubeVideoDetailsActivity::class.java)
            .putExtra("videoUrl",videoUrl)
            .putExtra("videoTitle",videoTitle)
            .putExtra("videoDesc",videoDesc)

        )
        Animatoo.animateSlideLeft(activity)
    }
}