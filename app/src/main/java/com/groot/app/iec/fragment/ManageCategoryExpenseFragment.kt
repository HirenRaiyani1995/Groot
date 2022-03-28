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
import com.groot.app.iec.adapter.ManageExpenseCategoryListAdapter
import com.groot.app.iec.databinding.FragmentManageCategoryExpenseBinding
import com.groot.app.iec.model.BasicModelResponse
import com.groot.app.iec.model.category_list.CategoryListResponse
import com.groot.app.iec.model.category_list.DataItem
import com.groot.app.iec.rest.RetrofitRestClient.instance
import com.groot.app.iec.utils.AppUtils.isConnectedToInternet
import com.groot.app.iec.utils.BaseFragment
import com.groot.app.iec.utils.MySharedPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ManageCategoryExpenseFragment : BaseFragment() {
    private var binding: FragmentManageCategoryExpenseBinding? = null
    private var manageExpenseCategoryListAdapter: ManageExpenseCategoryListAdapter? = null
    private var expenseCategoryList: ArrayList<DataItem>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentManageCategoryExpenseBinding.inflate(inflater, container, false)

        //Todo:: Initialization........
        expenseCategoryList = ArrayList()

        //Todo:: Expense Category fetch from here...
        expenseCategoryListAPICall()

        //Todo:: FOR PASS DATA BETWEEN TWO FRAGMENTS
        try {
            // Register receiver
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

    /*FOR PASS DATA BETWEEN TWO FRAGMENTS*/
    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val message = intent.getStringExtra("API")
            if (message.equals("EXPENSE", ignoreCase = true)) {
                //Todo:: Get All Expense Category
                expenseCategoryListAPICall()
            }
        }
    }

    //Todo:: FOR PASS DATA BETWEEN TWO FRAGMENTS
    private val receiver1: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val isRefresh = intent.getStringExtra("isRefresh")
            if (isRefresh.equals("y", ignoreCase = true)) {
                //Todo:: Get All Income Category
                expenseCategoryListAPICall()
            }
        }
    }

    //Todo:: Expense Category fetch from here...
    private fun expenseCategoryListAPICall() {
        try {
            if (isConnectedToInternet(activity)) {
                val call: Call<CategoryListResponse>
                call = instance!!.getCategoryListAPICall(
                    MySharedPreferences.getMySharedPreferences()?.accessToken
                )
                if (call == null) return
                call.enqueue(object : Callback<CategoryListResponse?> {
                    override fun onResponse(
                        call: Call<CategoryListResponse?>,
                        response: Response<CategoryListResponse?>
                    ) {
                        val categoryListResponse: CategoryListResponse?
                        if (response.isSuccessful) {
                            categoryListResponse = response.body()
                            if (Objects.requireNonNull(categoryListResponse)?.code == 200) {
                                expenseCategoryList!!.clear()
                                expenseCategoryList!!.addAll(categoryListResponse!!.data!!)
                                if (expenseCategoryList != null && expenseCategoryList!!.size > 0) {
                                    binding!!.recyclerviewExpense.layoutManager =
                                        LinearLayoutManager(
                                            activity, LinearLayoutManager.VERTICAL, false
                                        )

                                    try {
                                        manageExpenseCategoryListAdapter =
                                            activity?.let {
                                                ManageExpenseCategoryListAdapter(
                                                    it,
                                                    expenseCategoryList!!,
                                                    this@ManageCategoryExpenseFragment
                                                )
                                            }
                                    }catch (e:Exception){
                                        e.printStackTrace()
                                    }
                                    binding!!.recyclerviewExpense.adapter =
                                        manageExpenseCategoryListAdapter
                                }
                            } else if (Objects.requireNonNull(categoryListResponse)?.code == 999) {
                                logout(activity)
                            } else {
                                showSnackBar(activity, categoryListResponse!!.message)
                            }
                        } else {
                            showSnackBar(activity, response.message())
                        }
                    }

                    override fun onFailure(call: Call<CategoryListResponse?>, t: Throwable) {
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
    fun editExpenseCategory(position: Int, Action: String) {
        if (Action.equals("HideShowClick", ignoreCase = true)) {
            if (expenseCategoryList!![position].isHide == 0) {
                expenseCategoryListAPICall(
                    expenseCategoryList!![position].id.toString(),
                    expenseCategoryList!![position].name,
                    "1"
                )
            } else {
                expenseCategoryListAPICall(
                    expenseCategoryList!![position].id.toString(),
                    expenseCategoryList!![position].name,
                    "0"
                )
            }
        } else {
            val i1 = Intent("DataIntent")
            i1.putExtra("categoryName", expenseCategoryList!![position].name)
            i1.putExtra("categoryId", expenseCategoryList!![position].id.toString())
            if (expenseCategoryList!![position].isHide == 0) {
                i1.putExtra("hideshow", "0")
            } else {
                i1.putExtra("hideshow", "1")
            }
            LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(i1)
        }
    }

    //Todo:: Hide Show API Calling
    private fun expenseCategoryListAPICall(id: String, name: String, flag: String) {
        try {
            if (isConnectedToInternet(activity)) {
                showProgressDialog(activity)
                val params = HashMap<String?, String?>()
                params["id"] = id
                params["name"] = name
                params["is_hide"] = flag
                val call: Call<BasicModelResponse>
                call = instance!!.updateExpenseCategoryAPICall(
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
                                //Todo:: Get All Expense Category
                                expenseCategoryListAPICall()
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