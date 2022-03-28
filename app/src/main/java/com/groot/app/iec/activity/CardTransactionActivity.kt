package com.groot.app.iec.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager.widget.ViewPager
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.groot.app.iec.R
import com.groot.app.iec.adapter.SelectFilterAdapter
import com.groot.app.iec.databinding.ActivityCardTransactionBinding
import com.groot.app.iec.fragment.CardTransactionExpenseFragment
import com.groot.app.iec.fragment.CardTransactionIncomeFragment
import com.groot.app.iec.model.FilterListModel
import com.groot.app.iec.rest.RestConstant
import com.groot.app.iec.utils.AppUtils
import com.groot.app.iec.utils.BaseActivity
import com.groot.app.iec.utils.MySharedPreferences
import java.util.*
import kotlin.collections.ArrayList

class CardTransactionActivity : BaseActivity(), View.OnClickListener,
    CardTransactionIncomeFragment.onIncomeEventListener,
    CardTransactionExpenseFragment.onExpenseEventListener {
    private var binding: ActivityCardTransactionBinding? = null
    private var dialogSelectMonth: View? = null
    private var SelectMonthNew = RestConstant.SELECTED_MONTH
    private var selectedYear = ""
    private var MONTH_ID = RestConstant.SELECTED_MONTH_ID
    private var filterSelectedOption = ""
    private var lastSavedFilter = "Month"

    private val allList: ArrayList<FilterListModel> = ArrayList<FilterListModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        super.onCreate(savedInstanceState)
        binding = ActivityCardTransactionBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        dialogSelectMonth = findViewById(R.id.dialogSelectMonth)

        /*Spinner Language Adapter Set*/
        binding?.dialogSelectMonth?.txtdropdown?.adapter = SelectFilterAdapter(
            this@CardTransactionActivity,
            R.layout.spinner_row,
            getAllList()
        )

        showBannerAds(this,binding!!.adLayout)

        binding?.apply {

            txtTitle.text = intent.getStringExtra("source").toString() + " Transactions"

            tvIncomeAmount.text =
                MySharedPreferences.getMySharedPreferences()?.currency.toString() + "0"

            tvExpenseAmount.text =
                MySharedPreferences.getMySharedPreferences()?.currency.toString() + "0"

            btnIncome.setBackgroundResource(R.color.facebook)
            btnExpense.setBackgroundResource(R.color.dark_blue)
            btnBack.setOnClickListener(this@CardTransactionActivity)
            btnIncome.setOnClickListener(this@CardTransactionActivity)
            btnExpense.setOnClickListener(this@CardTransactionActivity)
            btnFilter.setOnClickListener(this@CardTransactionActivity)

            //For Month
            dialogSelectMonth.btnJanuary.setOnClickListener(this@CardTransactionActivity)
            dialogSelectMonth.btnFebruary.setOnClickListener(this@CardTransactionActivity)
            dialogSelectMonth.btnMarch.setOnClickListener(this@CardTransactionActivity)
            dialogSelectMonth.btnApril.setOnClickListener(this@CardTransactionActivity)
            dialogSelectMonth.btnMay.setOnClickListener(this@CardTransactionActivity)
            dialogSelectMonth.btnJune.setOnClickListener(this@CardTransactionActivity)
            dialogSelectMonth.btnJuly.setOnClickListener(this@CardTransactionActivity)
            dialogSelectMonth.btnAugust.setOnClickListener(this@CardTransactionActivity)
            dialogSelectMonth.btnSeptember.setOnClickListener(this@CardTransactionActivity)
            dialogSelectMonth.btnOctober.setOnClickListener(this@CardTransactionActivity)
            dialogSelectMonth.btnNovember.setOnClickListener(this@CardTransactionActivity)
            dialogSelectMonth.btnDecember.setOnClickListener(this@CardTransactionActivity)
            dialogSelectMonth.btnAll.setOnClickListener(this@CardTransactionActivity)
            dialogSelectMonth.btnTodaySave.setOnClickListener(this@CardTransactionActivity)

            dialogSelectMonth.btn2021.setOnClickListener(this@CardTransactionActivity)
            dialogSelectMonth.btn2022.setOnClickListener(this@CardTransactionActivity)
            dialogSelectMonth.btn2023.setOnClickListener(this@CardTransactionActivity)
            dialogSelectMonth.btn2024.setOnClickListener(this@CardTransactionActivity)
            dialogSelectMonth.btn2025.setOnClickListener(this@CardTransactionActivity)
            dialogSelectMonth.btn2026.setOnClickListener(this@CardTransactionActivity)
            dialogSelectMonth.btn2027.setOnClickListener(this@CardTransactionActivity)
            dialogSelectMonth.btn2028.setOnClickListener(this@CardTransactionActivity)
            dialogSelectMonth.btn2029.setOnClickListener(this@CardTransactionActivity)
            dialogSelectMonth.btn2030.setOnClickListener(this@CardTransactionActivity)
            dialogSelectMonth.btn2031.setOnClickListener(this@CardTransactionActivity)
            dialogSelectMonth.btn2032.setOnClickListener(this@CardTransactionActivity)
            dialogSelectMonth.btn2033.setOnClickListener(this@CardTransactionActivity)
            dialogSelectMonth.btnYearSave.setOnClickListener(this@CardTransactionActivity)

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

            selectCurrentMonthInDialog()

            selectCurrentYearInDialog()

            //Todo:: Save Month
            dialogSelectMonth.btnSave.setOnClickListener(View.OnClickListener {
                lastSavedFilter = "Month"
                hideshowBannerAds(true)
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
                    hideshowBannerAds(true)
                }
            })

            dialogSelectMonth.btnYearSave.setOnClickListener{
                lastSavedFilter = "Year"
                hideshowBannerAds(true)
                RestConstant.SELECTED_MONTH_ID = ""
                RestConstant.DATE = ""
                RestConstant.SELECTED_YEAR = selectedYear
                headerTitle.text = ""
                headerTitleYear.text = selectedYear

                dialogSelectMonth.monthLayout.visibility = View.GONE
                refreshFragmentData()
            }
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
                hideshowBannerAds(false)
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
                hideshowBannerAds(true)
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
            hideshowBannerAds(true)
        }else{
            super.onBackPressed()
            Animatoo.animateSlideRight(activity)
        }
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(
            supportFragmentManager
        )
        adapter.addFragment(
            CardTransactionIncomeFragment(
                intent.getStringExtra("source").toString()
            ), "Income"
        )
        adapter.addFragment(
            CardTransactionExpenseFragment(
                intent.getStringExtra("source").toString()
            ), "Expense"
        )
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

    fun changeDarkColor(btn: RelativeLayout, textView: AppCompatTextView) {
        btn.background = resources.getDrawable(R.drawable.rounded_button)
        textView.setTextColor(resources.getColor(R.color.white))
    }

    fun changeLightColor(btn: RelativeLayout, textView: AppCompatTextView) {
        btn.background = resources.getDrawable(R.drawable.rounded_border_without_cornor_white)
        textView.setTextColor(resources.getColor(R.color.black))
    }

    override fun incomeValue(s: String?) {
        if (s != null && s != "") {
            binding?.tvIncomeAmount?.text = s
        }
    }

    override fun expenseValue(s: String?) {
        if (s != null && s != "") {
            binding?.tvExpenseAmount?.text = s
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

    private fun hideshowBannerAds(isShow: Boolean){
        if(isShow == true){
            binding?.adLayout?.visibility = View.VISIBLE
        }else {
            binding?.adLayout?.visibility = View.GONE
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
            RestConstant.SELECTED_MONTH_ID.equals("2", ignoreCase = true) -> {
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
            RestConstant.SELECTED_MONTH_ID.equals("3", ignoreCase = true) -> {
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
            RestConstant.SELECTED_MONTH_ID.equals("4", ignoreCase = true) -> {
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
            RestConstant.SELECTED_MONTH_ID.equals("5", ignoreCase = true) -> {
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
            RestConstant.SELECTED_MONTH_ID.equals("6", ignoreCase = true) -> {
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
            RestConstant.SELECTED_MONTH_ID.equals("7", ignoreCase = true) -> {
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
            RestConstant.SELECTED_MONTH_ID.equals("8", ignoreCase = true) -> {
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
            RestConstant.SELECTED_MONTH_ID.equals("9", ignoreCase = true) -> {
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
            RestConstant.SELECTED_MONTH_ID.equals("10", ignoreCase = true) -> {
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
            RestConstant.SELECTED_MONTH_ID.equals("11", ignoreCase = true) -> {
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
            else -> {
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
        }
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