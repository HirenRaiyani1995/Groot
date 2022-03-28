package com.groot.app.iec.activity

import com.groot.app.iec.utils.AppUtils.isConnectedToInternet
import com.groot.app.iec.utils.AppUtils.showToast
import android.os.Bundle
import android.annotation.SuppressLint
import com.groot.app.iec.R
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.groot.app.iec.rest.RetrofitRestClient
import com.groot.app.iec.utils.MySharedPreferences
import com.groot.app.iec.adapter.CountryListAdapter
import android.text.TextWatcher
import android.text.Editable
import com.groot.app.iec.model.countryList.CountryListResponse
import androidx.recyclerview.widget.LinearLayoutManager
import com.groot.app.iec.model.BasicModelResponse
import android.content.Intent
import android.view.View
import com.groot.app.iec.databinding.ActivitySelectCountryBinding
import com.groot.app.iec.model.countryList.DataItem
import com.groot.app.iec.utils.BaseActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.*

class SelectCountryListActivity : BaseActivity(), View.OnClickListener {
    private var binding: ActivitySelectCountryBinding? = null
    private var countryList: ArrayList<DataItem>? = null
    private var countryListAdapter: CountryListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        super.onCreate(savedInstanceState)
        binding = ActivitySelectCountryBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        countryList = ArrayList()
        binding!!.btnBack.setOnClickListener(this)
        binding!!.btnNext.setOnClickListener(this)
        binding!!.txtTryAgain.setOnClickListener(this)
        getCountryList()
        filterData()
    }

    @SuppressLint("WrongConstant")
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnBack -> onBackPressed()
            R.id.txtTryAgain -> getCountryList()
            R.id.btnNext -> if (countryList != null) {
                if (countryList!!.size > 0) {
                    var i = 0
                    while (i < countryList!!.size) {
                        if (countryList!![i].isSelect) {
                            MySharedPreferences.getMySharedPreferences()?.currency = (countryList!![i].country)
                            MySharedPreferences.getMySharedPreferences()?.currency = (countryList!![i].code)
                            MySharedPreferences.getMySharedPreferences()?.currency = (countryList!![i].symbol)
                            break
                        }
                        i++
                    }
                    if (MySharedPreferences.getMySharedPreferences()?.currency.equals("")
                    ) {
                        showSnackBar(activity, "Please select your currency")
                    } else {
                        addCurrencyAPICall()
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Animatoo.animateSlideRight(activity)
    }

    private fun filterData() {
        /*Filter data*/
        binding!!.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (isConnectedToInternet(activity)) {
                    try {
                        countryListAdapter!!.filter(binding!!.edtSearch.text.toString())
                        countryListAdapter!!.notifyDataSetChanged()
                        if (countryListAdapter!!.itemCount == 0) {
                            binding!!.RCVCountryList.visibility = View.GONE
                            //txtNoDataFoundDialog.setText(getResources().getString(R.string.no_contacts_found));
                            //txtNoDataFoundDialog.setVisibility(View.VISIBLE);
                        } else {
                            binding!!.RCVCountryList.visibility = View.VISIBLE
                            //txtNoDataFoundDialog.setText(getResources().getString(R.string.no_contacts_found));
                            //txtNoDataFoundDialog.setVisibility(View.GONE);
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    showSnackBar(activity, getString(R.string.no_internet))
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    //Todo:: Country list API Calling...
    private fun getCountryList() {
        try {
            if (isConnectedToInternet(activity)) {
                val call: Call<CountryListResponse>?
                call = RetrofitRestClient.instance?.countryList( MySharedPreferences.getMySharedPreferences()?.accessToken)
                if (call == null) return
                call.enqueue(object : Callback<CountryListResponse?> {
                    override fun onResponse(
                        call: Call<CountryListResponse?>,
                        response: Response<CountryListResponse?>
                    ) {
                        val countryListResponse: CountryListResponse?
                        if (response.isSuccessful) {
                            countryListResponse = response.body()
                            if (Objects.requireNonNull(countryListResponse)?.code == 200) {
                                countryList!!.clear()
                                countryList!!.addAll(countryListResponse!!.data!!)
                                if (MySharedPreferences.getMySharedPreferences()?.countryCode.equals("")
                                ) {
                                    MySharedPreferences.getMySharedPreferences()?.currency = ("")
                                }
                                for (i in countryList!!.indices) {
                                    if (countryList!![i].code.equals(
                                            MySharedPreferences.getMySharedPreferences()?.countryCode, ignoreCase = true
                                        )
                                    ) {
                                        countryList!![i].isSelect = true
                                        MySharedPreferences.getMySharedPreferences()?.country = (
                                            countryList!![i].country
                                        )
                                        MySharedPreferences.getMySharedPreferences()?.currency = (
                                            countryList!![i].symbol
                                        )
                                        MySharedPreferences.getMySharedPreferences()?.countryCode = (
                                            countryList!![i].code
                                        )
                                        break
                                    }
                                }
                                binding!!.RCVCountryList.layoutManager = LinearLayoutManager(
                                    activity,
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                                countryListAdapter = CountryListAdapter(
                                    activity,
                                    countryList!!,
                                    this@SelectCountryListActivity
                                )
                                binding!!.RCVCountryList.adapter = countryListAdapter
                                binding!!.RCVCountryList.visibility = View.VISIBLE
                                binding!!.txtNoDataFound.visibility = View.GONE
                                binding!!.txtTryAgain.visibility = View.GONE
                            } else if (Objects.requireNonNull(countryListResponse)?.code == 999) {
                                logout(activity)
                            } else if (Objects.requireNonNull(countryListResponse)?.code == 404) {
                                binding!!.RCVCountryList.visibility = View.GONE
                                binding!!.txtNoDataFound.text = countryListResponse!!.message
                                binding!!.txtNoDataFound.visibility = View.VISIBLE
                                binding!!.txtTryAgain.visibility = View.GONE
                            } else {
                                binding!!.RCVCountryList.visibility = View.GONE
                                binding!!.txtNoDataFound.text = countryListResponse!!.message
                                binding!!.txtNoDataFound.visibility = View.VISIBLE
                                binding!!.txtTryAgain.visibility = View.VISIBLE
                            }
                        } else {
                            binding!!.RCVCountryList.visibility = View.GONE
                            binding!!.txtNoDataFound.text = response.message()
                            binding!!.txtNoDataFound.visibility = View.VISIBLE
                            binding!!.txtTryAgain.visibility = View.VISIBLE
                        }
                    }

                    override fun onFailure(call: Call<CountryListResponse?>, t: Throwable) {
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

    /**
     * Add Currency API CALLING
     */
    private fun addCurrencyAPICall() {
        try {
            if (isConnectedToInternet(activity)) {
                showProgressDialog(activity)
                val params = HashMap<String, String>()
                params["currency_code"] = MySharedPreferences.getMySharedPreferences()?.countryCode.toString()
                val call: Call<BasicModelResponse>?
                call = RetrofitRestClient.instance?.updateCurrencyAPICall(
                    params,
                    MySharedPreferences.getMySharedPreferences()?.accessToken
                )
                if (call == null) return
                call.enqueue(object : Callback<BasicModelResponse?> {
                    override fun onResponse(
                        call: Call<BasicModelResponse?>,
                        response: Response<BasicModelResponse?>
                    ) {
                        hideProgressDialog()
                        val basicModelResponse: BasicModelResponse?
                        if (response.isSuccessful) {
                            basicModelResponse = response.body()
                            if (Objects.requireNonNull(basicModelResponse)?.code == 200) {
                                showToast(activity, basicModelResponse!!.message)
                                if (MySharedPreferences.getMySharedPreferences()!!
                                        .isFirstTimeLogin
                                ) {
                                    MySharedPreferences.getMySharedPreferences()
                                        ?.isFirstTimeLogin = (false)
                                    startActivity(
                                        Intent(
                                            activity,
                                            AddMultipleCardActivity::class.java
                                        )
                                    )
                                    finishAffinity()
                                    Animatoo.animateSlideRight(activity)
                                } else {
                                    startActivity(Intent(activity, DashboardActivity::class.java))
                                    finishAffinity()
                                    Animatoo.animateSlideRight(activity)
                                }
                            } else if (Objects.requireNonNull(basicModelResponse)?.code == 999) {
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