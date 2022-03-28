package com.groot.app.iec.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.groot.app.iec.R
import com.groot.app.iec.activity.AddIncomeExpenseActivity
import com.groot.app.iec.adapter.IncomeCardTransactionAdapter
import com.groot.app.iec.databinding.FragmentCardTransactionBinding
import com.groot.app.iec.model.BasicModelResponse
import com.groot.app.iec.model.analytics.AnalyticsResponse
import com.groot.app.iec.model.analytics.DataItem
import com.groot.app.iec.rest.RestConstant
import com.groot.app.iec.rest.RetrofitRestClient
import com.groot.app.iec.utils.AppUtils
import com.groot.app.iec.utils.BaseFragment
import com.groot.app.iec.utils.MySharedPreferences
import com.groot.app.iec.utils.swipableRecyclerview.RecyclerTouchListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import android.app.Activity
import android.content.*
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.lang.ClassCastException


class CardTransactionIncomeFragment(private val source:String) : BaseFragment() {
    private var binding: FragmentCardTransactionBinding? = null
    private var incomeCardTransactionAdapter: IncomeCardTransactionAdapter? = null
    private var analyticsList: ArrayList<DataItem> = ArrayList()
    private var touchListener: RecyclerTouchListener? = null

    interface onIncomeEventListener {
        fun incomeValue(s: String?)
    }

    var incomeEventListener: onIncomeEventListener? = null

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        try {
            incomeEventListener = activity as onIncomeEventListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$activity must implement onIncomeEventListener")
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentCardTransactionBinding.inflate(inflater, container, false)

        binding!!.txtTryAgain.setOnClickListener { //Todo:: Get All Expense Category
            SpinnerAnalyticsListAPICall()
        }

        /*For Swipe Recycler view to show an option*/
        touchListener = RecyclerTouchListener(activity, binding!!.recyclerview)
        touchListener!!.setClickable(object : RecyclerTouchListener.OnRowClickListener {
            override fun onRowClicked(position: Int) {
            }

            override fun onIndependentViewClicked(independentViewID: Int, position: Int) {}
        })
            .setSwipeOptionViews(R.id.delete_task, R.id.edit_task)
            .setSwipeable(R.id.rowFG, R.id.rowBG) { viewID, position ->
                if (!analyticsList[position].category.equals("Opening", ignoreCase = true)) {
                    when (viewID) {
                        R.id.delete_task -> if (analyticsList[position].type.equals("income", ignoreCase = true)) {
                            dialogDelete(analyticsList[position].id.toString())
                        }
                        R.id.edit_task -> {
                            AppUtils.clearUpdateConstant()
                            RestConstant.UCLICK_ID = analyticsList[position].id.toString()
                            RestConstant.UICDate = analyticsList[position].updatedAt.toString()
                            RestConstant.UICCategory = analyticsList[position].category.toString()
                            RestConstant.UICMoneyIn = analyticsList[position].bankId.toString()
                            val incomeAmount = analyticsList[position].amount.toString()
                            RestConstant.UICNote = analyticsList[position].title.toString()
                            RestConstant.UICTextDate = analyticsList[position].date.toString()
                            RestConstant.UICIMAGEDATA = analyticsList[position].image.toString()
                            RestConstant.UPDATE_ITEM = "UpdateIncome"
                            val i1 = Intent(activity, AddIncomeExpenseActivity::class.java)
                            i1.putExtra("Select", "UpdateIncome")
                            i1.putExtra("incomeAmount", incomeAmount)
                            i1.putExtra("id", analyticsList[position].id.toString())
                            startActivity(i1)
                            Animatoo.animateSlideUp(activity)
                        }
                    }
                } else {
                    showSnackBar(activity, "This is opening card and this card is not editable")
                }
            }
        binding!!.recyclerview.addOnItemTouchListener(touchListener!!)

        //Todo:: FOR PASS DATA BETWEEN TWO FRAGMENTS
        try {
            // Register receiver
            LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
                receiver,
                IntentFilter("Intent")
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

        SpinnerAnalyticsListAPICall()

        return binding!!.root
    }

    /*FOR PASS DATA BETWEEN TWO FRAGMENTS*/
    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val message = intent.getStringExtra("API")
            if (message.equals("INCOME", ignoreCase = true)) {
                //Todo:: Get All Expense Category
                SpinnerAnalyticsListAPICall()
            }
        }
    }

    /**
     * ANALYTICS LIST API CALLING
     */
    private fun SpinnerAnalyticsListAPICall() {
        try {
            if (AppUtils.isConnectedToInternet(activity)) {
                showProgressDialog(activity)
                val params = HashMap<String?, String?>()
                params["month"] = RestConstant.SELECTED_MONTH_ID
                params["source"] = ""
                params["pay_method"] = source
                params["type"] = "income"
                params["start_date"] = ""
                params["end_date"] = ""
                params["date"] = RestConstant.DATE
                params["year"] = RestConstant.SELECTED_YEAR
                val call: Call<AnalyticsResponse> =
                    RetrofitRestClient.instance?.getAnalyticsListAPICall(
                        params,
                        MySharedPreferences.getMySharedPreferences()?.accessToken
                    ) ?: return

               // Log.e("-->IncomeParameter",params.toString())
                call.enqueue(object : Callback<AnalyticsResponse?> {
                    override fun onResponse(
                        call: Call<AnalyticsResponse?>,
                        response: Response<AnalyticsResponse?>
                    ) {
                        hideProgressDialog()
                        val analyticsResponse: AnalyticsResponse?
                        if (response.isSuccessful) {
                            analyticsResponse = response.body()
                            if (analyticsResponse?.code == 200) {
                                analyticsList.clear()
                                analyticsList.addAll(analyticsResponse.data!!)

                                if (analyticsResponse.incomeAmount.toString()
                                        .equals("0.0", ignoreCase = true)
                                ) {
                                    incomeEventListener?.incomeValue( MySharedPreferences.getMySharedPreferences()?.currency
                                        .toString() + "0.00")
                                } else {
                                    incomeEventListener?.incomeValue( MySharedPreferences.getMySharedPreferences()?.currency
                                        .toString() + AppUtils.doubleToStringNoDecimal(
                                        analyticsResponse.incomeAmount.toString()))
                                }

                                binding!!.recyclerview.layoutManager = LinearLayoutManager(
                                    activity,
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                                incomeCardTransactionAdapter = activity?.let {
                                    IncomeCardTransactionAdapter(
                                        it,
                                        analyticsList,
                                        this@CardTransactionIncomeFragment
                                    )
                                }
                                binding!!.recyclerview.adapter = incomeCardTransactionAdapter
                                binding!!.recyclerview.visibility = View.VISIBLE
                                binding!!.txtNoDataFound.visibility = View.GONE
                                binding!!.txtTryAgain.visibility = View.GONE
                            } else if (analyticsResponse?.code == 999) {
                                logout(activity)
                            } else if (analyticsResponse?.code == 404) {
                                incomeEventListener?.incomeValue( MySharedPreferences.getMySharedPreferences()?.currency
                                    .toString() + "0.00")
                                binding!!.recyclerview.visibility = View.GONE
                                binding!!.txtNoDataFound.text = analyticsResponse.message
                                binding!!.txtNoDataFound.visibility = View.VISIBLE
                                binding!!.txtTryAgain.visibility = View.GONE
                            } else {
                                binding!!.recyclerview.visibility = View.GONE
                                binding!!.txtNoDataFound.text = analyticsResponse!!.message
                                binding!!.txtNoDataFound.visibility = View.VISIBLE
                                binding!!.txtTryAgain.visibility = View.VISIBLE
                            }
                        } else {
                            binding!!.recyclerview.visibility = View.GONE
                            binding!!.txtNoDataFound.text = response.message()
                            binding!!.txtNoDataFound.visibility = View.VISIBLE
                            binding!!.txtTryAgain.visibility = View.VISIBLE
                        }
                    }

                    override fun onFailure(call: Call<AnalyticsResponse?>, t: Throwable) {
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

    fun transactionListClick(position: Int) {
        if (!analyticsList[position].category.equals("Opening", ignoreCase = true)) {
            AppUtils.clearUpdateConstant()
            RestConstant.UCLICK_ID = analyticsList[position].id.toString()
            RestConstant.UICDate = analyticsList[position].updatedAt.toString()
            RestConstant.UICCategory = analyticsList[position].category.toString()
            RestConstant.UICMoneyIn = analyticsList[position].bankId.toString()
            val incomeAmount = analyticsList[position].amount.toString()
            RestConstant.UICNote = analyticsList[position].title.toString()
            RestConstant.UICTextDate = analyticsList[position].date.toString()
            RestConstant.UICIMAGEDATA = analyticsList[position].image.toString()
            RestConstant.UPDATE_ITEM = "UpdateIncome"
            val i1 = Intent(activity, AddIncomeExpenseActivity::class.java)
            i1.putExtra("Select", "UpdateIncome")
            i1.putExtra("incomeAmount", incomeAmount)
            i1.putExtra("id", analyticsList[position].id.toString())
            startActivity(i1)
            Animatoo.animateSlideUp(activity)
        } else {
            showSnackBar(activity, "This is opening card and this card is not editable")
        }
    }

    /**
     * Dialog Logout
     */
    private fun dialogDelete(Id: String) {
        AlertDialog.Builder(activity)
            .setMessage(getString(R.string.are_you_delete))
            .setPositiveButton(getString(R.string.yes)) { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
                deleteIncomeAPICall(Id)
            }
            .setNegativeButton(getString(R.string.no)) { dialogInterface: DialogInterface, i: Int -> dialogInterface.dismiss() }
            .show()
    }

    /**
     * DELETE INCOME API CALLING
     */
    private fun deleteIncomeAPICall(Id: String) {
        try {
            if (AppUtils.isConnectedToInternet(activity)) {
                showProgressDialog(activity)
                val params = HashMap<String?, String?>()
                params["id"] = Id
                val call: Call<BasicModelResponse>?
                call = RetrofitRestClient.instance?.deleteIncomeAPICall(
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
                            if (basicModelResponse?.code == 200) {
                                SpinnerAnalyticsListAPICall()
                            } else if (basicModelResponse?.code == 999) {
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