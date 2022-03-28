package com.groot.app.iec.activity

import com.groot.app.iec.utils.AppUtils.isConnectedToInternet
import android.os.Bundle
import com.groot.app.iec.R
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.groot.app.iec.model.BasicModelResponse
import com.groot.app.iec.rest.RetrofitRestClient
import com.groot.app.iec.utils.MySharedPreferences
import android.content.Intent
import android.text.InputFilter
import android.view.View
import com.groot.app.iec.databinding.ActivityAddMultipleBankBinding
import com.groot.app.iec.utils.BaseActivity
import com.groot.app.iec.utils.DecimalDigitsInputFilter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.*

class AddMultipleCardActivity : BaseActivity(), View.OnClickListener {
    var binding: ActivityAddMultipleBankBinding? = null
    var cash = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMultipleBankBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)

        binding?.apply {
            edtCardAmount.filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(10, 2))
          //  edtCashAmount.filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(10, 2))
            btnSave.setOnClickListener(this@AddMultipleCardActivity)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnSave -> {
                addMultipleCardAPICall("", "")
               /* cash = if (binding!!.edtCashAmount.text.toString().equals("", ignoreCase = true)) {
                    "0"
                } else {
                    binding!!.edtCashAmount.text.toString()
                }
                if (!binding!!.edtCardAmount.text.toString().equals("", ignoreCase = true)) {
                    if (binding!!.edtCardName.text.toString().equals("", ignoreCase = true)) {
                        showSnackBar(activity, "Please add card name")
                    } else {
                        addMultipleCardAPICall(
                            binding!!.edtCardName.text.toString(),
                            binding!!.edtCardAmount.text.toString()
                        )
                    }
                }
                if (binding!!.edtCardName.text.toString().equals("", ignoreCase = true)
                    && binding!!.edtCardAmount.text.toString().equals("", ignoreCase = true)
                ) {

                }*/
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Animatoo.animateSlideDown(activity)
    }

    /**
     * Add Card API CALLING
     */
    private fun addMultipleCardAPICall(method_name: String, method_amount: String) {
        try {
            if (isConnectedToInternet(activity)) {
                showProgressDialog(activity)
                val params = HashMap<String, String>()
                params["cash_amount"] = cash
                if (!method_name.equals("", ignoreCase = true)) {
                    params["method_name"] = method_name
                    params["method_amount"] = method_amount
                }
                val call: Call<BasicModelResponse> = RetrofitRestClient.instance?.addMultipleCardAPICall(
                    params,
                    MySharedPreferences.getMySharedPreferences()?.accessToken
                ) ?: return
                call.enqueue(object : Callback<BasicModelResponse?> {
                    override fun onResponse(
                        call: Call<BasicModelResponse?>,
                        response: Response<BasicModelResponse?>
                    ) {
                        hideProgressDialog()
                        val basicModelResponse: BasicModelResponse?
                        if (response.isSuccessful) {
                            basicModelResponse = response.body()
                            if (basicModelResponse?.code == 200) {
                                //showSnackBar(activity, basicModelResponse?.message)
                                MySharedPreferences.getMySharedPreferences()?.isCardAdded = true
                                startActivity(
                                    Intent(
                                        this@AddMultipleCardActivity,
                                        DashboardActivity::class.java
                                    )
                                )
                                finish()
                                Animatoo.animateFade(activity)
                            } else if (basicModelResponse?.code == 999) {
                                logout(activity)
                            } else {
                                hideProgressDialog()
                                showSnackBar(activity, basicModelResponse!!.message)
                            }
                        } else {
                            showSnackBar(activity, response.message())
                        }
                    }

                    override fun onFailure(call: Call<BasicModelResponse?>, t: Throwable) {
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