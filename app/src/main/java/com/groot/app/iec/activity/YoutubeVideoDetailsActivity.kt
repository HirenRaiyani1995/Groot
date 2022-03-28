package com.groot.app.iec.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubeStandalonePlayer
import com.google.android.youtube.player.YouTubeThumbnailLoader
import com.google.android.youtube.player.YouTubeThumbnailLoader.OnThumbnailLoadedListener
import com.google.android.youtube.player.YouTubeThumbnailView
import com.groot.app.iec.R
import com.groot.app.iec.databinding.ActivityYoutubeVideosDetailsBinding
import com.groot.app.iec.utils.BaseActivity
import java.lang.Exception
import java.util.regex.Pattern

class YoutubeVideoDetailsActivity : BaseActivity(), View.OnClickListener{
    private var binding: ActivityYoutubeVideosDetailsBinding? = null
    private var videoDesc = ""
    private var videoTitle = ""
    private var videolink = ""
    private var VideoId = ""
    private var readyForLoadingYoutubeThumbnail = true
    private var key1 = "AIzaSyD8Tlyo"
    private var key2 = "OYMHbTXHPNSu"
    private var key3 = "K7VjXKd"
    private var key4 = "UW9ZJc"

    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        super.onCreate(savedInstanceState)
        binding = ActivityYoutubeVideosDetailsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        binding!!.btnBack.setOnClickListener(this)
        binding!!.btnYoutubePlay.setOnClickListener(this)

        videolink = intent.getStringExtra("videoUrl").toString()
        videoDesc = intent.getStringExtra("videoDesc").toString()
        videoTitle = intent.getStringExtra("videoTitle").toString()

        binding?.apply {
            txtTitle.text = videoTitle
            txtDescription.text = videoDesc
        }

        VideoId = extractYTId(videolink).toString()

        try {
            if (readyForLoadingYoutubeThumbnail) {
                readyForLoadingYoutubeThumbnail = false
                /*  initialize the thumbnail image view , we need to pass Developer Key */
                binding!!.youtubePlayer.initialize(
                    key1 + key2 + key3 + key4,
                    object : YouTubeThumbnailView.OnInitializedListener {
                        override fun onInitializationSuccess(
                            youTubeThumbnailView: YouTubeThumbnailView,
                            youTubeThumbnailLoader: YouTubeThumbnailLoader
                        ) {
                            //when initialization is sucess, set the video id to thumbnail to load
                            youTubeThumbnailLoader.setVideo(
                                extractYTId(
                                    videolink
                                )
                            )
                           // Log.e("-->videoId", VideoId)
                            youTubeThumbnailLoader.setOnThumbnailLoadedListener(object :
                                OnThumbnailLoadedListener {
                                override fun onThumbnailLoaded(
                                    youTubeThumbnailView: YouTubeThumbnailView,
                                    s: String
                                ) {
                                    //when thumbnail loaded successfully release the thumbnail loader as we are showing thumbnail in adapter
                                    youTubeThumbnailLoader.release()
                                }

                                override fun onThumbnailError(
                                    youTubeThumbnailView: YouTubeThumbnailView,
                                    errorReason: YouTubeThumbnailLoader.ErrorReason
                                ) {
                                    //print or show error when thumbnail load failed
                                    Log.e("-->ThumbnailError", "Youtube Thumbnail Error")
                                }
                            })
                            readyForLoadingYoutubeThumbnail = true
                        }

                        override fun onInitializationFailure(
                            youTubeThumbnailView: YouTubeThumbnailView,
                            youTubeInitializationResult: YouTubeInitializationResult
                        ) {
                            //print or show error when initialization failed
                            Log.e("-->", "Youtube Initialization Failure")
                            readyForLoadingYoutubeThumbnail = true
                        }
                    })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun extractYTId(ytUrl: String?): String? {
        val pattern =
            "^(?:(?:\\w*.?://)?\\w*.?\\w*-?.?\\w*/(?:embed|e|v|watch|.*/)?\\??(?:feature=\\w*\\.?\\w*)?&?(?:v=)?/?)([\\w\\d_-]+).*"
        val compiledPattern = Pattern.compile(
            pattern,
            Pattern.CASE_INSENSITIVE
        )
        val matcher = compiledPattern.matcher(ytUrl)
        return if (matcher.find()) {
            matcher.group(1)
        } else null
    }

    @SuppressLint("WrongConstant")
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnBack -> onBackPressed()
            R.id.btn_youtube_play -> {
                val intent = YouTubeStandalonePlayer.createVideoIntent(
                   activity,
                    key1 + key2 + key3 + key4,
                    VideoId,  //video id
                    100,  //after this time, video will start automatically
                    true,  //autoplay or not
                    true
                ) //lightbox mode or not; show the video in a small box

                startActivity(intent)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Animatoo.animateSlideRight(activity)
    }
}