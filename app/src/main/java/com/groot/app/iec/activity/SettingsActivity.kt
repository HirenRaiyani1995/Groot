package com.groot.app.iec.activity

import android.os.Bundle
import android.annotation.SuppressLint
import com.groot.app.iec.R
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.groot.app.iec.utils.MySharedPreferences
import android.content.Intent
import android.content.DialogInterface
import android.app.AlertDialog
import android.view.View
import com.groot.app.iec.databinding.ActivitySettingsBinding
import com.groot.app.iec.utils.BaseActivity
import java.util.*

class SettingsActivity : BaseActivity(), View.OnClickListener {
    private var binding: ActivitySettingsBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        binding!!.btnBack.setOnClickListener(this)
        binding!!.btnSelectCountry.setOnClickListener(this)
        binding!!.btnLogout.setOnClickListener(this)
        binding!!.btnNotification.setOnClickListener(this)
    }

    @SuppressLint("WrongConstant")
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnBack -> onBackPressed()
            R.id.btnSelectCountry -> {
                startActivity(Intent(activity, SelectCountryListActivity::class.java))
                Animatoo.animateSlideLeft(activity)
            }
            R.id.btnLogout -> dialogLogout()
            R.id.btnNotification -> if (MySharedPreferences.getMySharedPreferences()!!
                    .isNotificationOn
            ) {
                MySharedPreferences.getMySharedPreferences()?.isNotificationOn = (false)
                binding!!.btnNotification.setImageDrawable(resources.getDrawable(R.drawable.ic_off_togal))
            } else {
                MySharedPreferences.getMySharedPreferences()?.isNotificationOn = (true)
                binding!!.btnNotification.setImageDrawable(resources.getDrawable(R.drawable.ic_on_togal))
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Animatoo.animateSlideRight(activity)
    }

    /**
     * Dialog Logout
     */
    private fun dialogLogout() {
        AlertDialog.Builder(Objects.requireNonNull(activity))
            .setMessage(getString(R.string.are_you_logout))
            .setPositiveButton(getString(R.string.yes)) { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
                logout(activity)
            }
            .setNegativeButton(getString(R.string.no)) { dialogInterface: DialogInterface, i: Int -> dialogInterface.dismiss() }
            .show()
    }
}