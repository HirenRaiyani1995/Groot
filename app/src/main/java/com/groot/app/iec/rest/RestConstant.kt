package com.groot.app.iec.rest

object RestConstant {
    //Todo:: Live Server URL
      const val BASE_URL = "https://api.grootapp.com/api/"

    //Todo:: Development Server URL
 //   const val BASE_URL = "http://dev.api.grootapp.com/api/"

    /*01*/
    const val SOCIAL_SIGN_UP = "register"

    /*02*/
    const val PAYMENT_METHOD = "payment-method"

    /*03*/
    const val CATEGORY_LIST = "category"

    /*04*/
    const val INCOME_SOURCE_LIST = "source"

    /*05*/
    const val ADD_BANK = "payment-method/create"

    /*06*/
    const val ANALYTICS = "analytics"

    /*07*/
    const val ADD_INCOME = "income/create"

    /*08*/
    const val ADD_EXPANSE = "expense/create"

    /*09*/
    const val UPDATE_INCOME = "income/update"

    /*10*/
    const val UPDATE_EXPANSE = "expense/update"

    /*11*/
    const val DELETE_INCOME = "income/delete"

    /*12*/
    const val DELETE_EXPENSE = "expense/delete"

    /*13*/
    const val ADD_CATEGORY = "category/create"

    /*14*/
    const val ADD_SOURCE = "category/income/create"

    /*15*/
    const val DELETE_CATEGORY = "category/delete"

    /*16*/
    const val DELETE_SOURCE = "category/income/delete"

    /*17*/
    const val UPDATE_BANK = "payment-method/update"

    /*18*/
    const val DELETE_BANK = "payment-method/delete"

    /*19*/
    const val COUNTRY_LIST = "country"

    /*20*/
    const val UPDATE_INCOME_CATEGORY = "category/income/update"

    /*21*/
    const val UPDATE_EXPENSE_CATEGORY = "category/update"

    /*22*/
    const val PIE_CHART = "analytics/category"

    /*23*/
    const val ANALYTICS_ALL = "analytics/all"

    /*24*/
    const val CARD_HIDE_SHOW = "payment-method/hide-show"

    /*25*/
    const val MULTIPLE_CARD_CREATE = "payment-method/default/create"

    /*26*/
    const val CURRENCY_UPDATE = "user/update/currency"

    /*27*/
    const val CMS = "page"

    const val APP_VERSION = "app/version"

    const val VIDEOS = "videos"

    const val GOOGLE_ADS = "ads"

    /*All Rest Constant Variables declaration*/
    const val bannerAdsKey = "bannerAdsKey"
    const val interstitialAdsKey = "interstitialAdsKey"
    const val rewardedAdsKey = "rewardedAdsKey"
    const val appIdKey = "appIdKey"
    const val showBannerAds = "showBannerAds"
    const val showInterstitialAds = "showInterstitialAds"
    const val showRewardedAds = "showRewardedAds"
    const val DEVICE_Id = "device_id"
    const val USER_ID = "id"
    const val USER_NAME = "user_name"
    const val LAST_NAME = "last_name"
    const val FirstName = "FirstName"
    const val CREATED_DATE = "created_date"
    const val CURRENT_DATE = "current_date"
    const val EMAIL = "email"
    const val ACCESS_TOKEN = "access_token"
    const val FIREBASE_ID = "firebase_id"
    const val PROFILE = "profile"
    const val CONTACT_NUMBER = "contact_number"
    const val COUNTRY = "country"
    const val COUNTRY_CODE = "country_code"
    const val CURRENCY = "currency"
    var SIGNUP_TYPE = "signup_type" /*CHECK FOR SIGN UP WITH GOOGLE OR FACEBOOK*/
    @JvmField
    var SELECTED_MONTH = ""
    @JvmField
    var SELECTED_YEAR = ""
    @JvmField
    var START_DATE = ""
    @JvmField
    var END_DATE = ""
    @JvmField
    var SELECTED_MONTH_ID = ""
    var OPEN_SCREEN = ""
    var STARTDT = ""
    var ENDDT = ""
    var DATE = ""
    var IS_FINISH_ACTIVITY = false
    @JvmField
    var OPEN_ADD_INCOME_CATEGORY_SCREEN = false
    @JvmField
    var OPEN_ADD_EXPENSE_CATEGORY_SCREEN = false

    //Todo:: For AddIncome
    @JvmField
    var ICDate = ""
    @JvmField
    var ICTextDate = ""
    @JvmField
    var ICCategory = ""
    @JvmField
    var ICNote = ""
    @JvmField
    var ICMoneyIn = ""
    @JvmField
    var ICImageData = ""
    @JvmField
    var IS_Add_FINISH = false

    //Todo:: For AddExpense
    @JvmField
    var EXDate = ""
    @JvmField
    var EXTextDate = ""
    @JvmField
    var EXCategory = ""
    @JvmField
    var EXNote = ""
    @JvmField
    var EXPaidBy = ""
    @JvmField
    var EXImageData = ""

    //Todo:: For Update Income
    @JvmField
    var UICDate = ""
    @JvmField
    var UICTextDate = ""
    @JvmField
    var UICCategory = ""
    @JvmField
    var UICNote = ""
    @JvmField
    var UICMoneyIn = ""
    @JvmField
    var UPDATE_ITEM = ""
    @JvmField
    var UCLICK_ID = ""
    @JvmField
    var UICIMAGEDATA = ""
    @JvmField
    var UIS_Add_FINISH = false
}