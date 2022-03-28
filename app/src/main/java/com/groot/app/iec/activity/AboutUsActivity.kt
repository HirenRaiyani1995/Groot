package com.groot.app.iec.activity

import android.os.Bundle
import android.annotation.SuppressLint
import android.view.View
import com.groot.app.iec.R
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.groot.app.iec.BuildConfig
import com.groot.app.iec.databinding.ActivityAboutUsBinding
import com.groot.app.iec.utils.BaseActivity

class AboutUsActivity : BaseActivity(), View.OnClickListener {
    private var binding: ActivityAboutUsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        super.onCreate(savedInstanceState)
        binding = ActivityAboutUsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        binding?.apply {
            btnBack.setOnClickListener(this@AboutUsActivity)
            txtDescription.text = "App Version: " + BuildConfig.VERSION_NAME
        }
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
}