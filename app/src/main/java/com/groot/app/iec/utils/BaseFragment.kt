package com.groot.app.iec.utils

import android.content.Context
import com.groot.app.iec.utils.MySharedPreferences.Companion.getMySharedPreferences
import com.groot.app.iec.dialog.CustomProgressDialog
import android.view.ViewGroup
import android.os.Build
import com.google.android.material.snackbar.Snackbar
import androidx.core.content.ContextCompat
import com.groot.app.iec.R
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.facebook.login.LoginManager
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.groot.app.iec.activity.SocialLoginActivity
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import java.lang.Exception
import java.lang.IllegalStateException
import java.net.SocketTimeoutException

open class BaseFragment : Fragment() {
    private var customProgressDialog: CustomProgressDialog? = null
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

    fun requiredContext(): Context {
        return this.context
            ?: throw IllegalStateException("Fragment $this not attached to a context.")
    }

    fun showSnackBar(context: Context?, message: String?) {
        try {
            var view: ViewGroup? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                view = (requireActivity().findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup
            }
            val snackbar =
                Snackbar.make(view!!, message!!, Snackbar.LENGTH_LONG).setActionTextColor(
                    Color.WHITE
                )
            snackbar.duration = 3000
            val viewGroup = snackbar.view as ViewGroup
            viewGroup.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_orange))
            val viewTv = snackbar.view
            val tv = viewTv.findViewById<TextView>(R.id.snackbar_text)
            tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            tv.maxLines = 5
            snackbar.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showSuccessSnackBar(context: Context?, message: String?) {
        try {
            var view: ViewGroup? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                view = (requireActivity().findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup
            }
            val snackbar =
                Snackbar.make(view!!, message!!, Snackbar.LENGTH_LONG).setActionTextColor(
                    Color.WHITE
                )
            snackbar.duration = 3000
            val viewGroup = snackbar.view as ViewGroup
            viewGroup.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_yellow))
            val viewTv = snackbar.view
            val tv = viewTv.findViewById<TextView>(R.id.snackbar_text)
            tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            tv.maxLines = 5
            snackbar.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    fun logout(context: Context?) {
        val mySharedPreferences = getMySharedPreferences()
        mySharedPreferences!!.isLogin = false
        mySharedPreferences.userId = ""
        AppUtils.disconnectFromFacebook(context)
        AppUtils.disconnectFromGoogle(context)
        LoginManager.getInstance().logOut()
        val i = Intent(context, SocialLoginActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(i)
        requireActivity().finish()
        Animatoo.animateSlideLeft(activity)
    }

    fun onFailureCall(ctx: Context?, t: Throwable) {
        try {
            hideProgressDialog()
            if (t is SocketTimeoutException) {
                showSnackBar(ctx, getString(R.string.connection_timeout))
            } else {
                t.printStackTrace()
                showSnackBar(ctx, getString(R.string.something_went_wrong))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onFailureCallNew(
        ctx: Context?,
        t: Throwable,
        txtNoDataFound: AppCompatTextView,
        txtTryAgain: AppCompatTextView
    ) {
        try {
            hideProgressDialog()
            if (t is SocketTimeoutException) {
                txtNoDataFound.text = getString(R.string.connection_timeout)
                txtTryAgain.visibility = View.VISIBLE
                txtNoDataFound.visibility = View.VISIBLE
            } else {
                t.printStackTrace()
                txtNoDataFound.text = getString(R.string.something_went_wrong)
                txtTryAgain.visibility = View.VISIBLE
                txtNoDataFound.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun hideKeyBoard(context: Context?) {
        try {
            // Check if no view has focus:
            val view = requireActivity().currentFocus
            if (view != null) {
                val imm =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}