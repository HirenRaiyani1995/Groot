package com.groot.app.iec.activity

import com.groot.app.iec.utils.AppUtils.doubleToStringNoDecimal
import com.groot.app.iec.rest.RestConstant
import android.os.Bundle
import com.groot.app.iec.utils.MySharedPreferences
import com.groot.app.iec.R
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.content.IntentFilter
import android.content.BroadcastReceiver
import android.content.Context
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.widget.AppCompatTextView
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import androidx.viewpager.widget.ViewPager
import com.groot.app.iec.fragment.PieChartIncomeFragment
import com.groot.app.iec.fragment.PieChartExpenseFragment
import androidx.fragment.app.FragmentPagerAdapter
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.groot.app.iec.adapter.SelectFilterAdapter
import com.groot.app.iec.databinding.ActivityPieChartBinding
import com.groot.app.iec.model.FilterListModel
import com.groot.app.iec.utils.AppUtils
import com.groot.app.iec.utils.BaseActivity
import java.lang.Exception
import java.util.*

class PieChartActivity : BaseActivity(), View.OnClickListener {
    private var binding: ActivityPieChartBinding? = null
    private var dialogSelectMonth: View? = null
    private var SelectMonthNew = RestConstant.SELECTED_MONTH
    private var MONTH_ID = RestConstant.SELECTED_MONTH_ID
    private var filterSelectedOption = ""
    private var lastSavedFilter = "Month"
    private var selectedYear = ""
    private val allList: ArrayList<FilterListModel> = ArrayList<FilterListModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        super.onCreate(savedInstanceState)
        binding = ActivityPieChartBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        dialogSelectMonth = findViewById(R.id.dialogSelectMonth)

        // loading Video Ad
        loadRewardedVideoAd(this)

        showBannerAds(this,binding!!.adLayout)

        /*Spinner Language Adapter Set*/
        binding?.dialogSelectMonth?.txtdropdown?.adapter = SelectFilterAdapter(
            this@PieChartActivity,
            R.layout.spinner_row,
            getAllList()
        )

        binding?.apply {
            tvIncomeAmount.setText(
                MySharedPreferences.getMySharedPreferences()?.currency.toString() + "0"
            )

            tvExpenseAmount.setText(
                MySharedPreferences.getMySharedPreferences()?.currency.toString() + "0"
            )

            btnIncome.setBackgroundResource(R.color.facebook)
            btnExpense.setBackgroundResource(R.color.dark_blue)
            btnBack.setOnClickListener(this@PieChartActivity)
            btnIncome.setOnClickListener(this@PieChartActivity)
            btnExpense.setOnClickListener(this@PieChartActivity)
            btnFilter.setOnClickListener(this@PieChartActivity)

            dialogSelectMonth.btnJanuary.setOnClickListener(this@PieChartActivity)
            dialogSelectMonth.btnFebruary.setOnClickListener(this@PieChartActivity)
            dialogSelectMonth.btnMarch.setOnClickListener(this@PieChartActivity)
            dialogSelectMonth.btnApril.setOnClickListener(this@PieChartActivity)
            dialogSelectMonth.btnMay.setOnClickListener(this@PieChartActivity)
            dialogSelectMonth.btnJune.setOnClickListener(this@PieChartActivity)
            dialogSelectMonth.btnJuly.setOnClickListener(this@PieChartActivity)
            dialogSelectMonth.btnAugust.setOnClickListener(this@PieChartActivity)
            dialogSelectMonth.btnSeptember.setOnClickListener(this@PieChartActivity)
            dialogSelectMonth.btnOctober.setOnClickListener(this@PieChartActivity)
            dialogSelectMonth.btnNovember.setOnClickListener(this@PieChartActivity)
            dialogSelectMonth.btnDecember.setOnClickListener(this@PieChartActivity)
            dialogSelectMonth.btnAll.setOnClickListener(this@PieChartActivity)
            dialogSelectMonth.btnTodaySave.setOnClickListener(this@PieChartActivity)

            dialogSelectMonth.btn2021.setOnClickListener(this@PieChartActivity)
            dialogSelectMonth.btn2022.setOnClickListener(this@PieChartActivity)
            dialogSelectMonth.btn2023.setOnClickListener(this@PieChartActivity)
            dialogSelectMonth.btn2024.setOnClickListener(this@PieChartActivity)
            dialogSelectMonth.btn2025.setOnClickListener(this@PieChartActivity)
            dialogSelectMonth.btn2026.setOnClickListener(this@PieChartActivity)
            dialogSelectMonth.btn2027.setOnClickListener(this@PieChartActivity)
            dialogSelectMonth.btn2028.setOnClickListener(this@PieChartActivity)
            dialogSelectMonth.btn2029.setOnClickListener(this@PieChartActivity)
            dialogSelectMonth.btn2030.setOnClickListener(this@PieChartActivity)
            dialogSelectMonth.btn2031.setOnClickListener(this@PieChartActivity)
            dialogSelectMonth.btn2032.setOnClickListener(this@PieChartActivity)
            dialogSelectMonth.btn2033.setOnClickListener(this@PieChartActivity)
            dialogSelectMonth.btnYearSave.setOnClickListener(this@PieChartActivity)

            selectCurrentMonthInDialog()

            selectCurrentYearInDialog()

            setupViewPager(binding!!.viewPager)

            if (RestConstant.SELECTED_MONTH.equals("", ignoreCase = true)) {
                RestConstant.SELECTED_MONTH_ID = AppUtils.getCurrentMonth().toString()
                RestConstant.SELECTED_YEAR = AppUtils.getCurrentYear().toString()
                headerTitle.text = AppUtils.getCurrentMonthName()
                headerTitleYear.text = AppUtils.getCurrentYear().toString()
                selectedYear = AppUtils.getCurrentYear().toString()
            } else {
                headerTitle.text = RestConstant.SELECTED_MONTH
                headerTitleYear.text = AppUtils.getCurrentYear().toString()
            }

            //Todo:: Save Month
            dialogSelectMonth.btnSave.setOnClickListener(View.OnClickListener {
                lastSavedFilter = "Month"
                RestConstant.SELECTED_MONTH = SelectMonthNew
                RestConstant.SELECTED_MONTH_ID = MONTH_ID
                RestConstant.SELECTED_YEAR = ""
                RestConstant.DATE = ""
                dialogSelectMonth.monthLayout.visibility = View.GONE
                if (RestConstant.SELECTED_MONTH.equals("", ignoreCase = true)) {
                    headerTitle.text = "All"
                    headerTitleYear.text = ""
                } else {
                    headerTitle.text = RestConstant.SELECTED_MONTH
                    headerTitleYear.text = AppUtils.getCurrentYear().toString()
                }
                refreshFragmentData()
            })

            dialogSelectMonth.btnClose.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View) {
                    SelectMonthNew = ""
                    dialogSelectMonth.monthLayout.visibility = View.GONE
                }
            })

            dialogSelectMonth.btnYearSave.setOnClickListener {
                lastSavedFilter = "Year"
                RestConstant.SELECTED_MONTH_ID = ""
                RestConstant.DATE = ""
                RestConstant.SELECTED_YEAR = selectedYear
                headerTitle.text = ""
                headerTitleYear.text = selectedYear

                dialogSelectMonth.monthLayout.visibility = View.GONE
                refreshFragmentData()
            }

            binding?.dialogSelectMonth?.txtdropdown?.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    when (position) {
                        0 -> {
                            filterSelectedOption = "Month"
                            binding?.dialogSelectMonth?.layoutMonths?.visibility = View.VISIBLE
                            binding?.dialogSelectMonth?.layoutToday?.visibility = View.GONE
                            binding?.dialogSelectMonth?.layoutYear?.visibility = View.GONE

                            RestConstant.SELECTED_MONTH_ID = AppUtils.getCurrentMonth().toString()
                            RestConstant.SELECTED_YEAR = AppUtils.getCurrentYear().toString()
                            binding!!.headerTitle.text = AppUtils.getCurrentMonthName()
                            binding!!.headerTitleYear.text = AppUtils.getCurrentYear().toString()
                            selectedYear = AppUtils.getCurrentYear().toString()

                            selectCurrentMonthInDialog()
                        }
                        1 -> {
                            filterSelectedOption = "Today"
                            binding?.dialogSelectMonth?.layoutMonths?.visibility = View.GONE
                            binding?.dialogSelectMonth?.layoutToday?.visibility = View.VISIBLE
                            binding?.dialogSelectMonth?.layoutYear?.visibility = View.GONE
                        }
                        2 -> {
                            filterSelectedOption = "Yesterday"
                            binding?.dialogSelectMonth?.layoutMonths?.visibility = View.GONE
                            binding?.dialogSelectMonth?.layoutToday?.visibility = View.VISIBLE
                            binding?.dialogSelectMonth?.layoutYear?.visibility = View.GONE
                        }
                        3 -> {
                            filterSelectedOption = "Year"
                            binding?.dialogSelectMonth?.layoutMonths?.visibility = View.GONE
                            binding?.dialogSelectMonth?.layoutToday?.visibility = View.GONE
                            binding?.dialogSelectMonth?.layoutYear?.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            //Todo:: FOR PASS DATA BETWEEN TWO FRAGMENTS
            try {
                // Register receiver
                LocalBroadcastManager.getInstance(activity).registerReceiver(
                    receiver,
                    IntentFilter("DataIntent")
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /*FOR PASS DATA BETWEEN TWO FRAGMENTS*/
    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val IncomeAmount = intent.getStringExtra("IncomeAmount")
            val ExpenseAmount = intent.getStringExtra("ExpenseAmount")
            if (IncomeAmount != null) {
                if (IncomeAmount.equals("", ignoreCase = true)) {
                    binding!!.tvIncomeAmount.setText(
                        MySharedPreferences.getMySharedPreferences()?.currency
                            .toString() + "0.00"
                    )
                }
            }
            if (ExpenseAmount != null) {
                if (ExpenseAmount.equals("", ignoreCase = true)) {
                    binding!!.tvExpenseAmount.setText(
                        MySharedPreferences.getMySharedPreferences()?.currency
                            .toString() + "0.00"
                    )
                }
            }
            try {
                if (IncomeAmount.equals("0.0", ignoreCase = true)) {
                    binding!!.tvIncomeAmount.setText(
                        MySharedPreferences.getMySharedPreferences()?.currency
                            .toString() + "0.00"
                    )
                } else {
                    binding!!.tvIncomeAmount.setText(
                        MySharedPreferences.getMySharedPreferences()?.currency
                            .toString() + doubleToStringNoDecimal(
                            (IncomeAmount)!!
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                if (ExpenseAmount.equals("0.0", ignoreCase = true)) {
                    binding!!.tvExpenseAmount.setText(
                        MySharedPreferences.getMySharedPreferences()?.currency
                            .toString() + "0.00"
                    )
                } else {
                    binding!!.tvExpenseAmount.setText(
                        MySharedPreferences.getMySharedPreferences()?.currency
                            .toString() + doubleToStringNoDecimal(
                            (ExpenseAmount)!!
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

        private fun getAllList(): java.util.ArrayList<FilterListModel> {
        var item = FilterListModel()
        item.setData("Month")
        allList.add(item)

        item = FilterListModel()
        item.setData("Today")
        allList.add(item)

        item = FilterListModel()
        item.setData("Yesterday")
        allList.add(item)

        item = FilterListModel()
        item.setData("Year")
        allList.add(item)

        return allList
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnBack -> onBackPressed()
            R.id.btnIncome -> {
                binding!!.btnIncome.setBackgroundResource(R.color.facebook)
                binding!!.btnExpense.setBackgroundResource(R.color.dark_blue)
                binding!!.viewPager.currentItem = 0
                val i5 = Intent("Intent")
                i5.putExtra("API", "INCOME")
                LocalBroadcastManager.getInstance(activity).sendBroadcast(i5)
            }
            R.id.btnExpense -> {
                binding!!.btnIncome.setBackgroundResource(R.color.dark_blue)
                binding!!.btnExpense.setBackgroundResource(R.color.expense_orange)
                binding!!.viewPager.currentItem = 1
                val i6 = Intent("Intent")
                i6.putExtra("API", "EXPENSE")
                LocalBroadcastManager.getInstance(activity).sendBroadcast(i6)
            }

            R.id.btnFilter -> {
                when (lastSavedFilter) {
                    "Today" -> {
                        binding?.dialogSelectMonth?.layoutMonths?.visibility = View.GONE
                        binding?.dialogSelectMonth?.layoutToday?.visibility = View.VISIBLE
                        binding?.dialogSelectMonth?.layoutYear?.visibility = View.GONE
                        binding?.dialogSelectMonth?.txtdropdown?.setSelection(1,true)
                    }
                    "Yesterday" -> {
                        binding?.dialogSelectMonth?.layoutMonths?.visibility = View.GONE
                        binding?.dialogSelectMonth?.layoutToday?.visibility = View.VISIBLE
                        binding?.dialogSelectMonth?.layoutYear?.visibility = View.GONE
                        binding?.dialogSelectMonth?.txtdropdown?.setSelection(2,true)
                    }
                    "Month" -> {
                        binding?.dialogSelectMonth?.layoutMonths?.visibility = View.VISIBLE
                        binding?.dialogSelectMonth?.layoutToday?.visibility = View.GONE
                        binding?.dialogSelectMonth?.layoutYear?.visibility = View.GONE
                        binding?.dialogSelectMonth?.txtdropdown?.setSelection(0,true)
                    }
                    "Year" -> {
                        binding?.dialogSelectMonth?.layoutMonths?.visibility = View.GONE
                        binding?.dialogSelectMonth?.layoutToday?.visibility = View.GONE
                        binding?.dialogSelectMonth?.layoutYear?.visibility = View.VISIBLE
                        binding?.dialogSelectMonth?.txtdropdown?.setSelection(3,true)
                    }
                }

                binding?.dialogSelectMonth?.monthLayout?.visibility = View.VISIBLE
            }
             R.id.btnTodaySave -> {
                if (filterSelectedOption == "Today") {
                    lastSavedFilter = "Today"
                    binding?.headerTitle?.text = "Today"
                    RestConstant.SELECTED_MONTH_ID = ""
                    RestConstant.SELECTED_YEAR = ""
                    RestConstant.DATE = AppUtils.getCurrentDate() + " " + AppUtils.getCurrentTime()
                    binding?.headerTitleYear?.text = ""
                    refreshFragmentData()
                } else {
                    lastSavedFilter = "Yesterday"
                    binding?.headerTitle?.text = "Yesterday"
                    RestConstant.SELECTED_MONTH_ID = ""
                    RestConstant.SELECTED_YEAR = ""
                    RestConstant.DATE = AppUtils.getYesterDayDate()
                    binding?.headerTitleYear?.text = ""
                    refreshFragmentData()
                }
            }
            R.id.btnJanuary -> {
                SelectMonthNew = "January"
                MONTH_ID = "1"
                changeDarkColor(
                    binding!!.dialogSelectMonth.btnJanuary,
                    binding!!.dialogSelectMonth.txtJanuary
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnAll,
                    binding!!.dialogSelectMonth.txtAll
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnFebruary,
                    binding!!.dialogSelectMonth.txtFebruary
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnMarch,
                    binding!!.dialogSelectMonth.txtMarch
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnApril,
                    binding!!.dialogSelectMonth.txtApril
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnMay,
                    binding!!.dialogSelectMonth.txtMay
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJune,
                    binding!!.dialogSelectMonth.txtJune
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJuly,
                    binding!!.dialogSelectMonth.txtJuly
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnAugust,
                    binding!!.dialogSelectMonth.txtAugust
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnSeptember,
                    binding!!.dialogSelectMonth.txtSeptember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnOctober,
                    binding!!.dialogSelectMonth.txtOctober
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnNovember,
                    binding!!.dialogSelectMonth.txtNovember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnDecember,
                    binding!!.dialogSelectMonth.txtDecember
                )
            }
            R.id.btnFebruary -> {
                SelectMonthNew = "February"
                MONTH_ID = "2"
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJanuary,
                    binding!!.dialogSelectMonth.txtJanuary
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnAll,
                    binding!!.dialogSelectMonth.txtAll
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btnFebruary,
                    binding!!.dialogSelectMonth.txtFebruary
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnMarch,
                    binding!!.dialogSelectMonth.txtMarch
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnApril,
                    binding!!.dialogSelectMonth.txtApril
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnMay,
                    binding!!.dialogSelectMonth.txtMay
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJune,
                    binding!!.dialogSelectMonth.txtJune
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJuly,
                    binding!!.dialogSelectMonth.txtJuly
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnAugust,
                    binding!!.dialogSelectMonth.txtAugust
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnSeptember,
                    binding!!.dialogSelectMonth.txtSeptember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnOctober,
                    binding!!.dialogSelectMonth.txtOctober
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnNovember,
                    binding!!.dialogSelectMonth.txtNovember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnDecember,
                    binding!!.dialogSelectMonth.txtDecember
                )
            }
            R.id.btnMarch -> {
                SelectMonthNew = "March"
                MONTH_ID = "3"
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJanuary,
                    binding!!.dialogSelectMonth.txtJanuary
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnAll,
                    binding!!.dialogSelectMonth.txtAll
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnFebruary,
                    binding!!.dialogSelectMonth.txtFebruary
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btnMarch,
                    binding!!.dialogSelectMonth.txtMarch
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnApril,
                    binding!!.dialogSelectMonth.txtApril
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnMay,
                    binding!!.dialogSelectMonth.txtMay
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJune,
                    binding!!.dialogSelectMonth.txtJune
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJuly,
                    binding!!.dialogSelectMonth.txtJuly
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnAugust,
                    binding!!.dialogSelectMonth.txtAugust
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnSeptember,
                    binding!!.dialogSelectMonth.txtSeptember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnOctober,
                    binding!!.dialogSelectMonth.txtOctober
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnNovember,
                    binding!!.dialogSelectMonth.txtNovember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnDecember,
                    binding!!.dialogSelectMonth.txtDecember
                )
            }
            R.id.btnApril -> {
                SelectMonthNew = "April"
                MONTH_ID = "4"
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJanuary,
                    binding!!.dialogSelectMonth.txtJanuary
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnAll,
                    binding!!.dialogSelectMonth.txtAll
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnFebruary,
                    binding!!.dialogSelectMonth.txtFebruary
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnMarch,
                    binding!!.dialogSelectMonth.txtMarch
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btnApril,
                    binding!!.dialogSelectMonth.txtApril
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnMay,
                    binding!!.dialogSelectMonth.txtMay
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJune,
                    binding!!.dialogSelectMonth.txtJune
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJuly,
                    binding!!.dialogSelectMonth.txtJuly
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnAugust,
                    binding!!.dialogSelectMonth.txtAugust
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnSeptember,
                    binding!!.dialogSelectMonth.txtSeptember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnOctober,
                    binding!!.dialogSelectMonth.txtOctober
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnNovember,
                    binding!!.dialogSelectMonth.txtNovember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnDecember,
                    binding!!.dialogSelectMonth.txtDecember
                )
            }
            R.id.btnMay -> {
                SelectMonthNew = "May"
                MONTH_ID = "5"
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJanuary,
                    binding!!.dialogSelectMonth.txtJanuary
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnAll,
                    binding!!.dialogSelectMonth.txtAll
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnFebruary,
                    binding!!.dialogSelectMonth.txtFebruary
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnMarch,
                    binding!!.dialogSelectMonth.txtMarch
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnApril,
                    binding!!.dialogSelectMonth.txtApril
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btnMay,
                    binding!!.dialogSelectMonth.txtMay
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJune,
                    binding!!.dialogSelectMonth.txtJune
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJuly,
                    binding!!.dialogSelectMonth.txtJuly
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnAugust,
                    binding!!.dialogSelectMonth.txtAugust
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnSeptember,
                    binding!!.dialogSelectMonth.txtSeptember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnOctober,
                    binding!!.dialogSelectMonth.txtOctober
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnNovember,
                    binding!!.dialogSelectMonth.txtNovember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnDecember,
                    binding!!.dialogSelectMonth.txtDecember
                )
            }
            R.id.btnJune -> {
                SelectMonthNew = "June"
                MONTH_ID = "6"
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJanuary,
                    binding!!.dialogSelectMonth.txtJanuary
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnAll,
                    binding!!.dialogSelectMonth.txtAll
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnFebruary,
                    binding!!.dialogSelectMonth.txtFebruary
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnMarch,
                    binding!!.dialogSelectMonth.txtMarch
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnApril,
                    binding!!.dialogSelectMonth.txtApril
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnMay,
                    binding!!.dialogSelectMonth.txtMay
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btnJune,
                    binding!!.dialogSelectMonth.txtJune
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJuly,
                    binding!!.dialogSelectMonth.txtJuly
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnAugust,
                    binding!!.dialogSelectMonth.txtAugust
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnSeptember,
                    binding!!.dialogSelectMonth.txtSeptember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnOctober,
                    binding!!.dialogSelectMonth.txtOctober
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnNovember,
                    binding!!.dialogSelectMonth.txtNovember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnDecember,
                    binding!!.dialogSelectMonth.txtDecember
                )
            }
            R.id.btnJuly -> {
                SelectMonthNew = "July"
                MONTH_ID = "7"
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJanuary,
                    binding!!.dialogSelectMonth.txtJanuary
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnAll,
                    binding!!.dialogSelectMonth.txtAll
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnFebruary,
                    binding!!.dialogSelectMonth.txtFebruary
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnMarch,
                    binding!!.dialogSelectMonth.txtMarch
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnApril,
                    binding!!.dialogSelectMonth.txtApril
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnMay,
                    binding!!.dialogSelectMonth.txtMay
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJune,
                    binding!!.dialogSelectMonth.txtJune
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btnJuly,
                    binding!!.dialogSelectMonth.txtJuly
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnAugust,
                    binding!!.dialogSelectMonth.txtAugust
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnSeptember,
                    binding!!.dialogSelectMonth.txtSeptember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnOctober,
                    binding!!.dialogSelectMonth.txtOctober
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnNovember,
                    binding!!.dialogSelectMonth.txtNovember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnDecember,
                    binding!!.dialogSelectMonth.txtDecember
                )
            }
            R.id.btnAugust -> {
                SelectMonthNew = "August"
                MONTH_ID = "8"
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJanuary,
                    binding!!.dialogSelectMonth.txtJanuary
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnAll,
                    binding!!.dialogSelectMonth.txtAll
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnFebruary,
                    binding!!.dialogSelectMonth.txtFebruary
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnMarch,
                    binding!!.dialogSelectMonth.txtMarch
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnApril,
                    binding!!.dialogSelectMonth.txtApril
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnMay,
                    binding!!.dialogSelectMonth.txtMay
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJune,
                    binding!!.dialogSelectMonth.txtJune
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJuly,
                    binding!!.dialogSelectMonth.txtJuly
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btnAugust,
                    binding!!.dialogSelectMonth.txtAugust
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnSeptember,
                    binding!!.dialogSelectMonth.txtSeptember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnOctober,
                    binding!!.dialogSelectMonth.txtOctober
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnNovember,
                    binding!!.dialogSelectMonth.txtNovember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnDecember,
                    binding!!.dialogSelectMonth.txtDecember
                )
            }
            R.id.btnSeptember -> {
                SelectMonthNew = "September"
                MONTH_ID = "9"
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJanuary,
                    binding!!.dialogSelectMonth.txtJanuary
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnAll,
                    binding!!.dialogSelectMonth.txtAll
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnFebruary,
                    binding!!.dialogSelectMonth.txtFebruary
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnMarch,
                    binding!!.dialogSelectMonth.txtMarch
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnApril,
                    binding!!.dialogSelectMonth.txtApril
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnMay,
                    binding!!.dialogSelectMonth.txtMay
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJune,
                    binding!!.dialogSelectMonth.txtJune
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJuly,
                    binding!!.dialogSelectMonth.txtJuly
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnAugust,
                    binding!!.dialogSelectMonth.txtAugust
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btnSeptember,
                    binding!!.dialogSelectMonth.txtSeptember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnOctober,
                    binding!!.dialogSelectMonth.txtOctober
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnNovember,
                    binding!!.dialogSelectMonth.txtNovember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnDecember,
                    binding!!.dialogSelectMonth.txtDecember
                )
            }
            R.id.btnOctober -> {
                SelectMonthNew = "October"
                MONTH_ID = "10"
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJanuary,
                    binding!!.dialogSelectMonth.txtJanuary
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnAll,
                    binding!!.dialogSelectMonth.txtAll
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnFebruary,
                    binding!!.dialogSelectMonth.txtFebruary
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnMarch,
                    binding!!.dialogSelectMonth.txtMarch
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnApril,
                    binding!!.dialogSelectMonth.txtApril
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnMay,
                    binding!!.dialogSelectMonth.txtMay
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJune,
                    binding!!.dialogSelectMonth.txtJune
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJuly,
                    binding!!.dialogSelectMonth.txtJuly
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnAugust,
                    binding!!.dialogSelectMonth.txtAugust
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnSeptember,
                    binding!!.dialogSelectMonth.txtSeptember
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btnOctober,
                    binding!!.dialogSelectMonth.txtOctober
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnNovember,
                    binding!!.dialogSelectMonth.txtNovember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnDecember,
                    binding!!.dialogSelectMonth.txtDecember
                )
            }
            R.id.btnNovember -> {
                SelectMonthNew = "November"
                MONTH_ID = "11"
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJanuary,
                    binding!!.dialogSelectMonth.txtJanuary
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnAll,
                    binding!!.dialogSelectMonth.txtAll
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnFebruary,
                    binding!!.dialogSelectMonth.txtFebruary
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnMarch,
                    binding!!.dialogSelectMonth.txtMarch
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnApril,
                    binding!!.dialogSelectMonth.txtApril
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnMay,
                    binding!!.dialogSelectMonth.txtMay
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJune,
                    binding!!.dialogSelectMonth.txtJune
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJuly,
                    binding!!.dialogSelectMonth.txtJuly
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnAugust,
                    binding!!.dialogSelectMonth.txtAugust
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnSeptember,
                    binding!!.dialogSelectMonth.txtSeptember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnOctober,
                    binding!!.dialogSelectMonth.txtOctober
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btnNovember,
                    binding!!.dialogSelectMonth.txtNovember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnDecember,
                    binding!!.dialogSelectMonth.txtDecember
                )
            }
            R.id.btnDecember -> {
                SelectMonthNew = "December"
                MONTH_ID = "12"
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJanuary,
                    binding!!.dialogSelectMonth.txtJanuary
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnAll,
                    binding!!.dialogSelectMonth.txtAll
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnFebruary,
                    binding!!.dialogSelectMonth.txtFebruary
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnMarch,
                    binding!!.dialogSelectMonth.txtMarch
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnApril,
                    binding!!.dialogSelectMonth.txtApril
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnMay,
                    binding!!.dialogSelectMonth.txtMay
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJune,
                    binding!!.dialogSelectMonth.txtJune
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJuly,
                    binding!!.dialogSelectMonth.txtJuly
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnAugust,
                    binding!!.dialogSelectMonth.txtAugust
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnSeptember,
                    binding!!.dialogSelectMonth.txtSeptember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnOctober,
                    binding!!.dialogSelectMonth.txtOctober
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnNovember,
                    binding!!.dialogSelectMonth.txtNovember
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btnDecember,
                    binding!!.dialogSelectMonth.txtDecember
                )
            }
            R.id.btn_All -> {
                SelectMonthNew = ""
                MONTH_ID = ""
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJanuary,
                    binding!!.dialogSelectMonth.txtJanuary
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btnAll,
                    binding!!.dialogSelectMonth.txtAll
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnFebruary,
                    binding!!.dialogSelectMonth.txtFebruary
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnMarch,
                    binding!!.dialogSelectMonth.txtMarch
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnApril,
                    binding!!.dialogSelectMonth.txtApril
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnMay,
                    binding!!.dialogSelectMonth.txtMay
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJune,
                    binding!!.dialogSelectMonth.txtJune
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJuly,
                    binding!!.dialogSelectMonth.txtJuly
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnAugust,
                    binding!!.dialogSelectMonth.txtAugust
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnSeptember,
                    binding!!.dialogSelectMonth.txtSeptember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnOctober,
                    binding!!.dialogSelectMonth.txtOctober
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnNovember,
                    binding!!.dialogSelectMonth.txtNovember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnDecember,
                    binding!!.dialogSelectMonth.txtDecember
                )
            }
             R.id.btn2021 -> {
                selectedYear = "2021"
                changeDarkColor(
                    binding!!.dialogSelectMonth.btn2021,
                    binding!!.dialogSelectMonth.txt2021
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2022,
                    binding!!.dialogSelectMonth.txt2022
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2023,
                    binding!!.dialogSelectMonth.txt2023
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2024,
                    binding!!.dialogSelectMonth.txt2024
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2025,
                    binding!!.dialogSelectMonth.txt2025
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2026,
                    binding!!.dialogSelectMonth.txt2026
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2027,
                    binding!!.dialogSelectMonth.txt2027
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2028,
                    binding!!.dialogSelectMonth.txt2028
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2029,
                    binding!!.dialogSelectMonth.txt2029
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2030,
                    binding!!.dialogSelectMonth.txt2030
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2031,
                    binding!!.dialogSelectMonth.txt2031
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2032,
                    binding!!.dialogSelectMonth.txt2032
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2033,
                    binding!!.dialogSelectMonth.txt2033
                )
            }
            R.id.btn2022 -> {
                selectedYear = "2022"
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2021,
                    binding!!.dialogSelectMonth.txt2021
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btn2022,
                    binding!!.dialogSelectMonth.txt2022
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2023,
                    binding!!.dialogSelectMonth.txt2023
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2024,
                    binding!!.dialogSelectMonth.txt2024
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2025,
                    binding!!.dialogSelectMonth.txt2025
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2026,
                    binding!!.dialogSelectMonth.txt2026
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2027,
                    binding!!.dialogSelectMonth.txt2027
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2028,
                    binding!!.dialogSelectMonth.txt2028
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2029,
                    binding!!.dialogSelectMonth.txt2029
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2030,
                    binding!!.dialogSelectMonth.txt2030
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2031,
                    binding!!.dialogSelectMonth.txt2031
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2032,
                    binding!!.dialogSelectMonth.txt2032
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2033,
                    binding!!.dialogSelectMonth.txt2033
                )
            }
            R.id.btn2023 -> {
                selectedYear = "2023"
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2021,
                    binding!!.dialogSelectMonth.txt2021
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2022,
                    binding!!.dialogSelectMonth.txt2022
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btn2023,
                    binding!!.dialogSelectMonth.txt2023
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2024,
                    binding!!.dialogSelectMonth.txt2024
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2025,
                    binding!!.dialogSelectMonth.txt2025
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2026,
                    binding!!.dialogSelectMonth.txt2026
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2027,
                    binding!!.dialogSelectMonth.txt2027
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2028,
                    binding!!.dialogSelectMonth.txt2028
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2029,
                    binding!!.dialogSelectMonth.txt2029
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2030,
                    binding!!.dialogSelectMonth.txt2030
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2031,
                    binding!!.dialogSelectMonth.txt2031
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2032,
                    binding!!.dialogSelectMonth.txt2032
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2033,
                    binding!!.dialogSelectMonth.txt2033
                )
            }
            R.id.btn2024 -> {
                selectedYear = "2024"
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2021,
                    binding!!.dialogSelectMonth.txt2021
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2022,
                    binding!!.dialogSelectMonth.txt2022
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2023,
                    binding!!.dialogSelectMonth.txt2023
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btn2024,
                    binding!!.dialogSelectMonth.txt2024
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2025,
                    binding!!.dialogSelectMonth.txt2025
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2026,
                    binding!!.dialogSelectMonth.txt2026
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2027,
                    binding!!.dialogSelectMonth.txt2027
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2028,
                    binding!!.dialogSelectMonth.txt2028
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2029,
                    binding!!.dialogSelectMonth.txt2029
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2030,
                    binding!!.dialogSelectMonth.txt2030
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2031,
                    binding!!.dialogSelectMonth.txt2031
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2032,
                    binding!!.dialogSelectMonth.txt2032
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2033,
                    binding!!.dialogSelectMonth.txt2033
                )
            }
            R.id.btn2025 -> {
                selectedYear = "2025"
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2021,
                    binding!!.dialogSelectMonth.txt2021
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2022,
                    binding!!.dialogSelectMonth.txt2022
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2023,
                    binding!!.dialogSelectMonth.txt2023
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2024,
                    binding!!.dialogSelectMonth.txt2024
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btn2025,
                    binding!!.dialogSelectMonth.txt2025
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2026,
                    binding!!.dialogSelectMonth.txt2026
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2027,
                    binding!!.dialogSelectMonth.txt2027
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2028,
                    binding!!.dialogSelectMonth.txt2028
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2029,
                    binding!!.dialogSelectMonth.txt2029
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2030,
                    binding!!.dialogSelectMonth.txt2030
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2031,
                    binding!!.dialogSelectMonth.txt2031
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2032,
                    binding!!.dialogSelectMonth.txt2032
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2033,
                    binding!!.dialogSelectMonth.txt2033
                )
            }
            R.id.btn2026 -> {
                selectedYear = "2026"
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2021,
                    binding!!.dialogSelectMonth.txt2021
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2022,
                    binding!!.dialogSelectMonth.txt2022
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2023,
                    binding!!.dialogSelectMonth.txt2023
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2024,
                    binding!!.dialogSelectMonth.txt2024
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2025,
                    binding!!.dialogSelectMonth.txt2025
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btn2026,
                    binding!!.dialogSelectMonth.txt2026
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2027,
                    binding!!.dialogSelectMonth.txt2027
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2028,
                    binding!!.dialogSelectMonth.txt2028
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2029,
                    binding!!.dialogSelectMonth.txt2029
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2030,
                    binding!!.dialogSelectMonth.txt2030
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2031,
                    binding!!.dialogSelectMonth.txt2031
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2032,
                    binding!!.dialogSelectMonth.txt2032
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2033,
                    binding!!.dialogSelectMonth.txt2033
                )
            }
            R.id.btn2027 -> {
                selectedYear = "2027"
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2021,
                    binding!!.dialogSelectMonth.txt2021
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2022,
                    binding!!.dialogSelectMonth.txt2022
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2023,
                    binding!!.dialogSelectMonth.txt2023
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2024,
                    binding!!.dialogSelectMonth.txt2024
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2025,
                    binding!!.dialogSelectMonth.txt2025
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2026,
                    binding!!.dialogSelectMonth.txt2026
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btn2027,
                    binding!!.dialogSelectMonth.txt2027
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2028,
                    binding!!.dialogSelectMonth.txt2028
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2029,
                    binding!!.dialogSelectMonth.txt2029
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2030,
                    binding!!.dialogSelectMonth.txt2030
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2031,
                    binding!!.dialogSelectMonth.txt2031
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2032,
                    binding!!.dialogSelectMonth.txt2032
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2033,
                    binding!!.dialogSelectMonth.txt2033
                )
            }
            R.id.btn2028 -> {
                selectedYear = "2028"
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2021,
                    binding!!.dialogSelectMonth.txt2021
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2022,
                    binding!!.dialogSelectMonth.txt2022
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2023,
                    binding!!.dialogSelectMonth.txt2023
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2024,
                    binding!!.dialogSelectMonth.txt2024
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2025,
                    binding!!.dialogSelectMonth.txt2025
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2026,
                    binding!!.dialogSelectMonth.txt2026
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2027,
                    binding!!.dialogSelectMonth.txt2027
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btn2028,
                    binding!!.dialogSelectMonth.txt2028
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2029,
                    binding!!.dialogSelectMonth.txt2029
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2030,
                    binding!!.dialogSelectMonth.txt2030
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2031,
                    binding!!.dialogSelectMonth.txt2031
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2032,
                    binding!!.dialogSelectMonth.txt2032
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2033,
                    binding!!.dialogSelectMonth.txt2033
                )
            }
            R.id.btn2029 -> {
                selectedYear = "2029"
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2021,
                    binding!!.dialogSelectMonth.txt2021
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2022,
                    binding!!.dialogSelectMonth.txt2022
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2023,
                    binding!!.dialogSelectMonth.txt2023
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2024,
                    binding!!.dialogSelectMonth.txt2024
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2025,
                    binding!!.dialogSelectMonth.txt2025
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2026,
                    binding!!.dialogSelectMonth.txt2026
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2027,
                    binding!!.dialogSelectMonth.txt2027
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2028,
                    binding!!.dialogSelectMonth.txt2028
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btn2029,
                    binding!!.dialogSelectMonth.txt2029
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2030,
                    binding!!.dialogSelectMonth.txt2030
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2031,
                    binding!!.dialogSelectMonth.txt2031
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2032,
                    binding!!.dialogSelectMonth.txt2032
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2033,
                    binding!!.dialogSelectMonth.txt2033
                )
            }
            R.id.btn2030 -> {
                selectedYear = "2030"
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2021,
                    binding!!.dialogSelectMonth.txt2021
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2022,
                    binding!!.dialogSelectMonth.txt2022
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2023,
                    binding!!.dialogSelectMonth.txt2023
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2024,
                    binding!!.dialogSelectMonth.txt2024
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2025,
                    binding!!.dialogSelectMonth.txt2025
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2026,
                    binding!!.dialogSelectMonth.txt2026
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2027,
                    binding!!.dialogSelectMonth.txt2027
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2028,
                    binding!!.dialogSelectMonth.txt2028
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2029,
                    binding!!.dialogSelectMonth.txt2029
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btn2030,
                    binding!!.dialogSelectMonth.txt2030
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2031,
                    binding!!.dialogSelectMonth.txt2031
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2032,
                    binding!!.dialogSelectMonth.txt2032
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2033,
                    binding!!.dialogSelectMonth.txt2033
                )
            }
            R.id.btn2031 -> {
                selectedYear = "2031"
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2021,
                    binding!!.dialogSelectMonth.txt2021
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2022,
                    binding!!.dialogSelectMonth.txt2022
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2023,
                    binding!!.dialogSelectMonth.txt2023
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2024,
                    binding!!.dialogSelectMonth.txt2024
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2025,
                    binding!!.dialogSelectMonth.txt2025
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2026,
                    binding!!.dialogSelectMonth.txt2026
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2027,
                    binding!!.dialogSelectMonth.txt2027
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2028,
                    binding!!.dialogSelectMonth.txt2028
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2029,
                    binding!!.dialogSelectMonth.txt2029
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2030,
                    binding!!.dialogSelectMonth.txt2030
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btn2031,
                    binding!!.dialogSelectMonth.txt2031
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2032,
                    binding!!.dialogSelectMonth.txt2032
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2033,
                    binding!!.dialogSelectMonth.txt2033
                )
            }
            R.id.btn2032 -> {
                selectedYear = "2032"
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2021,
                    binding!!.dialogSelectMonth.txt2021
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2022,
                    binding!!.dialogSelectMonth.txt2022
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2023,
                    binding!!.dialogSelectMonth.txt2023
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2024,
                    binding!!.dialogSelectMonth.txt2024
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2025,
                    binding!!.dialogSelectMonth.txt2025
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2026,
                    binding!!.dialogSelectMonth.txt2026
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2027,
                    binding!!.dialogSelectMonth.txt2027
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2028,
                    binding!!.dialogSelectMonth.txt2028
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2029,
                    binding!!.dialogSelectMonth.txt2029
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2030,
                    binding!!.dialogSelectMonth.txt2030
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2031,
                    binding!!.dialogSelectMonth.txt2031
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btn2032,
                    binding!!.dialogSelectMonth.txt2032
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2033,
                    binding!!.dialogSelectMonth.txt2033
                )
            }
            R.id.btn2033 -> {
                selectedYear = "2033"
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2021,
                    binding!!.dialogSelectMonth.txt2021
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2022,
                    binding!!.dialogSelectMonth.txt2022
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2023,
                    binding!!.dialogSelectMonth.txt2023
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2024,
                    binding!!.dialogSelectMonth.txt2024
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2025,
                    binding!!.dialogSelectMonth.txt2025
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2026,
                    binding!!.dialogSelectMonth.txt2026
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2027,
                    binding!!.dialogSelectMonth.txt2027
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2028,
                    binding!!.dialogSelectMonth.txt2028
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2029,
                    binding!!.dialogSelectMonth.txt2029
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2030,
                    binding!!.dialogSelectMonth.txt2030
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2031,
                    binding!!.dialogSelectMonth.txt2031
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2032,
                    binding!!.dialogSelectMonth.txt2032
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btn2033,
                    binding!!.dialogSelectMonth.txt2033
                )
            }
        }
    }

    override fun onBackPressed() {
        if(binding?.dialogSelectMonth?.monthLayout?.visibility == View.VISIBLE){
            SelectMonthNew = ""
            binding?.dialogSelectMonth?.monthLayout?.visibility = View.GONE
        }else{
            super.onBackPressed()
            Animatoo.animateSlideRight(activity)
        }
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(
            supportFragmentManager
        )
        adapter.addFragment(PieChartIncomeFragment(), "Income")
        adapter.addFragment(PieChartExpenseFragment(), "Expense")
        viewPager.adapter = adapter
    }

    /* adapter.addFragment(new GasFragment(), "Cylinder");*/
    internal class ViewPagerAdapter(manager: FragmentManager?) : FragmentPagerAdapter((manager)!!) {
        private val mFragmentList: MutableList<Fragment> = ArrayList()
        private val mFragmentTitleList: MutableList<String> = ArrayList()
        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence {
            return mFragmentTitleList[position]
        }
    }

    /*Select Current Month bydefault in dialog*/
    private fun selectCurrentMonthInDialog() {
        when {
            RestConstant.SELECTED_MONTH_ID.equals("1", ignoreCase = true) -> {
                SelectMonthNew = "January"
                MONTH_ID = "1"
                changeDarkColor(
                    binding!!.dialogSelectMonth.btnJanuary,
                    binding!!.dialogSelectMonth.txtJanuary
                )
                changeLightColor(binding!!.dialogSelectMonth.btnAll, binding!!.dialogSelectMonth.txtAll)
                changeLightColor(
                    binding!!.dialogSelectMonth.btnFebruary,
                    binding!!.dialogSelectMonth.txtFebruary
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnMarch,
                    binding!!.dialogSelectMonth.txtMarch
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnApril,
                    binding!!.dialogSelectMonth.txtApril
                )
                changeLightColor(binding!!.dialogSelectMonth.btnMay, binding!!.dialogSelectMonth.txtMay)
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJune,
                    binding!!.dialogSelectMonth.txtJune
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJuly,
                    binding!!.dialogSelectMonth.txtJuly
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnAugust,
                    binding!!.dialogSelectMonth.txtAugust
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnSeptember,
                    binding!!.dialogSelectMonth.txtSeptember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnOctober,
                    binding!!.dialogSelectMonth.txtOctober
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnNovember,
                    binding!!.dialogSelectMonth.txtNovember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnDecember,
                    binding!!.dialogSelectMonth.txtDecember
                )
            }
            RestConstant.SELECTED_MONTH_ID.equals("2", ignoreCase = true) -> {
                SelectMonthNew = "February"
                MONTH_ID = "2"
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJanuary,
                    binding!!.dialogSelectMonth.txtJanuary
                )
                changeLightColor(binding!!.dialogSelectMonth.btnAll, binding!!.dialogSelectMonth.txtAll)
                changeDarkColor(
                    binding!!.dialogSelectMonth.btnFebruary,
                    binding!!.dialogSelectMonth.txtFebruary
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnMarch,
                    binding!!.dialogSelectMonth.txtMarch
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnApril,
                    binding!!.dialogSelectMonth.txtApril
                )
                changeLightColor(binding!!.dialogSelectMonth.btnMay, binding!!.dialogSelectMonth.txtMay)
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJune,
                    binding!!.dialogSelectMonth.txtJune
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJuly,
                    binding!!.dialogSelectMonth.txtJuly
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnAugust,
                    binding!!.dialogSelectMonth.txtAugust
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnSeptember,
                    binding!!.dialogSelectMonth.txtSeptember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnOctober,
                    binding!!.dialogSelectMonth.txtOctober
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnNovember,
                    binding!!.dialogSelectMonth.txtNovember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnDecember,
                    binding!!.dialogSelectMonth.txtDecember
                )
            }
            RestConstant.SELECTED_MONTH_ID.equals("3", ignoreCase = true) -> {
                SelectMonthNew = "March"
                MONTH_ID = "3"
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJanuary,
                    binding!!.dialogSelectMonth.txtJanuary
                )
                changeLightColor(binding!!.dialogSelectMonth.btnAll, binding!!.dialogSelectMonth.txtAll)
                changeLightColor(
                    binding!!.dialogSelectMonth.btnFebruary,
                    binding!!.dialogSelectMonth.txtFebruary
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btnMarch,
                    binding!!.dialogSelectMonth.txtMarch
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnApril,
                    binding!!.dialogSelectMonth.txtApril
                )
                changeLightColor(binding!!.dialogSelectMonth.btnMay, binding!!.dialogSelectMonth.txtMay)
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJune,
                    binding!!.dialogSelectMonth.txtJune
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJuly,
                    binding!!.dialogSelectMonth.txtJuly
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnAugust,
                    binding!!.dialogSelectMonth.txtAugust
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnSeptember,
                    binding!!.dialogSelectMonth.txtSeptember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnOctober,
                    binding!!.dialogSelectMonth.txtOctober
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnNovember,
                    binding!!.dialogSelectMonth.txtNovember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnDecember,
                    binding!!.dialogSelectMonth.txtDecember
                )
            }
            RestConstant.SELECTED_MONTH_ID.equals("4", ignoreCase = true) -> {
                SelectMonthNew = "April"
                MONTH_ID = "4"
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJanuary,
                    binding!!.dialogSelectMonth.txtJanuary
                )
                changeLightColor(binding!!.dialogSelectMonth.btnAll, binding!!.dialogSelectMonth.txtAll)
                changeLightColor(
                    binding!!.dialogSelectMonth.btnFebruary,
                    binding!!.dialogSelectMonth.txtFebruary
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnMarch,
                    binding!!.dialogSelectMonth.txtMarch
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btnApril,
                    binding!!.dialogSelectMonth.txtApril
                )
                changeLightColor(binding!!.dialogSelectMonth.btnMay, binding!!.dialogSelectMonth.txtMay)
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJune,
                    binding!!.dialogSelectMonth.txtJune
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJuly,
                    binding!!.dialogSelectMonth.txtJuly
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnAugust,
                    binding!!.dialogSelectMonth.txtAugust
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnSeptember,
                    binding!!.dialogSelectMonth.txtSeptember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnOctober,
                    binding!!.dialogSelectMonth.txtOctober
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnNovember,
                    binding!!.dialogSelectMonth.txtNovember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnDecember,
                    binding!!.dialogSelectMonth.txtDecember
                )
            }
            RestConstant.SELECTED_MONTH_ID.equals("5", ignoreCase = true) -> {
                SelectMonthNew = "May"
                MONTH_ID = "5"
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJanuary,
                    binding!!.dialogSelectMonth.txtJanuary
                )
                changeLightColor(binding!!.dialogSelectMonth.btnAll, binding!!.dialogSelectMonth.txtAll)
                changeLightColor(
                    binding!!.dialogSelectMonth.btnFebruary,
                    binding!!.dialogSelectMonth.txtFebruary
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnMarch,
                    binding!!.dialogSelectMonth.txtMarch
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnApril,
                    binding!!.dialogSelectMonth.txtApril
                )
                changeDarkColor(binding!!.dialogSelectMonth.btnMay, binding!!.dialogSelectMonth.txtMay)
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJune,
                    binding!!.dialogSelectMonth.txtJune
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJuly,
                    binding!!.dialogSelectMonth.txtJuly
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnAugust,
                    binding!!.dialogSelectMonth.txtAugust
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnSeptember,
                    binding!!.dialogSelectMonth.txtSeptember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnOctober,
                    binding!!.dialogSelectMonth.txtOctober
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnNovember,
                    binding!!.dialogSelectMonth.txtNovember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnDecember,
                    binding!!.dialogSelectMonth.txtDecember
                )
            }
            RestConstant.SELECTED_MONTH_ID.equals("6", ignoreCase = true) -> {
                SelectMonthNew = "June"
                MONTH_ID = "6"
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJanuary,
                    binding!!.dialogSelectMonth.txtJanuary
                )
                changeLightColor(binding!!.dialogSelectMonth.btnAll, binding!!.dialogSelectMonth.txtAll)
                changeLightColor(
                    binding!!.dialogSelectMonth.btnFebruary,
                    binding!!.dialogSelectMonth.txtFebruary
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnMarch,
                    binding!!.dialogSelectMonth.txtMarch
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnApril,
                    binding!!.dialogSelectMonth.txtApril
                )
                changeLightColor(binding!!.dialogSelectMonth.btnMay, binding!!.dialogSelectMonth.txtMay)
                changeDarkColor(
                    binding!!.dialogSelectMonth.btnJune,
                    binding!!.dialogSelectMonth.txtJune
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJuly,
                    binding!!.dialogSelectMonth.txtJuly
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnAugust,
                    binding!!.dialogSelectMonth.txtAugust
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnSeptember,
                    binding!!.dialogSelectMonth.txtSeptember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnOctober,
                    binding!!.dialogSelectMonth.txtOctober
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnNovember,
                    binding!!.dialogSelectMonth.txtNovember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnDecember,
                    binding!!.dialogSelectMonth.txtDecember
                )
            }
            RestConstant.SELECTED_MONTH_ID.equals("7", ignoreCase = true) -> {
                SelectMonthNew = "July"
                MONTH_ID = "7"
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJanuary,
                    binding!!.dialogSelectMonth.txtJanuary
                )
                changeLightColor(binding!!.dialogSelectMonth.btnAll, binding!!.dialogSelectMonth.txtAll)
                changeLightColor(
                    binding!!.dialogSelectMonth.btnFebruary,
                    binding!!.dialogSelectMonth.txtFebruary
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnMarch,
                    binding!!.dialogSelectMonth.txtMarch
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnApril,
                    binding!!.dialogSelectMonth.txtApril
                )
                changeLightColor(binding!!.dialogSelectMonth.btnMay, binding!!.dialogSelectMonth.txtMay)
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJune,
                    binding!!.dialogSelectMonth.txtJune
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btnJuly,
                    binding!!.dialogSelectMonth.txtJuly
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnAugust,
                    binding!!.dialogSelectMonth.txtAugust
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnSeptember,
                    binding!!.dialogSelectMonth.txtSeptember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnOctober,
                    binding!!.dialogSelectMonth.txtOctober
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnNovember,
                    binding!!.dialogSelectMonth.txtNovember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnDecember,
                    binding!!.dialogSelectMonth.txtDecember
                )
            }
            RestConstant.SELECTED_MONTH_ID.equals("8", ignoreCase = true) -> {
                SelectMonthNew = "August"
                MONTH_ID = "8"
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJanuary,
                    binding!!.dialogSelectMonth.txtJanuary
                )
                changeLightColor(binding!!.dialogSelectMonth.btnAll, binding!!.dialogSelectMonth.txtAll)
                changeLightColor(
                    binding!!.dialogSelectMonth.btnFebruary,
                    binding!!.dialogSelectMonth.txtFebruary
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnMarch,
                    binding!!.dialogSelectMonth.txtMarch
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnApril,
                    binding!!.dialogSelectMonth.txtApril
                )
                changeLightColor(binding!!.dialogSelectMonth.btnMay, binding!!.dialogSelectMonth.txtMay)
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJune,
                    binding!!.dialogSelectMonth.txtJune
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJuly,
                    binding!!.dialogSelectMonth.txtJuly
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btnAugust,
                    binding!!.dialogSelectMonth.txtAugust
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnSeptember,
                    binding!!.dialogSelectMonth.txtSeptember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnOctober,
                    binding!!.dialogSelectMonth.txtOctober
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnNovember,
                    binding!!.dialogSelectMonth.txtNovember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnDecember,
                    binding!!.dialogSelectMonth.txtDecember
                )
            }
            RestConstant.SELECTED_MONTH_ID.equals("9", ignoreCase = true) -> {
                SelectMonthNew = "September"
                MONTH_ID = "9"
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJanuary,
                    binding!!.dialogSelectMonth.txtJanuary
                )
                changeLightColor(binding!!.dialogSelectMonth.btnAll, binding!!.dialogSelectMonth.txtAll)
                changeLightColor(
                    binding!!.dialogSelectMonth.btnFebruary,
                    binding!!.dialogSelectMonth.txtFebruary
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnMarch,
                    binding!!.dialogSelectMonth.txtMarch
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnApril,
                    binding!!.dialogSelectMonth.txtApril
                )
                changeLightColor(binding!!.dialogSelectMonth.btnMay, binding!!.dialogSelectMonth.txtMay)
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJune,
                    binding!!.dialogSelectMonth.txtJune
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJuly,
                    binding!!.dialogSelectMonth.txtJuly
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnAugust,
                    binding!!.dialogSelectMonth.txtAugust
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btnSeptember,
                    binding!!.dialogSelectMonth.txtSeptember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnOctober,
                    binding!!.dialogSelectMonth.txtOctober
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnNovember,
                    binding!!.dialogSelectMonth.txtNovember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnDecember,
                    binding!!.dialogSelectMonth.txtDecember
                )
            }
            RestConstant.SELECTED_MONTH_ID.equals("10", ignoreCase = true) -> {
                SelectMonthNew = "October"
                MONTH_ID = "10"
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJanuary,
                    binding!!.dialogSelectMonth.txtJanuary
                )
                changeLightColor(binding!!.dialogSelectMonth.btnAll, binding!!.dialogSelectMonth.txtAll)
                changeLightColor(
                    binding!!.dialogSelectMonth.btnFebruary,
                    binding!!.dialogSelectMonth.txtFebruary
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnMarch,
                    binding!!.dialogSelectMonth.txtMarch
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnApril,
                    binding!!.dialogSelectMonth.txtApril
                )
                changeLightColor(binding!!.dialogSelectMonth.btnMay, binding!!.dialogSelectMonth.txtMay)
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJune,
                    binding!!.dialogSelectMonth.txtJune
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJuly,
                    binding!!.dialogSelectMonth.txtJuly
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnAugust,
                    binding!!.dialogSelectMonth.txtAugust
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnSeptember,
                    binding!!.dialogSelectMonth.txtSeptember
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btnOctober,
                    binding!!.dialogSelectMonth.txtOctober
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnNovember,
                    binding!!.dialogSelectMonth.txtNovember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnDecember,
                    binding!!.dialogSelectMonth.txtDecember
                )
            }
            RestConstant.SELECTED_MONTH_ID.equals("11", ignoreCase = true) -> {
                SelectMonthNew = "November"
                MONTH_ID = "11"
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJanuary,
                    binding!!.dialogSelectMonth.txtJanuary
                )
                changeLightColor(binding!!.dialogSelectMonth.btnAll, binding!!.dialogSelectMonth.txtAll)
                changeLightColor(
                    binding!!.dialogSelectMonth.btnFebruary,
                    binding!!.dialogSelectMonth.txtFebruary
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnMarch,
                    binding!!.dialogSelectMonth.txtMarch
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnApril,
                    binding!!.dialogSelectMonth.txtApril
                )
                changeLightColor(binding!!.dialogSelectMonth.btnMay, binding!!.dialogSelectMonth.txtMay)
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJune,
                    binding!!.dialogSelectMonth.txtJune
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJuly,
                    binding!!.dialogSelectMonth.txtJuly
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnAugust,
                    binding!!.dialogSelectMonth.txtAugust
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnSeptember,
                    binding!!.dialogSelectMonth.txtSeptember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnOctober,
                    binding!!.dialogSelectMonth.txtOctober
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btnNovember,
                    binding!!.dialogSelectMonth.txtNovember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnDecember,
                    binding!!.dialogSelectMonth.txtDecember
                )
            }
            else -> {
                SelectMonthNew = "December"
                MONTH_ID = "12"
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJanuary,
                    binding!!.dialogSelectMonth.txtJanuary
                )
                changeLightColor(binding!!.dialogSelectMonth.btnAll, binding!!.dialogSelectMonth.txtAll)
                changeLightColor(
                    binding!!.dialogSelectMonth.btnFebruary,
                    binding!!.dialogSelectMonth.txtFebruary
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnMarch,
                    binding!!.dialogSelectMonth.txtMarch
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnApril,
                    binding!!.dialogSelectMonth.txtApril
                )
                changeLightColor(binding!!.dialogSelectMonth.btnMay, binding!!.dialogSelectMonth.txtMay)
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJune,
                    binding!!.dialogSelectMonth.txtJune
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnJuly,
                    binding!!.dialogSelectMonth.txtJuly
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnAugust,
                    binding!!.dialogSelectMonth.txtAugust
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnSeptember,
                    binding!!.dialogSelectMonth.txtSeptember
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnOctober,
                    binding!!.dialogSelectMonth.txtOctober
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btnNovember,
                    binding!!.dialogSelectMonth.txtNovember
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btnDecember,
                    binding!!.dialogSelectMonth.txtDecember
                )
            }
        }
    }

    fun changeDarkColor(btn: RelativeLayout, textView: AppCompatTextView) {
        btn.background = resources.getDrawable(R.drawable.rounded_button)
        textView.setTextColor(resources.getColor(R.color.white))
    }

    fun changeLightColor(btn: RelativeLayout, textView: AppCompatTextView) {
        btn.background = resources.getDrawable(R.drawable.rounded_border_without_cornor_white)
        textView.setTextColor(resources.getColor(R.color.black))
    }

    override fun onResume() {
        super.onResume()
        if (binding!!.viewPager.currentItem == 0) {
            val i1 = Intent("Intent")
            i1.putExtra("API", "INCOME")
            LocalBroadcastManager.getInstance(activity).sendBroadcast(i1)
        } else if (binding!!.viewPager.currentItem == 1) {
            val i1 = Intent("Intent")
            i1.putExtra("API", "EXPENSE")
            LocalBroadcastManager.getInstance(activity).sendBroadcast(i1)
        }
    }

        private fun refreshFragmentData() {
        binding!!.dialogSelectMonth.monthLayout.visibility = View.GONE

        val i1 = Intent("Intent")
        i1.putExtra("API", "INCOME")
        LocalBroadcastManager.getInstance(activity).sendBroadcast(i1)

        val i2 = Intent("Intent")
        i2.putExtra("API", "EXPENSE")
        LocalBroadcastManager.getInstance(activity).sendBroadcast(i2)

    }

    private fun selectCurrentYearInDialog() {
        when {
            AppUtils.getCurrentYear().toString().equals("2021", ignoreCase = true) -> {
                selectedYear = "2021"
                changeDarkColor(
                    binding!!.dialogSelectMonth.btn2021,
                    binding!!.dialogSelectMonth.txt2021
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2022,
                    binding!!.dialogSelectMonth.txt2022
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2023,
                    binding!!.dialogSelectMonth.txt2023
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2024,
                    binding!!.dialogSelectMonth.txt2024
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2025,
                    binding!!.dialogSelectMonth.txt2025
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2026,
                    binding!!.dialogSelectMonth.txt2026
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2027,
                    binding!!.dialogSelectMonth.txt2027
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2028,
                    binding!!.dialogSelectMonth.txt2028
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2029,
                    binding!!.dialogSelectMonth.txt2029
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2030,
                    binding!!.dialogSelectMonth.txt2030
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2031,
                    binding!!.dialogSelectMonth.txt2031
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2032,
                    binding!!.dialogSelectMonth.txt2032
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2033,
                    binding!!.dialogSelectMonth.txt2033
                )
            }
            AppUtils.getCurrentYear().toString().equals("2022", ignoreCase = true) -> {
                selectedYear = "2022"
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2021,
                    binding!!.dialogSelectMonth.txt2021
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btn2022,
                    binding!!.dialogSelectMonth.txt2022
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2023,
                    binding!!.dialogSelectMonth.txt2023
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2024,
                    binding!!.dialogSelectMonth.txt2024
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2025,
                    binding!!.dialogSelectMonth.txt2025
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2026,
                    binding!!.dialogSelectMonth.txt2026
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2027,
                    binding!!.dialogSelectMonth.txt2027
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2028,
                    binding!!.dialogSelectMonth.txt2028
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2029,
                    binding!!.dialogSelectMonth.txt2029
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2030,
                    binding!!.dialogSelectMonth.txt2030
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2031,
                    binding!!.dialogSelectMonth.txt2031
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2032,
                    binding!!.dialogSelectMonth.txt2032
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2033,
                    binding!!.dialogSelectMonth.txt2033
                )
            }
            AppUtils.getCurrentYear().toString().equals("2023", ignoreCase = true) -> {
                selectedYear = "2023"
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2021,
                    binding!!.dialogSelectMonth.txt2021
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2022,
                    binding!!.dialogSelectMonth.txt2022
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btn2023,
                    binding!!.dialogSelectMonth.txt2023
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2024,
                    binding!!.dialogSelectMonth.txt2024
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2025,
                    binding!!.dialogSelectMonth.txt2025
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2026,
                    binding!!.dialogSelectMonth.txt2026
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2027,
                    binding!!.dialogSelectMonth.txt2027
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2028,
                    binding!!.dialogSelectMonth.txt2028
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2029,
                    binding!!.dialogSelectMonth.txt2029
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2030,
                    binding!!.dialogSelectMonth.txt2030
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2031,
                    binding!!.dialogSelectMonth.txt2031
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2032,
                    binding!!.dialogSelectMonth.txt2032
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2033,
                    binding!!.dialogSelectMonth.txt2033
                )
            }
            AppUtils.getCurrentYear().toString().equals("2024", ignoreCase = true) -> {
                selectedYear = "2024"
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2021,
                    binding!!.dialogSelectMonth.txt2021
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2022,
                    binding!!.dialogSelectMonth.txt2022
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2023,
                    binding!!.dialogSelectMonth.txt2023
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btn2024,
                    binding!!.dialogSelectMonth.txt2024
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2025,
                    binding!!.dialogSelectMonth.txt2025
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2026,
                    binding!!.dialogSelectMonth.txt2026
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2027,
                    binding!!.dialogSelectMonth.txt2027
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2028,
                    binding!!.dialogSelectMonth.txt2028
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2029,
                    binding!!.dialogSelectMonth.txt2029
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2030,
                    binding!!.dialogSelectMonth.txt2030
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2031,
                    binding!!.dialogSelectMonth.txt2031
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2032,
                    binding!!.dialogSelectMonth.txt2032
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2033,
                    binding!!.dialogSelectMonth.txt2033
                )
            }
            AppUtils.getCurrentYear().toString().equals("2025", ignoreCase = true) -> {
                selectedYear = "2025"
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2021,
                    binding!!.dialogSelectMonth.txt2021
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2022,
                    binding!!.dialogSelectMonth.txt2022
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2023,
                    binding!!.dialogSelectMonth.txt2023
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2024,
                    binding!!.dialogSelectMonth.txt2024
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btn2025,
                    binding!!.dialogSelectMonth.txt2025
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2026,
                    binding!!.dialogSelectMonth.txt2026
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2027,
                    binding!!.dialogSelectMonth.txt2027
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2028,
                    binding!!.dialogSelectMonth.txt2028
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2029,
                    binding!!.dialogSelectMonth.txt2029
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2030,
                    binding!!.dialogSelectMonth.txt2030
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2031,
                    binding!!.dialogSelectMonth.txt2031
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2032,
                    binding!!.dialogSelectMonth.txt2032
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2033,
                    binding!!.dialogSelectMonth.txt2033
                )
            }
            AppUtils.getCurrentYear().toString().equals("2026", ignoreCase = true) -> {
                selectedYear = "2026"
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2021,
                    binding!!.dialogSelectMonth.txt2021
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2022,
                    binding!!.dialogSelectMonth.txt2022
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2023,
                    binding!!.dialogSelectMonth.txt2023
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2024,
                    binding!!.dialogSelectMonth.txt2024
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2025,
                    binding!!.dialogSelectMonth.txt2025
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btn2026,
                    binding!!.dialogSelectMonth.txt2026
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2027,
                    binding!!.dialogSelectMonth.txt2027
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2028,
                    binding!!.dialogSelectMonth.txt2028
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2029,
                    binding!!.dialogSelectMonth.txt2029
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2030,
                    binding!!.dialogSelectMonth.txt2030
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2031,
                    binding!!.dialogSelectMonth.txt2031
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2032,
                    binding!!.dialogSelectMonth.txt2032
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2033,
                    binding!!.dialogSelectMonth.txt2033
                )
            }
            AppUtils.getCurrentYear().toString().equals("2027", ignoreCase = true) -> {
                selectedYear = "2027"
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2021,
                    binding!!.dialogSelectMonth.txt2021
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2022,
                    binding!!.dialogSelectMonth.txt2022
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2023,
                    binding!!.dialogSelectMonth.txt2023
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2024,
                    binding!!.dialogSelectMonth.txt2024
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2025,
                    binding!!.dialogSelectMonth.txt2025
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2026,
                    binding!!.dialogSelectMonth.txt2026
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btn2027,
                    binding!!.dialogSelectMonth.txt2027
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2028,
                    binding!!.dialogSelectMonth.txt2028
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2029,
                    binding!!.dialogSelectMonth.txt2029
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2030,
                    binding!!.dialogSelectMonth.txt2030
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2031,
                    binding!!.dialogSelectMonth.txt2031
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2032,
                    binding!!.dialogSelectMonth.txt2032
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2033,
                    binding!!.dialogSelectMonth.txt2033
                )
            }
            AppUtils.getCurrentYear().toString().equals("2028", ignoreCase = true) -> {
                selectedYear = "2028"
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2021,
                    binding!!.dialogSelectMonth.txt2021
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2022,
                    binding!!.dialogSelectMonth.txt2022
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2023,
                    binding!!.dialogSelectMonth.txt2023
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2024,
                    binding!!.dialogSelectMonth.txt2024
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2025,
                    binding!!.dialogSelectMonth.txt2025
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2026,
                    binding!!.dialogSelectMonth.txt2026
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2027,
                    binding!!.dialogSelectMonth.txt2027
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btn2028,
                    binding!!.dialogSelectMonth.txt2028
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2029,
                    binding!!.dialogSelectMonth.txt2029
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2030,
                    binding!!.dialogSelectMonth.txt2030
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2031,
                    binding!!.dialogSelectMonth.txt2031
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2032,
                    binding!!.dialogSelectMonth.txt2032
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2033,
                    binding!!.dialogSelectMonth.txt2033
                )
            }
            AppUtils.getCurrentYear().toString().equals("2029", ignoreCase = true) -> {
                selectedYear = "2029"
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2021,
                    binding!!.dialogSelectMonth.txt2021
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2022,
                    binding!!.dialogSelectMonth.txt2022
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2023,
                    binding!!.dialogSelectMonth.txt2023
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2024,
                    binding!!.dialogSelectMonth.txt2024
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2025,
                    binding!!.dialogSelectMonth.txt2025
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2026,
                    binding!!.dialogSelectMonth.txt2026
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2027,
                    binding!!.dialogSelectMonth.txt2027
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2028,
                    binding!!.dialogSelectMonth.txt2028
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btn2029,
                    binding!!.dialogSelectMonth.txt2029
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2030,
                    binding!!.dialogSelectMonth.txt2030
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2031,
                    binding!!.dialogSelectMonth.txt2031
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2032,
                    binding!!.dialogSelectMonth.txt2032
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2033,
                    binding!!.dialogSelectMonth.txt2033
                )
            }
            AppUtils.getCurrentYear().toString().equals("2030", ignoreCase = true) -> {
                selectedYear = "2030"
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2021,
                    binding!!.dialogSelectMonth.txt2021
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2022,
                    binding!!.dialogSelectMonth.txt2022
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2023,
                    binding!!.dialogSelectMonth.txt2023
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2024,
                    binding!!.dialogSelectMonth.txt2024
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2025,
                    binding!!.dialogSelectMonth.txt2025
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2026,
                    binding!!.dialogSelectMonth.txt2026
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2027,
                    binding!!.dialogSelectMonth.txt2027
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2028,
                    binding!!.dialogSelectMonth.txt2028
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2029,
                    binding!!.dialogSelectMonth.txt2029
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btn2030,
                    binding!!.dialogSelectMonth.txt2030
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2031,
                    binding!!.dialogSelectMonth.txt2031
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2032,
                    binding!!.dialogSelectMonth.txt2032
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2033,
                    binding!!.dialogSelectMonth.txt2033
                )
            }
            AppUtils.getCurrentYear().toString().equals("2031", ignoreCase = true) -> {
                selectedYear = "2031"
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2021,
                    binding!!.dialogSelectMonth.txt2021
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2022,
                    binding!!.dialogSelectMonth.txt2022
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2023,
                    binding!!.dialogSelectMonth.txt2023
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2024,
                    binding!!.dialogSelectMonth.txt2024
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2025,
                    binding!!.dialogSelectMonth.txt2025
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2026,
                    binding!!.dialogSelectMonth.txt2026
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2027,
                    binding!!.dialogSelectMonth.txt2027
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2028,
                    binding!!.dialogSelectMonth.txt2028
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2029,
                    binding!!.dialogSelectMonth.txt2029
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2030,
                    binding!!.dialogSelectMonth.txt2030
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btn2031,
                    binding!!.dialogSelectMonth.txt2031
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2032,
                    binding!!.dialogSelectMonth.txt2032
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2033,
                    binding!!.dialogSelectMonth.txt2033
                )
            }
            AppUtils.getCurrentYear().toString().equals("2032", ignoreCase = true) ->  {
                selectedYear = "2032"
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2021,
                    binding!!.dialogSelectMonth.txt2021
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2022,
                    binding!!.dialogSelectMonth.txt2022
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2023,
                    binding!!.dialogSelectMonth.txt2023
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2024,
                    binding!!.dialogSelectMonth.txt2024
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2025,
                    binding!!.dialogSelectMonth.txt2025
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2026,
                    binding!!.dialogSelectMonth.txt2026
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2027,
                    binding!!.dialogSelectMonth.txt2027
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2028,
                    binding!!.dialogSelectMonth.txt2028
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2029,
                    binding!!.dialogSelectMonth.txt2029
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2030,
                    binding!!.dialogSelectMonth.txt2030
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2031,
                    binding!!.dialogSelectMonth.txt2031
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btn2032,
                    binding!!.dialogSelectMonth.txt2032
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2033,
                    binding!!.dialogSelectMonth.txt2033
                )
            }
            else ->{
                selectedYear = "2033"
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2021,
                    binding!!.dialogSelectMonth.txt2021
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2022,
                    binding!!.dialogSelectMonth.txt2022
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2023,
                    binding!!.dialogSelectMonth.txt2023
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2024,
                    binding!!.dialogSelectMonth.txt2024
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2025,
                    binding!!.dialogSelectMonth.txt2025
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2026,
                    binding!!.dialogSelectMonth.txt2026
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2027,
                    binding!!.dialogSelectMonth.txt2027
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2028,
                    binding!!.dialogSelectMonth.txt2028
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2029,
                    binding!!.dialogSelectMonth.txt2029
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2030,
                    binding!!.dialogSelectMonth.txt2030
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2031,
                    binding!!.dialogSelectMonth.txt2031
                )
                changeLightColor(
                    binding!!.dialogSelectMonth.btn2032,
                    binding!!.dialogSelectMonth.txt2032
                )
                changeDarkColor(
                    binding!!.dialogSelectMonth.btn2033,
                    binding!!.dialogSelectMonth.txt2033
                )
            }
        }
    }
}