package com.groot.app.iec.activity

import android.os.Bundle
import android.annotation.SuppressLint
import com.groot.app.iec.R
import android.content.Intent
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import android.content.ComponentName
import android.telephony.PhoneNumberUtils
import android.widget.Toast
import android.content.pm.PackageManager
import android.net.Uri
import android.view.View
import com.groot.app.iec.databinding.ActivityContactUsBinding
import com.groot.app.iec.utils.BaseActivity
import java.lang.Exception

class ContactUsActivity : BaseActivity(), View.OnClickListener {
    private var binding: ActivityContactUsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        super.onCreate(savedInstanceState)
        binding = ActivityContactUsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        binding?.apply {
            btnBack.setOnClickListener(this@ContactUsActivity)
            btnEmailAddress.setOnClickListener(this@ContactUsActivity)
            btnWhatsapp.setOnClickListener(this@ContactUsActivity)
        }
    }

    @SuppressLint("WrongConstant")
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnBack -> onBackPressed()
            R.id.btnWhatsapp -> openWhatsApp()
            R.id.btnEmailAddress -> {
                val intent = Intent(Intent.ACTION_SEND)
                intent.run {
                    type = "plain/text"
                    putExtra(Intent.EXTRA_EMAIL, arrayOf("support@grootapp.com"))
                    putExtra(Intent.EXTRA_SUBJECT, "")
                    putExtra(Intent.EXTRA_TEXT, "")
                }
                startActivity(Intent.createChooser(intent, ""))
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Animatoo.animateSlideRight(activity)
    }

    private fun openWhatsApp() {
        val isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp")
        if (isWhatsappInstalled) {
            try {
                val MobileNo = "91" + "9586606460"
                val sendIntent = Intent("android.intent.action.MAIN")
                sendIntent.component = ComponentName("com.whatsapp", "com.whatsapp.Conversation")
                sendIntent.putExtra(
                    "jid",
                    PhoneNumberUtils.stripSeparators(MobileNo) + "@s.whatsapp.net"
                )
                activity.startActivity(sendIntent)
            } catch (e: Exception) {
                // Log.e("TAG", "ERROR_OPEN_MESSANGER" + e.toString());
            }
        } else {
            val uri = Uri.parse("market://details?id=com.whatsapp")
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            Toast.makeText(
                activity, "WhatsApp not Installed",
                Toast.LENGTH_SHORT
            ).show()
            startActivity(goToMarket)
        }
    }

    private fun whatsappInstalledOrNot(uri: String): Boolean {
        val pm = activity.packageManager
        val appInstalled: Boolean = try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
        return appInstalled
    }
}