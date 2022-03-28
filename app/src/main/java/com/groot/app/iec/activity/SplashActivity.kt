package com.groot.app.iec.activity

import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.groot.app.iec.BuildConfig
import com.groot.app.iec.R
import com.groot.app.iec.dialog.AppUpdateDialog
import com.groot.app.iec.model.appVersion.AppVersionResponse
import com.groot.app.iec.model.googleAds.AdmobAdsResponse
import com.groot.app.iec.rest.RetrofitRestClient
import com.groot.app.iec.utils.AppUtils
import com.groot.app.iec.utils.AppUtils.getDeviceId
import com.groot.app.iec.utils.AppUtils.showToast
import com.groot.app.iec.utils.BaseActivity
import com.groot.app.iec.utils.MySharedPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class SplashActivity : BaseActivity() {
    private val appUpdateManager: AppUpdateManager? = null
    var appUpdateDialog: AppUpdateDialog? = null
    private var adsList: ArrayList<com.groot.app.iec.model.googleAds.Datum>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //Todo:: Initialize Update Dialog..
        appUpdateDialog = AppUpdateDialog(activity)

        //appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
        //checkUpdate();

        MySharedPreferences.getMySharedPreferences()?.deviceId = (getDeviceId(activity))

        //getGoogleAdsDataListAPICall()

        getAppVersionDataAPICall()
    }

    private fun checkUpdate() {
        val appUpdateInfoTask = appUpdateManager!!.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                startUpdateFlow(appUpdateInfo)
            } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                startUpdateFlow(appUpdateInfo)
            }
        }
    }

    private fun startUpdateFlow(appUpdateInfo: AppUpdateInfo) {
        try {
            appUpdateManager!!.startUpdateFlowForResult(
                appUpdateInfo,
                AppUpdateType.IMMEDIATE,
                this,
                IMMEDIATE_APP_UPDATE_REQ_CODE
            )
        } catch (e: SendIntentException) {
            e.printStackTrace()
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMMEDIATE_APP_UPDATE_REQ_CODE) {
            if (resultCode == RESULT_CANCELED) {
                finish()
            } else if (resultCode == RESULT_OK) {
                /*MySharedPreferences.getMySharedPreferences().setDeviceId(AppUtils.getDeviceId(getActivity()));
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        if (MySharedPreferences.getMySharedPreferences().isLogin()) {
                            if(MySharedPreferences.getMySharedPreferences().isFirstTimeLogin()){
                                startActivity(new Intent(SplashActivity.this, SelectCountryListActivity.class));
                            }else {
                                if (MySharedPreferences.getMySharedPreferences().isCardAdded()) {
                                    startActivity(new Intent(SplashActivity.this, DashboardActivity.class));
                                } else {
                                    startActivity(new Intent(SplashActivity.this, AddMultipleCardActivity.class));
                                }
                            }
                            finish();
                            Animatoo.animateFade(getActivity());
                        } else {
                            // ready for intent
                            startActivity(new Intent(SplashActivity.this, SocialLoginActivity.class));
                            finish();
                            Animatoo.animateSlideLeft(getActivity());
                        }
                    }
                }, 1000);*/
            } else {
                Toast.makeText(
                    applicationContext,
                    "Update Failed! Result Code: $resultCode",
                    Toast.LENGTH_LONG
                ).show()
                checkUpdate()
            }
        }
    }

    companion object {
        private const val IMMEDIATE_APP_UPDATE_REQ_CODE = 124
    }


    private fun getAppVersionDataAPICall() {
        try {
            if (AppUtils.isConnectedToInternet(activity)) {
                showProgressDialog(activity)
                val call: Call<AppVersionResponse> = RetrofitRestClient.instance?.getAppVersionData() ?: return
                call.enqueue(object : Callback<AppVersionResponse?> {
                    override fun onResponse(
                        call: Call<AppVersionResponse?>,
                        response: Response<AppVersionResponse?>
                    ) {
                        hideProgressDialog()
                        val appVersionResponse: AppVersionResponse?
                        if (response.isSuccessful) {
                            appVersionResponse = response.body()
                            if (appVersionResponse?.code == 200) {
                                if (BuildConfig.VERSION_NAME != appVersionResponse.data?.get(0)?.version) {
                                    if (appVersionResponse.data?.get(0)?.isUpdateRequired == "0") {
                                        appUpdateDialog?.apply {
                                            show()
                                            btnSkip.setOnClickListener {
                                                dismiss()
                                                loadLoginData()
                                            }
                                            btnUpdate.setOnClickListener {
                                                startActivity(
                                                    Intent(
                                                        Intent.ACTION_VIEW,
                                                        Uri.parse("https://play.google.com/store/apps/details?id=com.groot.app.iec")
                                                    )
                                                )
                                                finish()
                                            }
                                        }

                                    } else {
                                        appUpdateDialog?.apply {
                                            show()
                                            btnSkip.visibility = View.GONE
                                            btnUpdate.setOnClickListener {
                                                startActivity(
                                                    Intent(
                                                        Intent.ACTION_VIEW,
                                                        Uri.parse("https://play.google.com/store/apps/details?id=com.groot.app.iec")
                                                    )
                                                )
                                                finish()
                                            }
                                        }
                                    }
                                }else{
                                    loadLoginData()
                                }
                            } else {
                                showToast(activity, response.message())
                                finishAffinity()
                            }
                        } else {
                            showToast(activity, response.message())
                            finishAffinity()
                        }
                    }

                    override fun onFailure(call: Call<AppVersionResponse?>, t: Throwable) {
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

    fun loadLoginData(){
        Handler(mainLooper).postDelayed({
            if (MySharedPreferences.getMySharedPreferences()!!.isLogin) {
                if (MySharedPreferences.getMySharedPreferences()!!.isFirstTimeLogin) {
                    startActivity(
                        Intent(
                            this@SplashActivity,
                            SelectCountryListActivity::class.java
                        )
                    )
                } else {
                    if (MySharedPreferences.getMySharedPreferences()!!.isCardAdded) {
                        if (MySharedPreferences.getMySharedPreferences()?.currency
                                .equals("")
                        ) {
                            startActivity(
                                Intent(
                                    this@SplashActivity,
                                    SelectCountryListActivity::class.java
                                )
                            )
                        } else {
                            startActivity(
                                Intent(
                                    this@SplashActivity,
                                    DashboardActivity::class.java
                                )
                            )
                        }
                    } else {
                        startActivity(
                            Intent(
                                this@SplashActivity,
                                AddMultipleCardActivity::class.java
                            )
                        )
                    }
                }
                finish()
                Animatoo.animateFade(activity)
            } else {
                // ready for intent
                startActivity(
                    Intent(
                        this@SplashActivity,
                        SocialLoginActivity::class.java
                    )
                )
                finish()
                Animatoo.animateSlideLeft(activity)
            }
        }, 1000)
    }

    private fun getGoogleAdsDataListAPICall() {
        try {
            if (AppUtils.isConnectedToInternet(activity)) {
                val call: Call<AdmobAdsResponse> = RetrofitRestClient.instance?.getAdmobAdsListData(
                    MySharedPreferences.getMySharedPreferences()?.accessToken
                ) ?: return
                call.enqueue(object : Callback<AdmobAdsResponse?> {
                    override fun onResponse(
                        call: Call<AdmobAdsResponse?>,
                        response: Response<AdmobAdsResponse?>
                    ) {
                        val admobAdsResponse: AdmobAdsResponse?
                        if (response.isSuccessful) {
                            admobAdsResponse = response.body()
                            if (admobAdsResponse?.code == 200) {
                                try {
                                    adsList?.clear()
                                    adsList?.addAll(admobAdsResponse.data!!)

                                    if (adsList != null && adsList!!.size > 0) {
                                        MySharedPreferences.getMySharedPreferences()?.bannerAdsKey = adsList?.get(0)?.adsKey.toString()
                                        MySharedPreferences.getMySharedPreferences()?.interstitialAdsKey = adsList?.get(1)?.adsKey.toString()
                                        MySharedPreferences.getMySharedPreferences()?.rewardedAdsKey = adsList?.get(4)?.adsKey.toString()
                                        MySharedPreferences.getMySharedPreferences()?.appIdKey = adsList?.get(6)?.adsKey.toString()
                                        MySharedPreferences.getMySharedPreferences()?.showBannerAds = adsList?.get(0)?.enable.toString()
                                        MySharedPreferences.getMySharedPreferences()?.showInterstitialAds = adsList?.get(1)?.enable.toString()
                                        MySharedPreferences.getMySharedPreferences()?.showRewardedAds = adsList?.get(4)?.enable.toString()
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            } else if (admobAdsResponse?.code == 999) {
                                logout(activity)
                            } else {
                                showSnackBar(activity, admobAdsResponse!!.message)
                            }
                        } else {
                            showSnackBar(activity, response.message())
                        }
                    }

                    override fun onFailure(call: Call<AdmobAdsResponse?>, t: Throwable) {
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