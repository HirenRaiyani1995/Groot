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
import com.groot.app.iec.adapter.IncomeCategoryCardAdapter
import com.groot.app.iec.adapter.PieChartColorListAdapter
import com.groot.app.iec.databinding.FragmentPieChartIncomeBinding
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

class PieChartIncomeFragment : BaseFragment() {
    private var binding: FragmentPieChartIncomeBinding? = null
    private var pieChartColorListAdapter: PieChartColorListAdapter? = null
    private var incomeCategoryCardAdapter: IncomeCategoryCardAdapter? = null
    private var incomeCategoryList: ArrayList<DataItem>? = null
    lateinit var multicolor: IntArray

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentPieChartIncomeBinding.inflate(inflater, container, false)
        incomeCategoryList = ArrayList()
        multicolor = requireContext().resources.getIntArray(R.array.multicolor)

        //Todo:: Get All Income Category
        incomeCategoryListAPICall()

        binding?.apply {
            slimChart.strokeWidth = 37
            slimChart.setRoundEdges(false)
            txtTryAgain.setOnClickListener { //Todo:: Get All Income Category
                incomeCategoryListAPICall()
            }
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
            if (message.equals("INCOME", ignoreCase = true)) {
                //Todo:: Get All Expense Category
                incomeCategoryListAPICall()
            }
        }
    }

    //Todo:: INCOME CATEGORY LIST API CALLING
    private fun incomeCategoryListAPICall() {
        try {
            if (isConnectedToInternet(activity)) {
                val params = HashMap<String?, String?>()
                params["month"] = RestConstant.SELECTED_MONTH_ID
                params["type"] = "income"
                params["start_date"] = ""
                params["end_date"] = ""
                params["date"] = RestConstant.DATE
                params["year"] = RestConstant.SELECTED_YEAR
                val call: Call<PieChartResponse> = instance!!.getPieChartAnalyticsListAPICall(
                    params,
                    MySharedPreferences.getMySharedPreferences()?.accessToken
                )
                call.enqueue(object : Callback<PieChartResponse?> {
                    override fun onResponse(
                        call: Call<PieChartResponse?>,
                        response: Response<PieChartResponse?>
                    ) {
                        val pieChartResponse: PieChartResponse?
                        if (response.isSuccessful) {
                            pieChartResponse = response.body()
                            if (pieChartResponse?.code == 200) {
                                incomeCategoryList!!.clear()
                                incomeCategoryList!!.addAll(pieChartResponse.data!!)
                                binding!!.tvTotalIncome.text = "Income\n"+
                                        MySharedPreferences.getMySharedPreferences()?.currency.toString().trimIndent() + doubleToStringNoDecimal(
                                    pieChartResponse.totalIncomeAmount.toString()
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
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                Collections.sort(incomeCategoryList, DataItem.PercentageComparator)
                                if (incomeCategoryList != null && incomeCategoryList!!.size > 0) {
                                    binding!!.recyclerviewIncome.layoutManager =
                                        LinearLayoutManager(
                                            activity
                                        )
                                    try {
                                        incomeCategoryCardAdapter = IncomeCategoryCardAdapter(
                                            activity,
                                            incomeCategoryList!!,
                                            this@PieChartIncomeFragment
                                        )
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    binding!!.recyclerviewIncome.adapter = incomeCategoryCardAdapter
                                    binding!!.recyclerviePieChartColor.layoutManager =
                                        GridLayoutManager(activity, 2)
                                    try {
                                        pieChartColorListAdapter = PieChartColorListAdapter(
                                            activity,
                                            incomeCategoryList!!
                                        )
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    binding!!.recyclerviePieChartColor.adapter =
                                        pieChartColorListAdapter

                                    //Optional - create colors array
                                    val colors = IntArray(
                                        incomeCategoryList!!.size + 1
                                    )
                                    colors[0] = Color.parseColor("#080b27")
                                    try {
                                        for (c in incomeCategoryList!!.indices) {
                                            colors[c + 1] = multicolor[c]
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    binding!!.slimChart.colors = colors

                                    //Create array for your stats
                                    val stats = FloatArray(
                                        incomeCategoryList!!.size + 1
                                    )
                                    stats[0] = 100f
                                    stats[1] = 100f
                                    var TotalPercentage = 100
                                    if (incomeCategoryList!!.size > 1) {
                                        for (s in 1 until incomeCategoryList!!.size) {
                                            stats[s + 1] =
                                                (TotalPercentage - incomeCategoryList!![s - 1].percentage.toInt()).toFloat()
                                            TotalPercentage =
                                                TotalPercentage - incomeCategoryList!![s - 1].percentage.toInt()
                                        }
                                    }
                                    binding!!.slimChart.stats = stats

                                    //Play animation
                                    binding!!.slimChart.setStartAnimationDuration(2000)
                                }
                                binding!!.mainLayout.visibility = View.VISIBLE
                                binding!!.txtTryAgain.visibility = View.GONE
                                binding!!.txtNoDataFound.visibility = View.GONE
                            } else if (pieChartResponse?.code == 999) {
                                logout(activity)
                            } else if (pieChartResponse?.code == 404) {
                                try {
                                    val i1 = Intent("DataIntent")
                                    i1.putExtra("IncomeAmount", "0.0")
                                    LocalBroadcastManager.getInstance(requireContext())
                                        .sendBroadcast(i1)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                binding!!.txtNoDataFound.text = pieChartResponse.message
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