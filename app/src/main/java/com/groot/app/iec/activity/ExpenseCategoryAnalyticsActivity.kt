package com.groot.app.iec.activity

import com.groot.app.iec.utils.AppUtils.clearUpdateConstant
import com.groot.app.iec.utils.AppUtils.isConnectedToInternet
import com.groot.app.iec.utils.AppUtils.doubleToStringNoDecimal
import com.groot.app.iec.rest.RestConstant
import com.groot.app.iec.adapter.ExpenseCategoryAnalyticsAdapter
import com.groot.app.iec.adapter.ExpensePaidByListAdapter
import com.groot.app.iec.utils.swipableRecyclerview.RecyclerTouchListener
import android.os.Bundle
import android.content.Intent
import com.groot.app.iec.R
import android.app.DatePickerDialog.OnDateSetListener
import android.app.DatePickerDialog
import com.groot.app.iec.utils.swipableRecyclerview.RecyclerTouchListener.OnRowClickListener
import com.groot.app.iec.utils.swipableRecyclerview.RecyclerTouchListener.OnSwipeOptionsClickListener
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatTextView
import com.groot.app.iec.model.category_details.CategoryDetailsResponse
import com.groot.app.iec.rest.RetrofitRestClient
import com.groot.app.iec.utils.MySharedPreferences
import com.groot.app.iec.model.analytics.AnalyticsResponse
import androidx.recyclerview.widget.LinearLayoutManager
import com.groot.app.iec.model.payment_method.PaymentMethodResponse
import android.content.DialogInterface
import android.view.View
import android.widget.AdapterView
import com.groot.app.iec.adapter.SelectFilterAdapter
import com.groot.app.iec.databinding.ActivityExpenseCategoryAnalyticsBinding
import com.groot.app.iec.model.BasicModelResponse
import com.groot.app.iec.model.FilterListModel
import com.groot.app.iec.model.analytics.DataItem
import com.groot.app.iec.utils.AppUtils
import com.groot.app.iec.utils.BaseActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class ExpenseCategoryAnalyticsActivity : BaseActivity(), View.OnClickListener {
    private var binding: ActivityExpenseCategoryAnalyticsBinding? = null
    private var dialogSelectMonth: View? = null
    var SelectMonthNew = RestConstant.SELECTED_MONTH
    var MONTH_ID = RestConstant.SELECTED_MONTH_ID
    private var expenseCategoryAnalyticsAdapter: ExpenseCategoryAnalyticsAdapter? = null
    private var analyticsList: ArrayList<DataItem>? = null
    private var expensePaidByListAdapter: ExpensePaidByListAdapter? = null
    private var paidByList: ArrayList<com.groot.app.iec.model.payment_method.DataItem>? = null
    private var Source: String = ""
    private var Progress: String = ""
    private var touchListener: RecyclerTouchListener? = null

    private var selectedYear = ""
    private var filterSelectedOption = ""
    private var lastSavedFilter = "Month"
    private val allList: ArrayList<FilterListModel> = ArrayList<FilterListModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseCategoryAnalyticsBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        analyticsList = ArrayList()
        paidByList = ArrayList()

        val intent = intent
        if (intent != null) {
            Source = intent.getStringExtra("Source")!!
            Progress = intent.getStringExtra("Progress")!!
        }
        if (Source.equals("Food & Drinks", ignoreCase = true)) {
            binding!!.ivCategory.setImageDrawable(resources.getDrawable(R.drawable.ic_food___drinks))
        } else if (Source.equals("Transportation", ignoreCase = true)) {
            binding!!.ivCategory.setImageDrawable(resources.getDrawable(R.drawable.ic_transportation))
        } else if (Source.equals("Uilities", ignoreCase = true)) {
            binding!!.ivCategory.setImageDrawable(resources.getDrawable(R.drawable.ic_utilities))
        } else if (Source.equals("Shopping", ignoreCase = true)) {
            binding!!.ivCategory.setImageDrawable(resources.getDrawable(R.drawable.ic_shopping))
        } else if (Source.equals("Medical/Healthcare", ignoreCase = true)) {
            binding!!.ivCategory.setImageDrawable(resources.getDrawable(R.drawable.ic_medical_healthcare))
        } else if (Source.equals("Insurance", ignoreCase = true)) {
            binding!!.ivCategory.setImageDrawable(resources.getDrawable(R.drawable.ic_insurance))
        } else if (Source.equals("Education", ignoreCase = true)) {
            binding!!.ivCategory.setImageDrawable(resources.getDrawable(R.drawable.ic_education))
        } else if (Source.equals("Entertainment", ignoreCase = true)) {
            binding!!.ivCategory.setImageDrawable(resources.getDrawable(R.drawable.ic_entertainment))
        } else if (Source.equals("Groceries", ignoreCase = true)) {
            binding!!.ivCategory.setImageDrawable(resources.getDrawable(R.drawable.ic_groceries))
        } else {
            binding!!.ivCategory.setImageDrawable(resources.getDrawable(R.drawable.ic_custom))
        }
        RestConstant.EXPaidBy = ""
        binding!!.tvProgress.text = "$Progress%"
        // binding.progressBar.setProgress(Integer.valueOf(Progress));
        binding!!.txtName.text = Source
        dialogSelectMonth = findViewById(R.id.dialogSelectMonth)

        showBannerAds(this,binding!!.adLayout)

        /*Spinner Language Adapter Set*/
        binding?.dialogSelectMonth?.txtdropdown?.adapter = SelectFilterAdapter(
            this@ExpenseCategoryAnalyticsActivity,
            R.layout.spinner_row,
            getAllList()
        )

        binding?.apply {
            btnBack.setOnClickListener(this@ExpenseCategoryAnalyticsActivity)
            headerTitle.setOnClickListener(this@ExpenseCategoryAnalyticsActivity)
            dialogSelectMonth.btnJanuary.setOnClickListener(this@ExpenseCategoryAnalyticsActivity)
            dialogSelectMonth.btnFebruary.setOnClickListener(this@ExpenseCategoryAnalyticsActivity)
            dialogSelectMonth.btnMarch.setOnClickListener(this@ExpenseCategoryAnalyticsActivity)
            dialogSelectMonth.btnApril.setOnClickListener(this@ExpenseCategoryAnalyticsActivity)
            dialogSelectMonth.btnMay.setOnClickListener(this@ExpenseCategoryAnalyticsActivity)
            dialogSelectMonth.btnJune.setOnClickListener(this@ExpenseCategoryAnalyticsActivity)
            dialogSelectMonth.btnJuly.setOnClickListener(this@ExpenseCategoryAnalyticsActivity)
            dialogSelectMonth.btnAugust.setOnClickListener(this@ExpenseCategoryAnalyticsActivity)
            dialogSelectMonth.btnSeptember.setOnClickListener(this@ExpenseCategoryAnalyticsActivity)
            dialogSelectMonth.btnOctober.setOnClickListener(this@ExpenseCategoryAnalyticsActivity)
            dialogSelectMonth.btnNovember.setOnClickListener(this@ExpenseCategoryAnalyticsActivity)
            dialogSelectMonth.btnDecember.setOnClickListener(this@ExpenseCategoryAnalyticsActivity)
            dialogSelectMonth.btnAll.setOnClickListener(this@ExpenseCategoryAnalyticsActivity)
            dialogSelectMonth.btnTodaySave.setOnClickListener(this@ExpenseCategoryAnalyticsActivity)
            btnFilter.setOnClickListener(this@ExpenseCategoryAnalyticsActivity)

            dialogSelectMonth.btn2021.setOnClickListener(this@ExpenseCategoryAnalyticsActivity)
            dialogSelectMonth.btn2022.setOnClickListener(this@ExpenseCategoryAnalyticsActivity)
            dialogSelectMonth.btn2023.setOnClickListener(this@ExpenseCategoryAnalyticsActivity)
            dialogSelectMonth.btn2024.setOnClickListener(this@ExpenseCategoryAnalyticsActivity)
            dialogSelectMonth.btn2025.setOnClickListener(this@ExpenseCategoryAnalyticsActivity)
            dialogSelectMonth.btn2026.setOnClickListener(this@ExpenseCategoryAnalyticsActivity)
            dialogSelectMonth.btn2027.setOnClickListener(this@ExpenseCategoryAnalyticsActivity)
            dialogSelectMonth.btn2028.setOnClickListener(this@ExpenseCategoryAnalyticsActivity)
            dialogSelectMonth.btn2029.setOnClickListener(this@ExpenseCategoryAnalyticsActivity)
            dialogSelectMonth.btn2030.setOnClickListener(this@ExpenseCategoryAnalyticsActivity)
            dialogSelectMonth.btn2031.setOnClickListener(this@ExpenseCategoryAnalyticsActivity)
            dialogSelectMonth.btn2032.setOnClickListener(this@ExpenseCategoryAnalyticsActivity)
            dialogSelectMonth.btn2033.setOnClickListener(this@ExpenseCategoryAnalyticsActivity)
            dialogSelectMonth.btnYearSave.setOnClickListener(this@ExpenseCategoryAnalyticsActivity)

            selectCurrentMonthInDialog()

            selectCurrentYearInDialog()

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

            binding!!.dialogSelectMonth.btnSave.setOnClickListener {
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
                binding!!.dialogSelectMonth.monthLayout.visibility = View.GONE
                SpinnerAnalyticsListAPICall()
            }

            binding!!.dialogSelectMonth.btnClose.setOnClickListener {
                SelectMonthNew = ""
                dialogSelectMonth.monthLayout.visibility = View.GONE
            }

            dialogSelectMonth.btnYearSave.setOnClickListener{
                lastSavedFilter = "Year"
                RestConstant.SELECTED_MONTH_ID = ""
                RestConstant.DATE = ""
                RestConstant.SELECTED_YEAR = selectedYear
                headerTitle.text = ""
                headerTitleYear.text = selectedYear

                dialogSelectMonth.monthLayout.visibility = View.GONE
                binding!!.dialogSelectMonth.monthLayout.visibility = View.GONE
                SpinnerAnalyticsListAPICall()
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

        /*For Swipe Recycler view to show an option*/
        touchListener = RecyclerTouchListener(activity, binding!!.rcvAnalysis)
        touchListener!!
            .setClickable(object : OnRowClickListener {
                override fun onRowClicked(position: Int) {
                    //Toast.makeText(getActivity(), String.valueOf(position), Toast.LENGTH_SHORT).show();
                }

                override fun onIndependentViewClicked(independentViewID: Int, position: Int) {}
            })
            .setSwipeOptionViews(R.id.delete_task, R.id.edit_task)
            .setSwipeable(R.id.rowFG, R.id.rowBG, object : OnSwipeOptionsClickListener {
                override fun onSwipeOptionClicked(viewID: Int, position: Int) {
                    when (viewID) {
                        R.id.delete_task -> dialogDelete(
                            "EXPENSE",
                            analyticsList!![position].id.toString(),
                            position
                        )
                        R.id.edit_task -> {
                            clearUpdateConstant()
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
            })
        binding!!.rcvAnalysis.addOnItemTouchListener(touchListener!!)
        SpinnerAnalyticsListAPICall()
        paymentMethodAPICall()
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


    fun transactionListClick(position: Int) {
        clearUpdateConstant()
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

    /*Select Current Month bydefault in dialog*/
    private fun selectCurrentMonthInDialog() {
        if (RestConstant.SELECTED_MONTH_ID.equals("1", ignoreCase = true)) {
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
        } else if (RestConstant.SELECTED_MONTH_ID.equals("2", ignoreCase = true)) {
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
        } else if (RestConstant.SELECTED_MONTH_ID.equals("3", ignoreCase = true)) {
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
        } else if (RestConstant.SELECTED_MONTH_ID.equals("4", ignoreCase = true)) {
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
        } else if (RestConstant.SELECTED_MONTH_ID.equals("5", ignoreCase = true)) {
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
        } else if (RestConstant.SELECTED_MONTH_ID.equals("6", ignoreCase = true)) {
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
        } else if (RestConstant.SELECTED_MONTH_ID.equals("7", ignoreCase = true)) {
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
        } else if (RestConstant.SELECTED_MONTH_ID.equals("8", ignoreCase = true)) {
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
        } else if (RestConstant.SELECTED_MONTH_ID.equals("9", ignoreCase = true)) {
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
        } else if (RestConstant.SELECTED_MONTH_ID.equals("10", ignoreCase = true)) {
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
        } else if (RestConstant.SELECTED_MONTH_ID.equals("11", ignoreCase = true)) {
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
        } else {
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

    @SuppressLint("WrongConstant")
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnBack -> onBackPressed()
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
                    binding!!.dialogSelectMonth.monthLayout.visibility = View.GONE
                    SpinnerAnalyticsListAPICall()
                } else {
                    lastSavedFilter = "Yesterday"
                    binding?.headerTitle?.text = "Yesterday"
                    RestConstant.SELECTED_MONTH_ID = ""
                    RestConstant.SELECTED_YEAR = ""
                    RestConstant.DATE = AppUtils.getYesterDayDate()
                    binding?.headerTitleYear?.text = ""
                    binding!!.dialogSelectMonth.monthLayout.visibility = View.GONE
                    SpinnerAnalyticsListAPICall()
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

    fun changeDarkColor(btn: RelativeLayout, textView: AppCompatTextView) {
        btn.background = resources.getDrawable(R.drawable.rounded_button)
        textView.setTextColor(resources.getColor(R.color.white))
    }

    fun changeLightColor(btn: RelativeLayout, textView: AppCompatTextView) {
        btn.background = resources.getDrawable(R.drawable.rounded_border_without_cornor_white)
        textView.setTextColor(resources.getColor(R.color.black))
    }

    //Todo:: Get Category Details API Calling
    private fun categoryDetailsAPICall() {
        try {
            if (isConnectedToInternet(activity)) {
                // showProgressDialog(getActivity());
                val params = HashMap<String?, String?>()
                params["month"] = RestConstant.SELECTED_MONTH_ID
                params["category"] = Source
                params["payment_method"] = RestConstant.ICMoneyIn
                params["type"] = "expense"
                params["start_date"] = RestConstant.START_DATE
                params["end_date"] = RestConstant.END_DATE
                val call: Call<CategoryDetailsResponse>?
                call = RetrofitRestClient.instance?.getCategoryAnalyticsDetailsAPICall(
                    params,
                    MySharedPreferences.getMySharedPreferences()?.accessToken
                )
                if (call == null) return
                call.enqueue(object : Callback<CategoryDetailsResponse?> {
                    override fun onResponse(
                        call: Call<CategoryDetailsResponse?>,
                        response: Response<CategoryDetailsResponse?>
                    ) {
                        //  hideProgressDialog();
                        val categoryDetailsResponse: CategoryDetailsResponse?
                        if (response.isSuccessful) {
                            categoryDetailsResponse = response.body()
                            if (Objects.requireNonNull(categoryDetailsResponse)?.code == 200) {
                                binding!!.tvProgress.text =
                                    categoryDetailsResponse!!.categoryPercentage.toString() + "%"
                                //  binding.progressBar.setProgress((int)categoryDetailsResponse.getCategoryPercentage());
                                binding!!.txtName.text = Source
                                binding!!.txtAmount.setText(
                                    MySharedPreferences.getMySharedPreferences()?.currency
                                        .toString() + doubleToStringNoDecimal(
                                        categoryDetailsResponse.categoryAmount.toString()
                                    )
                                )
                            } else if (Objects.requireNonNull(categoryDetailsResponse)?.code == 404
                            ) {
                            } else if (Objects.requireNonNull(categoryDetailsResponse)?.code == 999
                            ) {
                                logout(activity)
                            } else {
                                showSnackBar(activity, categoryDetailsResponse!!.message)
                            }
                        } else {
                            showSnackBar(activity, response.message())
                        }
                    }

                    override fun onFailure(call: Call<CategoryDetailsResponse?>, t: Throwable) {
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

    // ANALYTICS LIST API CALLING
    private fun SpinnerAnalyticsListAPICall() {
        try {
            if (isConnectedToInternet(activity)) {
                showProgressDialog(activity)
                val params = HashMap<String?, String?>()
                params["month"] = RestConstant.SELECTED_MONTH_ID
                params["source"] = Source
                params["pay_method"] = RestConstant.EXPaidBy
                params["type"] = "expense"
                params["start_date"] = ""
                params["end_date"] = ""
                params["date"] = RestConstant.DATE
                params["year"] = RestConstant.SELECTED_YEAR
                val call: Call<AnalyticsResponse>?
                call = RetrofitRestClient.instance?.getAnalyticsListAPICall(
                    params,
                    MySharedPreferences.getMySharedPreferences()?.accessToken
                )
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
                            if (Objects.requireNonNull(analyticsResponse)?.code == 200) {
                                analyticsList!!.clear()
                                analyticsList!!.addAll(analyticsResponse!!.data!!)
                                binding!!.txtAmount.setText(
                                    MySharedPreferences.getMySharedPreferences()?.currency
                                        .toString() + doubleToStringNoDecimal(
                                        analyticsResponse.expenseAmount.toString()
                                    )
                                )
                                binding!!.txtAmount.setTextColor(resources.getColor(R.color.expense_orange))
                                binding!!.rcvAnalysis.layoutManager = LinearLayoutManager(
                                    activity,
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                                expenseCategoryAnalyticsAdapter = ExpenseCategoryAnalyticsAdapter(
                                    activity,
                                    analyticsList,
                                    this@ExpenseCategoryAnalyticsActivity
                                )
                                binding!!.rcvAnalysis.adapter = expenseCategoryAnalyticsAdapter
                                binding!!.rcvAnalysis.visibility = View.VISIBLE
                                binding!!.txtNoDataFound.visibility = View.GONE
                                binding!!.txtTryAgain.visibility = View.GONE
                                categoryDetailsAPICall()
                            } else if (Objects.requireNonNull(analyticsResponse)?.code == 999) {
                                logout(activity)
                            } else if (Objects.requireNonNull(analyticsResponse)?.code == 404) {
                                binding!!.txtAmount.setText(
                                    MySharedPreferences.getMySharedPreferences()?.currency
                                        .toString() + "0"
                                )
                                binding!!.tvProgress.text = "0%"
                                //  binding.progressBar.setProgress(0);
                                binding!!.rcvAnalysis.visibility = View.GONE
                                binding!!.txtNoDataFound.text = analyticsResponse!!.message
                                binding!!.txtNoDataFound.visibility = View.VISIBLE
                                binding!!.txtTryAgain.visibility = View.GONE
                            } else {
                                binding!!.rcvAnalysis.visibility = View.GONE
                                binding!!.txtNoDataFound.text = analyticsResponse!!.message
                                binding!!.txtNoDataFound.visibility = View.VISIBLE
                                binding!!.txtTryAgain.visibility = View.VISIBLE
                            }
                        } else {
                            binding!!.rcvAnalysis.visibility = View.GONE
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

    //Todo:: Get All Expense Paid by List
    private fun paymentMethodAPICall() {
        try {
            if (isConnectedToInternet(activity)) {
                val call: Call<PaymentMethodResponse>?
                call = RetrofitRestClient.instance?.getPaymentMethodAPICall(
                    MySharedPreferences.getMySharedPreferences()?.accessToken
                )
                if (call == null) return
                call.enqueue(object : Callback<PaymentMethodResponse?> {
                    override fun onResponse(
                        call: Call<PaymentMethodResponse?>,
                        response: Response<PaymentMethodResponse?>
                    ) {
                        val paymentMethodResponse: PaymentMethodResponse?
                        if (response.isSuccessful) {
                            paymentMethodResponse = response.body()
                            if (Objects.requireNonNull(paymentMethodResponse)?.code == 200) {
                                paidByList!!.clear()
                                paidByList!!.add(
                                    com.groot.app.iec.model.payment_method.DataItem(
                                        0.0,
                                        "",
                                        "",
                                        0,
                                        "",
                                        0,
                                        "All",
                                        "All",
                                        true
                                    )
                                )
                                paidByList!!.addAll(paymentMethodResponse!!.data!!)
                                if (paidByList != null && paidByList!!.size > 0) {
                                    binding!!.RCVExpense.layoutManager = LinearLayoutManager(
                                        activity,
                                        LinearLayoutManager.HORIZONTAL,
                                        false
                                    )
                                    expensePaidByListAdapter = ExpensePaidByListAdapter(
                                        activity,
                                        paidByList,
                                        this@ExpenseCategoryAnalyticsActivity,
                                        null
                                    )
                                    binding!!.RCVExpense.adapter = expensePaidByListAdapter
                                }
                            } else if (Objects.requireNonNull(paymentMethodResponse)?.code == 404) {
                                //   logout(getActivity());
                            } else if (Objects.requireNonNull(paymentMethodResponse)?.code == 999) {
                                logout(activity)
                            } else {
                                hideProgressDialog()
                                showSnackBar(activity, paymentMethodResponse!!.message)
                            }
                        } else {
                            showSnackBar(activity, response.message())
                        }
                    }

                    override fun onFailure(call: Call<PaymentMethodResponse?>, t: Throwable) {
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

    val incomeInClick: Unit
        get() {
            SpinnerAnalyticsListAPICall()
        }

    /**
     * Dialog Logout
     */
    private fun dialogDelete(APINAME: String, Id: String, position: Int) {
        AlertDialog.Builder(Objects.requireNonNull(activity))
            .setMessage(getString(R.string.are_you_delete))
            .setPositiveButton(getString(R.string.yes)) { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
                deleteExpenseAPICall(Id, position)
            }
            .setNegativeButton(getString(R.string.no)) { dialogInterface: DialogInterface, i: Int -> dialogInterface.dismiss() }
            .show()
    }

    /**
     * DELETE EXPENSE API CALLING
     */
    private fun deleteExpenseAPICall(Id: String, position: Int) {
        try {
            if (isConnectedToInternet(activity)) {
                showProgressDialog(activity)
                val params = HashMap<String?, String?>()
                params["id"] = Id
                val call: Call<BasicModelResponse>?
                call = RetrofitRestClient.instance?.deleteExpenseAPICall(
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
                                SpinnerAnalyticsListAPICall()
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

    override fun onResume() {
        super.onResume()
        if (RestConstant.UIS_Add_FINISH) {
            RestConstant.UIS_Add_FINISH = false
            SpinnerAnalyticsListAPICall()
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