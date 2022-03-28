package com.groot.app.iec.activity

import com.groot.app.iec.utils.AppUtils.isConnectedToInternet
import android.os.Bundle
import android.annotation.SuppressLint
import com.groot.app.iec.R
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.groot.app.iec.model.cmsResponse.CMSResponse
import com.groot.app.iec.rest.RetrofitRestClient
import com.groot.app.iec.utils.MySharedPreferences
import android.text.Spanned
import android.os.Build
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import com.groot.app.iec.databinding.ActivityPrivacyPolicyBinding
import com.groot.app.iec.utils.BaseActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.*

class PrivacyPolicyActivity : BaseActivity(), View.OnClickListener {
    private var binding: ActivityPrivacyPolicyBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        cmsAPICall()
        binding!!.btnBack.setOnClickListener(this)
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

    private fun cmsAPICall() {
        try {
            if (isConnectedToInternet(activity)) {
                showProgressDialog(activity)
                val call: Call<CMSResponse>?
                call = RetrofitRestClient.instance?.getCMSData(
                    "privacy-policy",
                    MySharedPreferences.getMySharedPreferences()?.accessToken
                )
                if (call == null) return
                call.enqueue(object : Callback<CMSResponse?> {
                    override fun onResponse(
                        call: Call<CMSResponse?>,
                        response: Response<CMSResponse?>
                    ) {
                        hideProgressDialog()
                        val cmsResponse: CMSResponse?
                        if (response.isSuccessful) {
                            cmsResponse = response.body()
                            if (Objects.requireNonNull(cmsResponse)?.code == 200) {
                                //    AppUtils.showToast(getActivity(), cmsResponse.getMessage());
                                val result: Spanned
                                try {
                                    result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        Html.fromHtml(
                                            cmsResponse!!.data?.content,
                                            Html.FROM_HTML_MODE_LEGACY
                                        )
                                    } else {
                                        Html.fromHtml(cmsResponse!!.data?.content)
                                    }
                                    binding!!.txtDescription.text = result
                                    binding!!.txtDescription.movementMethod =
                                        LinkMovementMethod.getInstance()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            } else if (Objects.requireNonNull(cmsResponse)?.code == 999) {
                                logout(activity)
                            } else {
                                hideProgressDialog()
                                showSnackBar(activity, cmsResponse!!.message)
                            }
                        } else {
                            showSnackBar(activity, response.message())
                        }
                    }

                    override fun onFailure(call: Call<CMSResponse?>, t: Throwable) {
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
}