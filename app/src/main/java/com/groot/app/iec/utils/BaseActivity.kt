package com.groot.app.iec.utils

import android.app.Activity
import android.app.Dialog
import com.groot.app.iec.utils.MySharedPreferences.Companion.getMySharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.app.ProgressDialog
import android.content.Context
import com.groot.app.iec.dialog.CustomProgressDialog
import android.os.Bundle
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import androidx.core.content.ContextCompat
import com.groot.app.iec.R
import android.widget.TextView
import android.view.WindowManager
import com.facebook.login.LoginManager
import android.content.Intent
import android.graphics.Color
import android.net.TrafficStats
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import com.groot.app.iec.activity.SocialLoginActivity
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import android.widget.Toast
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.groot.app.iec.rest.RestConstant
import java.lang.Exception
import java.net.SocketTimeoutException
import java.util.*

open class BaseActivity : AppCompatActivity() {
    private var customProgressDialog: CustomProgressDialog? = null
    private var bytes: Long = 0
    private var mb: String? = null

    private var mInterstitialAd: InterstitialAd? = null

    private var mRewardedAd: RewardedAd? = null
    private final var TAG = "MainActivity"

    // AdMob Rewarded Video Ad Id
   // private val AdId = "ca-app-pub-3940256099942544/5224354917"
    private val AdId = getMySharedPreferences()?.rewardedAdsKey.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun showProgressDialog(ctx: Context?) {
        try {
            customProgressDialog = CustomProgressDialog(ctx)
            customProgressDialog!!.show()
            customProgressDialog!!.setCancelable(false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun hideProgressDialog() {
        try {
            if (customProgressDialog != null && customProgressDialog!!.isShowing) {
                customProgressDialog!!.dismiss()
                customProgressDialog = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showSnackBar(context: Context?, message: String?) {
        val view =
            (findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup
        val snackbar = Snackbar.make(view, message!!, Snackbar.LENGTH_LONG).setActionTextColor(
            Color.WHITE
        )
        val viewGroup = snackbar.view as ViewGroup
        viewGroup.setBackgroundColor(ContextCompat.getColor(context!!, R.color.orange))
        val viewTv = snackbar.view
        val tv = viewTv.findViewById<TextView>(R.id.snackbar_text)
        tv.setTextColor(ContextCompat.getColor(context, R.color.white))
        tv.maxLines = 5
        snackbar.show()
    }

    fun hideKeyBoard(context: Context?) {
        try {
            // Check if no view has focus:
            val view = this.currentFocus
            if (view != null) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun logout(context: Context?) {
        val mySharedPreferences = getMySharedPreferences()
        mySharedPreferences?.clearPreferences()
        mySharedPreferences!!.isLogin = false
        mySharedPreferences.isCardAdded = false
        mySharedPreferences.userId = ""
        AppUtils.disconnectFromFacebook(context)
        AppUtils.disconnectFromGoogle(context)
        LoginManager.getInstance().logOut()
        val i = Intent(context, SocialLoginActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(i)
        Objects.requireNonNull(activity).finish()
        Animatoo.animateSlideLeft(context)
    }

    val activity: BaseActivity
        get() = this

    fun onFailureCall(ctx: Context?, t: Throwable) {
       // Log.e("-->OnFailure", t.message!!)
        Toast.makeText(ctx, t.message.toString(), Toast.LENGTH_SHORT).show()
        try {
            hideProgressDialog()
            if (t is SocketTimeoutException) {
                Toast.makeText(ctx, getString(R.string.connection_timeout), Toast.LENGTH_SHORT)
                    .show()
            } else {
                t.printStackTrace()
                Toast.makeText(ctx, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun clearRestConstant() {
        RestConstant.ICDate = ""
        RestConstant.ICTextDate = ""
        RestConstant.ICCategory = ""
        RestConstant.ICNote = ""
        RestConstant.ICMoneyIn = ""
        RestConstant.ICImageData = ""
        RestConstant.EXDate = ""
        RestConstant.EXTextDate = ""
        RestConstant.EXCategory = ""
        RestConstant.EXNote = ""
        RestConstant.EXPaidBy = ""
        RestConstant.EXImageData = ""
    }

    fun showInterstitialAds(context: Context){
        try {
            if (getMySharedPreferences()?.showInterstitialAds.toString() == "1") {
                val adRequest = AdRequest.Builder().build()

                InterstitialAd.load(
                    this,
                    getMySharedPreferences()?.interstitialAdsKey,
                    adRequest,
                    object : InterstitialAdLoadCallback() {
                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            Log.d("TAG", adError.message)
                            mInterstitialAd = null
                        }

                        override fun onAdLoaded(interstitialAd: InterstitialAd) {
                            Log.d("TAG", "Ad was loaded.")
                            mInterstitialAd = interstitialAd

                            if (mInterstitialAd != null) {
                                mInterstitialAd?.show(activity)
                            } else {
                                Log.d("TAG", "The interstitial ad wasn't ready yet.")
                            }
                        }
                    })

                mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        Log.d("TAG", "Ad was dismissed.")
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                        Log.d("TAG", "Ad failed to show.")
                    }

                    override fun onAdShowedFullScreenContent() {
                        Log.d("TAG", "Ad showed fullscreen content.")
                        mInterstitialAd = null
                    }
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun loadRewardedVideoAd(context: Context) {
        try {
            if (getMySharedPreferences()?.showRewardedAds.toString() == "1") {
                val adRequest = AdRequest.Builder().build()

                RewardedAd.load(
                    this,
                    getMySharedPreferences()?.rewardedAdsKey,
                    adRequest,
                    object : RewardedAdLoadCallback() {
                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            Log.d(TAG, adError.message)
                            mRewardedAd = null
                        }

                        override fun onAdLoaded(rewardedAd: RewardedAd) {
                            Log.d(TAG, "Ad was loaded.")
                            mRewardedAd = rewardedAd
                            // showRewardedVideoAd()

                            if (mRewardedAd != null) {
                                mRewardedAd?.show(
                                    context as Activity,
                                    OnUserEarnedRewardListener() {
                                        fun onUserEarnedReward(rewardItem: RewardItem) {
                                            Log.d(TAG, "User earned the reward.")
                                        }
                                    })
                            } else {
                                Log.d(TAG, "The rewarded ad wasn't ready yet.")
                            }
                        }
                    })

                mRewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdShowedFullScreenContent() {
                        // Called when ad is shown.
                        Log.d(TAG, "Ad was shown.")
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                        // Called when ad fails to show.
                        Log.d(TAG, "Ad failed to show.")
                    }

                    override fun onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        // Set the ad reference to null so you don't show the ad a second time.
                        Log.d(TAG, "Ad was dismissed.")
                        mRewardedAd = null
                    }
                }

                if (mRewardedAd != null) {
                    mRewardedAd?.show(this, OnUserEarnedRewardListener() {
                        fun onUserEarnedReward(rewardItem: RewardItem) {
                            Log.d(TAG, "User earned the reward.")
                        }
                    })
                } else {
                    Log.d(TAG, "The rewarded ad wasn't ready yet.")
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun showBannerAds(context: Context, layout:RelativeLayout){
        if(getMySharedPreferences()?.showBannerAds.toString() == "1") {
     //   MobileAds.initialize(this) {}
        layout.visibility = View.VISIBLE
        val adView = AdView(context)
        adView.adSize = AdSize.BANNER
        adView.adUnitId = getMySharedPreferences()?.bannerAdsKey

        layout.addView(adView)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        adView.adListener = object: AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            override fun onAdFailedToLoad(adError : LoadAdError) {
                // Code to be executed when an ad request fails.
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        }
        } else {
            layout.visibility = View.GONE
        }
    }
}