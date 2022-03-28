package com.groot.app.iec.utils

import com.groot.app.iec.utils.MySharedPreferences.Companion.getMySharedPreferences
import android.widget.TextView
import android.widget.Toast
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.annotation.SuppressLint
import okhttp3.RequestBody
import android.app.Activity
import android.content.Context
import android.provider.Settings
import android.text.TextUtils
import android.view.inputmethod.InputMethodManager
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.HttpMethod
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.groot.app.iec.rest.RestConstant
import com.groot.app.iec.utils.GrootApp.grootApp
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.Exception
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

object AppUtils {
    var Diff_Min = 0
    var Diff_Hours = 0
    fun printCurrency(value: Double?): String {
        val formatter =
            NumberFormat.getCurrencyInstance(Locale.ENGLISH)
        return formatter.format(value)
    }

    fun doubleToStringNoDecimal(d: String): String {
        val amount = d.toDouble()
        val formatter = NumberFormat.getInstance(Locale.US) as DecimalFormat
        formatter.applyPattern("#,###.00")
        return formatter.format(amount)
        //  return d;
    }

    fun isEmailValid(email: String?): Boolean {
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    fun isValidPassword(password: String?): Boolean {
        val PASSWORD_PATTERN = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$"
        val pattern = Pattern.compile(PASSWORD_PATTERN)
        val matcher = pattern.matcher(password)
        return matcher.matches()
    }

    fun getText(textView: TextView): String {
        return textView.text.toString().trim { it <= ' ' }
    }

    fun showToast(context: Context?, message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun timestamp(): String {
        val tsLong = System.currentTimeMillis() / 1000
        val ts = tsLong.toString()
        return getMySharedPreferences()!!.userId.toString() + ts
    }

    fun isConnectedToInternet(context: Context?): Boolean {
        val cm = grootApp!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var netInfo: NetworkInfo? = null
        if (cm != null) {
            netInfo = cm.activeNetworkInfo
        }
        return netInfo != null && netInfo.isConnected && netInfo.isAvailable
    }

    @SuppressLint("HardwareIds")
    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun getRequestBody(value: String?): RequestBody {
        return value!!.toRequestBody("multipart/form-data".toMediaTypeOrNull())
    }

    fun hideSoftKeyboard(activity: Activity) {
        val focusedView = activity.currentFocus
        if (focusedView != null) {
            val inputMethodManager =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(focusedView.windowToken, 0)
        }
    }

    // Check EditText or String is Empty or null etc.
    fun isEmpty(str: String?): Boolean {
        return TextUtils.isEmpty(str)
    }

    /***
     * Convert Date FROM DD/MM/YYYY to MM/DD/YYYY
     *
     * @param date
     * @return
     */
    fun ConvertDateToMM(date: String?): String? {
        val newformat = SimpleDateFormat("MM/dd/yyyy")
        val oldformat = SimpleDateFormat("dd/MM/yyyy")
        var reformattedStr: String? = null
        try {
            reformattedStr = newformat.format(oldformat.parse(date))
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return reformattedStr
    }

    /***
     * Convert Date FROM MM/DD/YYYY to DD/MM/YYYY
     *
     * @param date
     * @return
     */
    fun ConvertDateToDD(date: String?): String? {
        val oldformat = SimpleDateFormat("MM/dd/yyyy")
        val newformat = SimpleDateFormat("dd/MM/yyyy")
        var reformattedStr: String? = null
        try {
            reformattedStr = newformat.format(oldformat.parse(date))
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return reformattedStr
    }

    /***
     * Convert Date FROM MM/DD/YYYY to DD/MM/YYYY
     *
     * @param date
     * @return
     */
    fun ConvertStringToDate(date: String?): Date {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        var convertedDate = Date()
        try {
            convertedDate = dateFormat.parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return convertedDate
    }

    /***
     * Convert Short Date To Full Date From  dd/MM/yyyy to dd/mm/yyyy HH:mm:ss
     *
     * @param date
     * @return
     */
    fun ConvertStringFullDate(date: String?): Date {
        val dateFormat = SimpleDateFormat(
            "dd/mm/yyyy HH:mm:ss"
        )
        var convertedDate = Date()
        try {
            convertedDate = dateFormat.parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return convertedDate
    }

    /***
     * Get Date Difference Between Two Date
     *
     * @param dateStart
     * @param dateStop
     */
    fun DateDifference(dateStart: String?, dateStop: String?) {
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        var d1: Date? = null
        var d2: Date? = null
        try {
            d1 = format.parse(dateStart) as Date
            d2 = format.parse(dateStop) as Date

            // in milliseconds
            val diff = d2.time - d1.time

            //long diffSeconds = diff / 1000 % 60;
            val Diff_Minutes = diff / (60 * 1000) % 60
            val Diff_Hourss = diff / (60 * 60 * 1000) % 24
            //long diffDays = diff / (24 * 60 * 60 * 1000);
            Diff_Min = Diff_Minutes.toInt()
            Diff_Hours = Diff_Hourss.toInt()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun disconnectFromFacebook(context: Context?) {
        if (AccessToken.getCurrentAccessToken() == null) {
            return  // already logged out
        }
        GraphRequest(
            AccessToken.getCurrentAccessToken(),
            "/me/permissions/",
            null,
            HttpMethod.DELETE
        ) { LoginManager.getInstance().logOut() }.executeAsync()
    }

    fun disconnectFromGoogle(context: Context?) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        val googleSignInClient = context?.let { GoogleSignIn.getClient(it, gso) }
        googleSignInClient?.signOut()
    }

    fun clearUpdateConstant() {
        RestConstant.UICDate = ""
        RestConstant.UICTextDate = ""
        RestConstant.UICCategory = ""
        RestConstant.UICNote = ""
        RestConstant.UICMoneyIn = ""
        RestConstant.UPDATE_ITEM = ""
        RestConstant.UCLICK_ID = ""
        RestConstant.ICImageData = ""
        RestConstant.EXImageData = ""
        RestConstant.UICIMAGEDATA = ""
    }

    fun clearConstant(){
        RestConstant.SELECTED_MONTH_ID = ""
        RestConstant.SELECTED_MONTH = ""
        RestConstant.DATE = ""
        RestConstant.SELECTED_YEAR = ""
    }

    fun getCurrentMonth():Int{
        val c = Calendar.getInstance()
        val month = c[Calendar.MONTH]
        return month + 1
    }

    fun getCurrentYear():Int{
        val c = Calendar.getInstance()
        val year = c[Calendar.YEAR]
        return year
    }


    fun getCurrentMonthName():String
    {
        val cal = Calendar.getInstance()
        val month_date = SimpleDateFormat("MMMM")
        val month_name = month_date.format(cal.time)
        return month_name
    }

    fun getCurrentDate():String{
        var currentDT = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return currentDT
    }

    fun getCurrentTime():String{
        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        return currentTime
    }

    fun getYesterDayDate():String{
        val mydate = Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val yestr = dateFormat.format(mydate)
        return "$yestr 00:00:00"
    }
}