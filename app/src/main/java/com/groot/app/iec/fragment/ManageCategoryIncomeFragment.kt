package com.groot.app.iec.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.groot.app.iec.R
import com.groot.app.iec.adapter.ManageIncomeCategoryListAdapter
import com.groot.app.iec.databinding.FragmentManageCategoryIncomeBinding
import com.groot.app.iec.model.BasicModelResponse
import com.groot.app.iec.model.income_source.DataItem
import com.groot.app.iec.model.income_source.IncomeSourceResponse
import com.groot.app.iec.rest.RetrofitRestClient.instance
import com.groot.app.iec.utils.AppUtils.isConnectedToInternet
import com.groot.app.iec.utils.BaseFragment
import com.groot.app.iec.utils.MySharedPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ManageCategoryIncomeFragment : BaseFragment() {
    private var binding: FragmentManageCategoryIncomeBinding? = null
    private var manageIncomeCategoryListAdapter: ManageIncomeCategoryListAdapter? = null
    private var incomeCategoryList: ArrayList<DataItem>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_manage_category_income, container, false)
        binding = FragmentManageCategoryIncomeBinding.inflate(inflater, container, false)
        incomeCategoryList = ArrayList()

        //Todo:: Get All Income Category
        incomeCategoryListAPICall()

        //Todo:: FOR PASS DATA BETWEEN TWO FRAGMENTS
        try {
            LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(
                receiver,
                IntentFilter("Refresh")
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //Todo:: FOR PASS DATA BETWEEN TWO FRAGMENTS
        try {
            // Register receiver
            LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(
                receiver1,
                IntentFilter("RefreshIntent")
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding!!.root
    }

    //Todo:: FOR PASS DATA BETWEEN TWO FRAGMENTS
    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val message = intent.getStringExtra("API")
            if (message.equals("INCOME", ignoreCase = true)) {
                //Todo:: Get All Income Category
                incomeCategoryListAPICall()
            }
        }
    }

    //Todo:: FOR PASS DATA BETWEEN TWO FRAGMENTS
    private val receiver1: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val isRefresh = intent.getStringExtra("isRefresh")
            if (isRefresh.equals("y", ignoreCase = true)) {
                //Todo:: Get All Income Category
                incomeCategoryListAPICall()
            }
        }
    }

    //Todo:: Get All Income Category
    private fun incomeCategoryListAPICall() {
        try {
            if (isConnectedToInternet(activity)) {
                showProgressDialog(activity)
                val call: Call<IncomeSourceResponse>
                call = instance!!.getIncomeSourceListAPICall(
                    MySharedPreferences.getMySharedPreferences()?.accessToken
                )
                if (call == null) return
                call.enqueue(object : Callback<IncomeSourceResponse?> {
                    override fun onResponse(
                        call: Call<IncomeSourceResponse?>,
                        response: Response<IncomeSourceResponse?>
                    ) {
                        hideProgressDialog()
                        val incomeSourceResponse: IncomeSourceResponse?
                        if (response.isSuccessful) {
                            incomeSourceResponse = response.body()
                            if (Objects.requireNonNull(incomeSourceResponse)?.code == 200) {
                                incomeCategoryList!!.clear()
                                incomeCategoryList!!.addAll(incomeSourceResponse!!.data!!)
                                if (incomeCategoryList != null && incomeCategoryList!!.size > 0) {
                                    binding!!.recyclerviewIncome.layoutManager =
                                        LinearLayoutManager(
                                            activity, LinearLayoutManager.VERTICAL, false
                                        )
                                    try {
                                        manageIncomeCategoryListAdapter =
                                            activity?.let {
                                                ManageIncomeCategoryListAdapter(
                                                    it,
                                                    incomeCategoryList!!,
                                                    this@ManageCategoryIncomeFragment
                                                )
                                            }
                                    }catch (e:Exception){
                                        e.printStackTrace()
                                    }
                                    binding!!.recyclerviewIncome.adapter =
                                        manageIncomeCategoryListAdapter
                                }
                            } else if (Objects.requireNonNull(incomeSourceResponse)?.code == 999) {
                                logout(activity)
                            } else {
                                showSnackBar(activity, incomeSourceResponse!!.message)
                            }
                        } else {
                            showSnackBar(activity, response.message())
                        }
                    }

                    override fun onFailure(call: Call<IncomeSourceResponse?>, t: Throwable) {
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

    //Todo:: HideShow and Edit Category from here
    fun editIncomeCategory(position: Int, Action: String) {
        if (Action.equals("HideShowClick", ignoreCase = true)) {
            if (incomeCategoryList!![position].isHide == 0) {
                incomeCategoryListAPICall(
                    incomeCategoryList!![position].id.toString(),
                    incomeCategoryList!![position].name,
                    "1"
                )
            } else {
                incomeCategoryListAPICall(
                    incomeCategoryList!![position].id.toString(),
                    incomeCategoryList!![position].name,
                    "0"
                )
            }
        } else {
            val i1 = Intent("DataIntent")
            i1.putExtra("categoryName", incomeCategoryList!![position].name)
            i1.putExtra("categoryId", incomeCategoryList!![position].id.toString())
            if (incomeCategoryList!![position].isHide == 0) {
                i1.putExtra("hideshow", "0")
            } else {
                i1.putExtra("hideshow", "1")
            }
            LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(i1)
        }
    }

    //Todo:: Hide Show API Calling
    private fun incomeCategoryListAPICall(id: String, name: String, flag: String) {
        try {
            if (isConnectedToInternet(activity)) {
                showProgressDialog(activity)
                val params = HashMap<String?, String?>()
                params["id"] = id
                params["name"] = name
                params["is_hide"] = flag
                val call: Call<BasicModelResponse>
                call = instance!!.updateIncomeCategoryAPICall(
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
                                showSnackBar(activity, basicModelResponse!!.message)
                                //Todo:: Get All Income Category
                                incomeCategoryListAPICall()
                            } else if (Objects.requireNonNull(basicModelResponse)?.code == 999) {
                                logout(activity)
                            } else {
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