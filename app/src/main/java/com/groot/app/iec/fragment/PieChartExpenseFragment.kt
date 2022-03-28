package com.groot.app.iec.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.groot.app.iec.R
import com.groot.app.iec.adapter.ExpenseCategoryCardAdapter
import com.groot.app.iec.adapter.PieChartColorListAdapter
import com.groot.app.iec.databinding.FragmentPieChartExpenseBinding
import com.groot.app.iec.model.pie_chart.DataItem
import com.groot.app.iec.model.pie_chart.PieChartResponse
import com.groot.app.iec.rest.RestConstant
import com.groot.app.iec.rest.RetrofitRestClient.instance
import com.groot.app.iec.utils.AppUtils.doubleToStringNoDecimal
import com.groot.app.iec.utils.AppUtils.isConnectedToInternet
import com.groot.app.iec.utils.BaseFragment
import com.groot.app.iec.utils.MySharedPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class PieChartExpenseFragment : BaseFragment() {
    private var binding: FragmentPieChartExpenseBinding? = null
    private var pieChartColorListAdapter: PieChartColorListAdapter? = null
    private var expenseCategoryCardAdapter: ExpenseCategoryCardAdapter? = null
    private var expenseCategoryList: ArrayList<DataItem>? = null
    lateinit var multicolor: IntArray

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentPieChartExpenseBinding.inflate(inflater, container, false)
        expenseCategoryList = ArrayList()
        multicolor = requireContext().resources.getIntArray(R.array.multicolor)

        //Todo:: Get All Expense Category
        expenseCategoryListAPICall()
        binding!!.slimChart.strokeWidth = 37
        binding!!.slimChart.setRoundEdges(false)
        binding!!.txtTryAgain.setOnClickListener { //Todo:: Get All Expense Category
            expenseCategoryListAPICall()
        }

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
                expenseCategoryListAPICall()
            }
        }
    }

    /**
     * Expense CATEGORY LIST API CALLING
     */
    private fun expenseCategoryListAPICall() {
        try {
            if (isConnectedToInternet(activity)) {
                // showProgressDialog(getActivity());
                val params = HashMap<String?, String?>()
                params["month"] = RestConstant.SELECTED_MONTH_ID
                params["type"] = "expense"
                params["start_date"] = ""
                params["end_date"] = ""
                params["date"] = RestConstant.DATE
                params["year"] = RestConstant.SELECTED_YEAR
                val call: Call<PieChartResponse>
                call = instance!!.getPieChartAnalyticsListAPICall(
                    params,
                    MySharedPreferences.getMySharedPreferences()?.accessToken
                )
                if (call == null) return
                call.enqueue(object : Callback<PieChartResponse?> {
                    override fun onResponse(
                        call: Call<PieChartResponse?>,
                        response: Response<PieChartResponse?>
                    ) {
                        hideProgressDialog()
                        val pieChartResponse: PieChartResponse?
                        if (response.isSuccessful) {
                            pieChartResponse = response.body()
                            if (Objects.requireNonNull(pieChartResponse)?.code == 200) {
                                expenseCategoryList!!.clear()
                                expenseCategoryList!!.addAll(pieChartResponse!!.data!!)
                                binding!!.tvTotalExpense.text = "Expense\n" +
                                        MySharedPreferences.getMySharedPreferences()?.currency.toString().trimIndent() + doubleToStringNoDecimal(
                                    pieChartResponse.totalExpenseAmount.toString()
                                )
                                try {
                                    val i1 = Intent("DataIntent")
                                    i1.putExtra(
                                        "IncomeAmount",
                                        pieChartResponse.totalIncomeAmount.toString()
                                    )
                                    i1.putExtra(
                                        "ExpenseAmount",
                                        pieChartResponse.totalExpenseAmount.toString()
                                    )
                                    LocalBroadcastManager.getInstance(requireContext())
                                        .sendBroadcast(i1)
                                }catch (e: Exception){
                                    e.printStackTrace()
                                }
                                Collections.sort(expenseCategoryList, DataItem.PercentageComparator)
                                if (expenseCategoryList != null && expenseCategoryList!!.size > 0) {
                                    binding!!.recyclerviewExpense.layoutManager =
                                        LinearLayoutManager(
                                            activity
                                        )
                                    expenseCategoryCardAdapter = ExpenseCategoryCardAdapter(
                                        activity,
                                        expenseCategoryList!!,
                                        this@PieChartExpenseFragment
                                    )
                                    binding!!.recyclerviewExpense.adapter =
                                        expenseCategoryCardAdapter
                                    binding!!.recyclerviePieChartColor.layoutManager =
                                        GridLayoutManager(
                                            activity, 2
                                        )
                                    pieChartColorListAdapter =
                                        PieChartColorListAdapter(activity, expenseCategoryList!!)
                                    binding!!.recyclerviePieChartColor.adapter =
                                        pieChartColorListAdapter

                                    //Optional - create colors array
                                    val colors = IntArray(
                                        expenseCategoryList!!.size + 1
                                    )
                                    colors[0] = Color.parseColor("#080b27")
                                    try {
                                        for (c in expenseCategoryList!!.indices) {
                                            colors[c + 1] = multicolor[c]
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    binding!!.slimChart.colors = colors

                                    //Create array for your stats
                                    val stats = FloatArray(
                                        expenseCategoryList!!.size + 1
                                    )
                                    stats[0] = 100f
                                    stats[1] = 100f
                                    var TotalPercentage = 100
                                    if (expenseCategoryList!!.size > 1) {
                                        for (s in 1 until expenseCategoryList!!.size) {
                                            stats[s + 1] =
                                                (TotalPercentage - expenseCategoryList!![s - 1].percentage.toInt()).toFloat()
                                            TotalPercentage =
                                                TotalPercentage - expenseCategoryList!![s - 1].percentage.toInt()
                                        }
                                    }
                                    binding!!.slimChart.stats = stats

                                    //Play animation
                                    binding!!.slimChart.setStartAnimationDuration(2000)
                                }
                                binding!!.mainLayout.visibility = View.VISIBLE
                                binding!!.txtTryAgain.visibility = View.GONE
                                binding!!.txtNoDataFound.visibility = View.GONE
                            } else if (Objects.requireNonNull(pieChartResponse)?.code == 999) {
                                logout(activity)
                            } else if (Objects.requireNonNull(pieChartResponse)?.code == 404) {
                                try {
                                    val i1 = Intent("DataIntent")
                                    i1.putExtra("ExpenseAmount", "0.0")
                                    LocalBroadcastManager.getInstance(requireContext())
                                        .sendBroadcast(i1)
                                }catch (e: Exception){
                                    e.printStackTrace()
                                }
                                binding!!.txtNoDataFound.text = pieChartResponse!!.message
                                binding!!.mainLayout.visibility = View.GONE
                                binding!!.txtTryAgain.visibility = View.GONE
                                binding!!.txtNoDataFound.visibility = View.VISIBLE
                            } else {
                                binding!!.txtNoDataFound.text = pieChartResponse!!.message
                                binding!!.mainLayout.visibility = View.GONE
                                binding!!.txtTryAgain.visibility = View.VISIBLE
                                binding!!.txtNoDataFound.visibility = View.VISIBLE
                            }
                        } else {
                            binding!!.txtNoDataFound.text = response.message()
                            binding!!.mainLayout.visibility = View.GONE
                            binding!!.txtTryAgain.visibility = View.VISIBLE
                            binding!!.txtNoDataFound.visibility = View.VISIBLE
                        }
                    }

                    override fun onFailure(call: Call<PieChartResponse?>, t: Throwable) {
                        onFailureCallNew(
                            activity,
                            t,
                            binding!!.txtNoDataFound,
                            binding!!.txtTryAgain
                        )
                    }
                })
            } else {
                binding!!.txtNoDataFound.text = getString(R.string.no_internet)
                binding!!.mainLayout.visibility = View.GONE
                binding!!.txtTryAgain.visibility = View.VISIBLE
                binding!!.txtNoDataFound.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}