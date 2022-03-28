package com.groot.app.iec.rest

import com.groot.app.iec.model.social_signup.SocialSignupResponse
import com.groot.app.iec.model.payment_method.PaymentMethodResponse
import com.groot.app.iec.model.category_list.CategoryListResponse
import com.groot.app.iec.model.income_source.IncomeSourceResponse
import com.groot.app.iec.model.add_bank.AddBankResponse
import com.groot.app.iec.model.analytics.AnalyticsResponse
import okhttp3.RequestBody
import com.groot.app.iec.model.add_income.AddIncomeResponse
import com.groot.app.iec.model.add_expanse.AddExpanseResponse
import com.groot.app.iec.model.BasicModelResponse
import com.groot.app.iec.model.add_category.AddCategoryResponse
import com.groot.app.iec.model.appVersion.AppVersionResponse
import com.groot.app.iec.model.countryList.CountryListResponse
import com.groot.app.iec.model.pie_chart.PieChartResponse
import com.groot.app.iec.model.category_details.CategoryDetailsResponse
import com.groot.app.iec.model.card_hide_show.CardHideShowResponse
import com.groot.app.iec.model.cmsResponse.CMSResponse
import com.groot.app.iec.model.googleAds.AdmobAdsResponse
import com.groot.app.iec.model.vidoes.VideosResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*
import java.util.HashMap

interface ApiService {
    /*1) SOCIAL SIGN UP API CALL*/
    @FormUrlEncoded
    @Headers("Accept-Encoding: identity")
    @POST(RestConstant.SOCIAL_SIGN_UP)
    fun socialSignUp(@FieldMap parameters: HashMap<String, String>): Call<SocialSignupResponse>

    /*2) PAYMENT METHOD API CALL*/
    @GET(RestConstant.PAYMENT_METHOD)
    @Headers("Accept-Encoding: identity")
    fun getPaymentMethodAPICall(@Header("Authorization") authHeader: String?): Call<PaymentMethodResponse>

    /*3) CATEGORY LIST API CALL*/
    @GET(RestConstant.CATEGORY_LIST)
    @Headers("Accept-Encoding: identity")
    fun getCategoryListAPICall(@Header("Authorization") authHeader: String?): Call<CategoryListResponse>

    /*4) INCOME SOURCE LIST API CALL*/
    @GET(RestConstant.INCOME_SOURCE_LIST)
    @Headers("Accept-Encoding: identity")
    fun getIncomeSourceListAPICall(@Header("Authorization") authHeader: String?): Call<IncomeSourceResponse>

    /*5) ADD BANK API CALL*/
    @FormUrlEncoded
    @POST(RestConstant.ADD_BANK)
    @Headers("Accept-Encoding: identity")
    fun addBankAPICall(
        @FieldMap parameters: HashMap<String, String>,
        @Header("Authorization") authHeader: String?
    ): Call<AddBankResponse>

    /*6) ANALYTICS LIST API CALL*/
    @FormUrlEncoded
    @POST(RestConstant.ANALYTICS)
    @Headers("Accept-Encoding: identity")
    fun getAnalyticsListAPICall(
        @FieldMap parameters: HashMap<String?, String?>,
        @Header("Authorization") authHeader: String?
    ): Call<AnalyticsResponse>

    /*7) ADD INCOME API CALL*/
    @Multipart
    @POST(RestConstant.ADD_INCOME)
    @Headers("Accept-Encoding: identity")
    fun addIncomeAPICall(
        @PartMap parameters: HashMap<String, RequestBody>,
        @Header("Authorization") authHeader: String?,
        @Part bodyImg1: MultipartBody.Part?
    ): Call<AddIncomeResponse>

    /*8) ADD EXPANSE API CALL*/
    @Multipart
    @POST(RestConstant.ADD_EXPANSE)
    @Headers("Accept-Encoding: identity")
    fun addExpanseAPICall(
        @PartMap parameters: HashMap<String, RequestBody>,
        @Header("Authorization") authHeader: String?,
        @Part bodyImg1: MultipartBody.Part?
    ): Call<AddExpanseResponse>

    /*9) DELETE INCOME API CALL*/
    @FormUrlEncoded
    @POST(RestConstant.DELETE_INCOME)
    @Headers("Accept-Encoding: identity")
    fun deleteIncomeAPICall(
        @FieldMap parameters: HashMap<String?, String?>,
        @Header("Authorization") authHeader: String?
    ): Call<BasicModelResponse>

    /*10) DELETE INCOME API CALL*/
    @FormUrlEncoded
    @POST(RestConstant.DELETE_EXPENSE)
    @Headers("Accept-Encoding: identity")
    fun deleteExpenseAPICall(
        @FieldMap parameters: HashMap<String?, String?>,
        @Header("Authorization") authHeader: String?
    ): Call<BasicModelResponse>

    /*11) UPDATE INCOME API CALL*/
    @Multipart
    @POST(RestConstant.UPDATE_INCOME)
    @Headers("Accept-Encoding: identity")
    fun updateIncomeAPICall(
        @PartMap parameters: HashMap<String, RequestBody>,
        @Header("Authorization") authHeader: String?,
        @Part bodyImg1: MultipartBody.Part?
    ): Call<AddIncomeResponse>

    /*12) UPDATE EXPANSE API CALL*/
    @Multipart
    @POST(RestConstant.UPDATE_EXPANSE)
    @Headers("Accept-Encoding: identity")
    fun updateExpanseAPICall(
        @PartMap parameters: HashMap<String, RequestBody>,
        @Header("Authorization") authHeader: String?,
        @Part bodyImg1: MultipartBody.Part?
    ): Call<AddExpanseResponse>

    /*13) ADD CATEGORY API CALL*/
    @FormUrlEncoded
    @POST(RestConstant.ADD_CATEGORY)
    @Headers("Accept-Encoding: identity")
    fun addCategoryAPICall(
        @FieldMap parameters: HashMap<String, String>,
        @Header("Authorization") authHeader: String?
    ): Call<AddCategoryResponse>

    /*14) DELETE CATEGORY API CALL*/
    @FormUrlEncoded
    @POST(RestConstant.DELETE_CATEGORY)
    @Headers("Accept-Encoding: identity")
    fun deleteCategoryAPICall(
        @FieldMap parameters: HashMap<String, String>,
        @Header("Authorization") authHeader: String?
    ): Call<BasicModelResponse>

    /*15) UPDATE BANK API CALL*/
    @FormUrlEncoded
    @POST(RestConstant.UPDATE_BANK)
    @Headers("Accept-Encoding: identity")
    fun updateBankAPICall(
        @FieldMap parameters: HashMap<String, String>,
        @Header("Authorization") authHeader: String?
    ): Call<BasicModelResponse>

    /*16) DELETE BANK API CALL*/
    @FormUrlEncoded
    @POST(RestConstant.DELETE_BANK)
    @Headers("Accept-Encoding: identity")
    fun deleteBankAPICall(
        @FieldMap parameters: HashMap<String, String>,
        @Header("Authorization") authHeader: String?
    ): Call<BasicModelResponse>

    /*17) DELETE SOURCE API CALL*/
    @FormUrlEncoded
    @POST(RestConstant.DELETE_SOURCE)
    @Headers("Accept-Encoding: identity")
    fun deleteSourceAPICall(
        @FieldMap parameters: HashMap<String?, String?>,
        @Header("Authorization") authHeader: String?
    ): Call<BasicModelResponse>

    /*18) ADD SOURCE API CALL*/
    @FormUrlEncoded
    @POST(RestConstant.ADD_SOURCE)
    @Headers("Accept-Encoding: identity")
    fun addSourceAPICall(
        @FieldMap parameters: HashMap<String, String>,
        @Header("Authorization") authHeader: String?
    ): Call<BasicModelResponse>

    /*19) COUNTRY LIST API CALL*/
    @Headers("Accept-Encoding: identity")
    @GET(RestConstant.COUNTRY_LIST)
    fun countryList(@Header("Authorization") authHeader: String?): Call<CountryListResponse>

    /*20) UPDATE_INCOME_CATEGORY API CALL*/
    @FormUrlEncoded
    @POST(RestConstant.UPDATE_INCOME_CATEGORY)
    @Headers("Accept-Encoding: identity")
    fun updateIncomeCategoryAPICall(
        @FieldMap parameters: HashMap<String?, String?>,
        @Header("Authorization") authHeader: String?
    ): Call<BasicModelResponse>

    /*21) UPDATE_INCOME_CATEGORY API CALL*/
    @FormUrlEncoded
    @POST(RestConstant.UPDATE_EXPENSE_CATEGORY)
    @Headers("Accept-Encoding: identity")
    fun updateExpenseCategoryAPICall(
        @FieldMap parameters: HashMap<String?, String?>,
        @Header("Authorization") authHeader: String?
    ): Call<BasicModelResponse>

    /*22) PIE CHART API CALL*/
    @FormUrlEncoded
    @POST(RestConstant.PIE_CHART)
    @Headers("Accept-Encoding: identity")
    fun getPieChartAnalyticsListAPICall(
        @FieldMap parameters: HashMap<String?, String?>,
        @Header("Authorization") authHeader: String?
    ): Call<PieChartResponse>

    /*23) ANALYTICS DETAILS API CALL*/
    @FormUrlEncoded
    @POST(RestConstant.ANALYTICS_ALL)
    @Headers("Accept-Encoding: identity")
    fun getCategoryAnalyticsDetailsAPICall(
        @FieldMap parameters: HashMap<String?, String?>,
        @Header("Authorization") authHeader: String?
    ): Call<CategoryDetailsResponse>

    /*24) CARD HIDE SHOW API CALL*/
    @FormUrlEncoded
    @POST(RestConstant.CARD_HIDE_SHOW)
    @Headers("Accept-Encoding: identity")
    fun applyHideShowAPICall(
        @FieldMap parameters: HashMap<String?, String?>,
        @Header("Authorization") authHeader: String?
    ): Call<CardHideShowResponse>

    /*25) MULTIPLE CARD CREATE API CALL*/
    @FormUrlEncoded
    @POST(RestConstant.MULTIPLE_CARD_CREATE)
    @Headers("Accept-Encoding: identity")
    fun addMultipleCardAPICall(
        @FieldMap parameters: HashMap<String, String>,
        @Header("Authorization") authHeader: String?
    ): Call<BasicModelResponse>

    /*26) CURRENCY UPDATE API CALL*/
    @FormUrlEncoded
    @POST(RestConstant.CURRENCY_UPDATE)
    @Headers("Accept-Encoding: identity")
    fun updateCurrencyAPICall(
        @FieldMap parameters: HashMap<String, String>,
        @Header("Authorization") authHeader: String?
    ): Call<BasicModelResponse>

    /*27) CMS API CALL*/
    @GET(RestConstant.CMS)
    @Headers("Accept: application/json", "Content-Type: application/json","Accept-Encoding: identity")
    fun getCMSData(
        @Query("slug") userId: String,
        @Header("Authorization") authHeader: String?
    ): Call<CMSResponse>

    /*28) APP VERSION API CALL*/
    @GET(RestConstant.APP_VERSION)
    @Headers("Accept-Encoding: identity")
    fun getAppVersionData(): Call<AppVersionResponse>

    /*29) CMS API CALL*/
    @GET(RestConstant.VIDEOS)
    @Headers("Accept-Encoding: identity")
    fun getVideoListData(
        @Header("Authorization") authHeader: String?
    ): Call<VideosResponse>

    /*30) CMS API CALL*/
    @GET(RestConstant.GOOGLE_ADS)
    @Headers("Accept-Encoding: identity")
    fun getAdmobAdsListData(
        @Header("Authorization") authHeader: String?
    ): Call<AdmobAdsResponse>
}