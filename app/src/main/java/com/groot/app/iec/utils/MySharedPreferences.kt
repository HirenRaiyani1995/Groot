package com.groot.app.iec.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.groot.app.iec.rest.RestConstant

class MySharedPreferences {
    private val SP_NAME = "Groot_Preference"
    private var sharedPreferences: SharedPreferences
    var context: Context? = null
    var MODE = 0
    private var editor: SharedPreferences.Editor
    var gson: Gson? = null

    constructor(context: Context) {
        this.context = context
        sharedPreferences = context.getSharedPreferences(PREFERENCE, MODE)
        editor = sharedPreferences.edit()
    }

    fun setFirstTimeLaunch(isFirstTime: Boolean) {
        editor.putBoolean(FIRST_LAUNCH, isFirstTime)
        editor.commit()
    }

    fun FirstLaunch(): Boolean {
        return sharedPreferences.getBoolean(FIRST_LAUNCH, true)
    }

    private constructor(context: Context, gson: Gson) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        this.gson = gson
    }

    var isLogin: Boolean
        get() = sharedPreferences.getBoolean("isLogin", false)
        set(isLogin) {
            editor.putBoolean("isLogin", isLogin).apply()
        }
    var isCardAdded: Boolean
        get() = sharedPreferences.getBoolean("isCardAdded", false)
        set(isCardAdded) {
            editor.putBoolean("isCardAdded", isCardAdded).apply()
        }
    var isFirstTimeLogin: Boolean
        get() = sharedPreferences.getBoolean("isFirstTimeLogin", true)
        set(isFirstTimeLogin) {
            editor.putBoolean("isFirstTimeLogin", isFirstTimeLogin).apply()
        }

    var isFirstTimeLoginVideoShow: Boolean
        get() = sharedPreferences.getBoolean("isFirstTimeLoginVideoShow", true)
        set(isFirstTimeLoginVideoShow) {
            editor.putBoolean("isFirstTimeLoginVideoShow", isFirstTimeLogin).apply()
        }

    var isNotificationOn: Boolean
        get() = sharedPreferences.getBoolean("isNotificationOn", true)
        set(isNotificationOn) {
            editor.putBoolean("isNotificationOn", isNotificationOn).apply()
        }
    var deviceId: String?
        get() = sharedPreferences.getString(RestConstant.DEVICE_Id, "")
        set(deviceid) {
            editor.putString(RestConstant.DEVICE_Id, deviceid).apply()
        }
    var firebaseId: String?
        get() = sharedPreferences.getString(RestConstant.FIREBASE_ID, "")
        set(firebaseId) {
            editor.putString(RestConstant.FIREBASE_ID, firebaseId).apply()
        }
    var userId: String?
        get() = sharedPreferences.getString(RestConstant.USER_ID, "")
        set(userId) {
            editor.putString(RestConstant.USER_ID, userId).apply()
        }
    var country: String?
        get() = sharedPreferences.getString(RestConstant.COUNTRY, "")
        set(country) {
            editor.putString(RestConstant.COUNTRY, country).apply()
        }
    var countryCode: String?
        get() = sharedPreferences.getString(RestConstant.COUNTRY_CODE, "")
        set(countryCode) {
            editor.putString(RestConstant.COUNTRY_CODE, countryCode).apply()
        }
    var currency: String?
        get() = sharedPreferences.getString(RestConstant.CURRENCY, "")
        set(currency) {
            editor.putString(RestConstant.CURRENCY, currency).apply()
        }
    var userName: String?
        get() = sharedPreferences.getString(RestConstant.USER_NAME, "")
        set(username) {
            editor.putString(RestConstant.USER_NAME, username).apply()
        }
    var lastName: String?
        get() = sharedPreferences.getString(RestConstant.LAST_NAME, "")
        set(lastName) {
            editor.putString(RestConstant.LAST_NAME, lastName).apply()
        }
    var contactNumber: String?
        get() = sharedPreferences.getString(RestConstant.CONTACT_NUMBER, "")
        set(contactNumber) {
            editor.putString(RestConstant.CONTACT_NUMBER, contactNumber).apply()
        }
    var email: String?
        get() = sharedPreferences.getString(RestConstant.EMAIL, "")
        set(email) {
            editor.putString(RestConstant.EMAIL, email).apply()
        }
    var accessToken: String?
        get() = sharedPreferences.getString(RestConstant.ACCESS_TOKEN, "")
        set(deviceToken) {
            editor.putString(RestConstant.ACCESS_TOKEN, deviceToken).apply()
        }
    var firstName: String?
        get() = sharedPreferences.getString(RestConstant.FirstName, "")
        set(firstName) {
            editor.putString(RestConstant.FirstName, firstName).apply()
        }
    var profile: String?
        get() = sharedPreferences.getString(RestConstant.PROFILE, "")
        set(profile) {
            editor.putString(RestConstant.PROFILE, profile).apply()
        }
    var bannerAdsKey: String?
        get() = sharedPreferences.getString(RestConstant.bannerAdsKey, "")
        set(bannerAdsKey) {
            editor.putString(RestConstant.bannerAdsKey, bannerAdsKey).apply()
        }
    var interstitialAdsKey: String?
        get() = sharedPreferences.getString(RestConstant.interstitialAdsKey, "")
        set(interstitialAdsKey) {
            editor.putString(RestConstant.interstitialAdsKey, interstitialAdsKey).apply()
        }
    var rewardedAdsKey: String?
        get() = sharedPreferences.getString(RestConstant.rewardedAdsKey, "")
        set(rewardedAdsKey) {
            editor.putString(RestConstant.rewardedAdsKey, rewardedAdsKey).apply()
        }
    var appIdKey: String?
        get() = sharedPreferences.getString(RestConstant.appIdKey, "")
        set(appIdKey) {
            editor.putString(RestConstant.appIdKey, appIdKey).apply()
        }
    var showBannerAds: String?
        get() = sharedPreferences.getString(RestConstant.showBannerAds, "1")
        set(showBannerAds) {
            editor.putString(RestConstant.showBannerAds, showBannerAds).apply()
        }
    var showInterstitialAds: String?
        get() = sharedPreferences.getString(RestConstant.showInterstitialAds, "1")
        set(showInterstitialAds) {
            editor.putString(RestConstant.showInterstitialAds, showInterstitialAds).apply()
        }
    var showRewardedAds: String?
        get() = sharedPreferences.getString(RestConstant.showRewardedAds, "1")
        set(showRewardedAds) {
            editor.putString(RestConstant.showRewardedAds, showRewardedAds).apply()
        }

    fun clearPreferences() {
        editor.clear().apply()
    }

    companion object {
        var mySharedPreferences: MySharedPreferences? = null
        private const val FIRST_LAUNCH = "firstLaunch"
        private const val PREFERENCE = "Groot"
        @JvmName("getMySharedPreferences1")
        fun getMySharedPreferences(): MySharedPreferences? {
            if (mySharedPreferences == null) {
                mySharedPreferences = GrootApp.grootApp?.let { MySharedPreferences(it, Gson()) }
            }
            return mySharedPreferences
        }
    }
}