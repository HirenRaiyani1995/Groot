package com.groot.app.iec.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.groot.app.iec.R
import com.groot.app.iec.activity.AddIncomeExpenseActivity
import com.groot.app.iec.adapter.ExpenseCardTransactionAdapter
import com.groot.app.iec.adapter.ExpenseCategoryAnalyticsAdapter
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
import java.lang.ClassCastException
import java.lang.Exception
import java.util.*

class CardTransactionExpenseFragment(private val source:String) : BaseFragment() {
    private var binding: FragmentCardTransactionBinding? = null
    private var expenseCardTransactionAdapter: ExpenseCardTransactionAdapter? = null
    private var analyticsList: ArrayList<DataItem>? = ArrayList()
    private var touchListener: RecyclerTouchListener? = null

    interface onExpenseEventListener {
        fun expenseValue(s: String?)
    }

    var expenseEventListener: onExpenseEventListener? = null

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        try {
            expenseEventListener = activity as onExpenseEventListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$activity must implement onExpenseEventListener")
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
            //Todo:: Get All Expense Transaction List
            SpinnerAnalyticsListAPICall()
        }

        /*For Swipe Recycler view to show an option*/
        touchListener = RecyclerTouchListener(activity, binding!!.recyclerview)
        touchListener!!
            .setClickable(object : RecyclerTouchListener.OnRowClickListener {
                override fun onRowClicked(position: Int) {
                    //Toast.makeText(getActivity(), String.valueOf(position), Toast.LENGTH_SHORT).show();
                }

                override fun onIndependentViewClicked(independentViewID: Int, position: Int) {}
            })
            .setSwipeOptionViews(R.id.delete_task, R.id.edit_task)
            .setSwipeable(R.id.rowFG, R.id.rowBG) { viewID, position ->
                when (viewID) {
                    R.id.delete_task -> dialogDelete(analyticsList!![position].id.toString())
                    R.id.edit_task -> {
                        AppUtils.clearUpdateConstant()
                        RestConstant.UCLICK_ID = analyticsList!![position].id.toString()
                        RestConstant.UICDate = analyticsList!![position].updatedAt.toString()
                        RestConstant.UICCategory = analyticsList!![position].category.toString()
                        RestConstant.UICMoneyIn = analyticsList!![position].bankId.toString()
                        val incomeAmount = analyticsList!![position].amount.toString()
                        RestConstant.UICNote = analyticsList!![position].title.toString()
                        RestConstant.UICTextDate = analyticsList!![position].date.toString()
                        RestConstant.UICIMAGEDATA = analyticsList!![position].image.toString()
                        RestConstant.UPDATE_ITEM = "UpdateExpense"
                        val i1 = Intent(activity, AddIncomeExpenseActivity::class.java)
                        i1.putExtra("Select", "UpdateExpense")
                        i1.putExtra("incomeAmount", incomeAmount)
                        i1.putExtra("id", analyticsList!![position].id.toString())
                        startActivity(i1)
                        Animatoo.animateSlideUp(activity)
                    }
                }
            }
        binding!!.recyclerview.addOnItemTouchListener(touchListener!!)

        //Todo:: Get All Expense Transaction List
        SpinnerAnalyticsListAPICall()

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
        return binding!!.root
    }

    /*FOR PASS DATA BETWEEN TWO FRAGMENTS*/
    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val message = intent.getStringExtra("API")
            if (message.equals("EXPENSE", ignoreCase = true)) {
                //Todo:: Get All Expense Category
                SpinnerAnalyticsListAPICall()
            }
        }
    }

    fun transactionListClick(position: Int) {
        AppUtils.clearUpdateConstant()
        RestConstant.UCLICK_ID = analyticsList!![position].id.toString()
        RestConstant.UICDate = analyticsList!![position].updatedAt.toString()
        RestConstant.UICCategory = analyticsList!![position].category.toString()
        RestConstant.UICMoneyIn = analyticsList!![position].bankId.toString()
        val incomeAmount = analyticsList!![position].amount.toString()
        RestConstant.UICNote = analyticsList!![position].title.toString()
        RestConstant.UICTextDate = analyticsList!![position].date.toString()
        RestConstant.UICIMAGEDATA = analyticsList!![position].image.toString()
        RestConstant.UPDATE_ITEM = "UpdateExpense"
        val i1 = Intent(activity, AddIncomeExpenseActivity::class.java)
        i1.putExtra("Select", "UpdateExpense")
        i1.putExtra("incomeAmount", incomeAmount)
        i1.putExtra("id", analyticsList!![position].id.toString())
        startActivity(i1)
        Animatoo.animateSlideUp(activity)
    }

    // ANALYTICS LIST API CALLING
    private fun SpinnerAnalyticsListAPICall() {
        try {
            if (AppUtils.isConnectedToInternet(activity)) {
                showProgressDialog(activity)
                val params = HashMap<String?, String?>()
                params["month"] = RestConstant.SELECTED_MONTH_ID
                params["source"] = ""
                params["pay_method"] = source
                params["type"] = "expense"
                params["start_date"] = ""
                params["end_date"] = ""
                params["date"] = RestConstant.DATE
                params["year"] = RestConstant.SELECTED_YEAR
                val call: Call<AnalyticsResponse>? = RetrofitRestClient.instance?.getAnalyticsListAPICall(
                    params,
                    MySharedPreferences.getMySharedPreferences()?.accessToken
                )

                //Log.e("-->ExpenseParameter",params.toString())
                if (call == null) return
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
                                analyticsList!!.clear()
                                analyticsList!!.addAll(analyticsResponse.data!!)

                                if (analyticsResponse.expenseAmount.toString()
                                        .equals("0.0", ignoreCase = true)
                                ) {
                                    expenseEventListener?.expenseValue( MySharedPreferences.getMySharedPreferences()?.currency
                                        .toString() + "0.00")
                                } else {
                                    expenseEventListener?.expenseValue(MySharedPreferences.getMySharedPreferences()?.currency
                                        .toString() + AppUtils.doubleToStringNoDecimal(
                                        analyticsResponse.expenseAmount.toString()))
                                }

                                binding!!.recyclerview.layoutManager = LinearLayoutManager(
                                    activity,
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                                expenseCardTransactionAdapter = activity?.let {
                                    ExpenseCardTransactionAdapter(
                                        it,
                                        analyticsList,
                                        this@CardTransactionExpenseFragment
                                    )
                                }
                                binding!!.recyclerview.adapter = expenseCardTransactionAdapter
                                binding!!.recyclerview.visibility = View.VISIBLE
                                binding!!.txtNoDataFound.visibility = View.GONE
                                binding!!.txtTryAgain.visibility = View.GONE
                            } else if (analyticsResponse?.code == 999) {
                                logout(activity)
                            } else if (analyticsResponse?.code == 404) {
                                //  binding.progressBar.setProgress(0);
                                expenseEventListener?.expenseValue( MySharedPreferences.getMySharedPreferences()?.currency
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

    /**
     * Dialog Logout
     */
    private fun dialogDelete(Id: String) {
        AlertDialog.Builder(activity)
            .setMessage(getString(R.string.are_you_delete))
            .setPositiveButton(getString(R.string.yes)) { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
                deleteExpenseAPICall(Id)
            }
            .setNegativeButton(getString(R.string.no)) { dialogInterface: DialogInterface, i: Int -> dialogInterface.dismiss() }
            .show()
    }

    /**
     * DELETE EXPENSE API CALLING
     */
    private fun deleteExpenseAPICall(Id: String) {
        try {
            if (AppUtils.isConnectedToInternet(activity)) {
                showProgressDialog(activity)
                val params = HashMap<String?, String?>()
                params["id"] = Id
                val call: Call<BasicModelResponse>? = RetrofitRestClient.instance?.deleteExpenseAPICall(
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