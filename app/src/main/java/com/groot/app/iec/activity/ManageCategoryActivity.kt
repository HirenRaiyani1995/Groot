package com.groot.app.iec.activity

import com.groot.app.iec.utils.AppUtils.getText
import com.groot.app.iec.utils.AppUtils.showToast
import com.groot.app.iec.utils.AppUtils.isConnectedToInternet
import android.os.Bundle
import com.groot.app.iec.R
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.content.IntentFilter
import android.text.TextWatcher
import android.text.Editable
import android.content.BroadcastReceiver
import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.groot.app.iec.fragment.ManageCategoryIncomeFragment
import com.groot.app.iec.fragment.ManageCategoryExpenseFragment
import androidx.fragment.app.FragmentPagerAdapter
import com.groot.app.iec.databinding.ActivityManageCategoriesBinding
import com.groot.app.iec.model.BasicModelResponse
import com.groot.app.iec.rest.RetrofitRestClient
import com.groot.app.iec.utils.MySharedPreferences
import com.groot.app.iec.model.add_category.AddCategoryResponse
import com.groot.app.iec.utils.BaseActivity
import com.groot.app.iec.utils.NonSwipeableViewPager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.*

class ManageCategoryActivity : BaseActivity(), View.OnClickListener {
    private var binding: ActivityManageCategoriesBinding? = null
    private var SCREEN: String? = ""
    private var APICall = "save"
    private var categoryName: String? = null
    private var categoryId: String? = null
    private var hideshow: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        super.onCreate(savedInstanceState)
        binding = ActivityManageCategoriesBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)

        with(binding!!){
            this.btnIncome.setBackgroundResource(R.color.facebook)
            this.btnExpense.setBackgroundResource(R.color.dark_blue)
        }

        val intent = intent
        if (intent != null) {
            SCREEN = intent.getStringExtra("SCREEN")
        }
        binding!!.btnBack.setOnClickListener(this)
        binding!!.btnIncome.setOnClickListener(this)
        binding!!.btnExpense.setOnClickListener(this)
        binding!!.btnSaveIncome.setOnClickListener(this)
        setupViewPager(binding!!.viewPager)

        if (SCREEN.equals("INCOME", ignoreCase = true)) {
            binding!!.btnIncome.setBackgroundResource(R.color.facebook)
            binding!!.background.setBackgroundResource(R.color.facebook)
            binding!!.btnExpense.setBackgroundResource(R.color.dark_blue)
            binding!!.txtSave.setTextColor(resources.getColor(R.color.facebook))
            binding!!.edtCategoryName.setHintTextColor(resources.getColor(R.color.off_white))
            binding!!.viewPager.currentItem = 0
        } else {
            binding!!.btnIncome.setBackgroundResource(R.color.dark_blue)
            binding!!.btnExpense.setBackgroundResource(R.color.expense_orange)
            binding!!.background.setBackgroundResource(R.color.expense_orange)
            binding!!.txtSave.setTextColor(resources.getColor(R.color.expense_orange))
            binding!!.edtCategoryName.setHintTextColor(resources.getColor(R.color.off_white))
            binding!!.viewPager.currentItem = 1
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
        binding!!.edtCategoryName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (binding!!.edtCategoryName.text.toString().equals("", ignoreCase = true)) {
                    binding!!.txtSave.text = "Save"
                    APICall = "save"
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    /*FOR PASS DATA BETWEEN TWO FRAGMENTS*/
    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            categoryName = intent.getStringExtra("categoryName")
            categoryId = intent.getStringExtra("categoryId")
            hideshow = intent.getStringExtra("hideshow")
            binding!!.edtCategoryName.setText(categoryName)
            binding!!.txtSave.text = "Update"
            APICall = "update"
            binding?.edtCategoryName?.requestFocus()
            categoryName?.length?.let { binding?.edtCategoryName?.setSelection(it) }
            val imm: InputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding?.edtCategoryName, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    @SuppressLint("WrongConstant")
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnBack -> onBackPressed()
            R.id.btnIncome -> {
                APICall = "save"
                binding?.edtCategoryName?.setText("")
                binding!!.txtSave.text = "Save"
                binding!!.btnIncome.setBackgroundResource(R.color.facebook)
                binding!!.background.setBackgroundResource(R.color.facebook)
                binding!!.btnExpense.setBackgroundResource(R.color.dark_blue)
                binding!!.txtSave.setTextColor(resources.getColor(R.color.facebook))
                binding!!.edtCategoryName.setHintTextColor(resources.getColor(R.color.off_white))
                binding!!.viewPager.currentItem = 0
            }
            R.id.btnExpense -> {
                APICall = "save"
                binding?.edtCategoryName?.setText("")
                binding!!.txtSave.text = "Save"
                binding!!.btnIncome.setBackgroundResource(R.color.dark_blue)
                binding!!.btnExpense.setBackgroundResource(R.color.expense_orange)
                binding!!.background.setBackgroundResource(R.color.expense_orange)
                binding!!.txtSave.setTextColor(resources.getColor(R.color.expense_orange))
                binding!!.edtCategoryName.setHintTextColor(resources.getColor(R.color.off_white))
                binding!!.viewPager.currentItem = 1
            }
            R.id.btnSaveIncome -> if (binding!!.viewPager.currentItem == 0) {
                if (TextUtils.isEmpty(getText(binding!!.edtCategoryName))) {
                    showToast(activity, getString(R.string.please_enter_income_category_name))
                } else {
                    if (APICall.equals("save", ignoreCase = true)) {
                        addIncomeCategoryAPICall()
                    } else {
                        incomeCategoryListAPICall(categoryId, hideshow)
                    }
                }
            } else {
                if (TextUtils.isEmpty(getText(binding!!.edtCategoryName))) {
                    showToast(activity, getString(R.string.please_enter_expense_category_name))
                } else {
                    if (APICall.equals("save", ignoreCase = true)) {
                        addExpenseCategoryAPICall()
                    } else {
                        expenseCategoryListAPICall(categoryId, hideshow)
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Animatoo.animateSlideRight(activity)
    }

    private fun setupViewPager(viewPager: NonSwipeableViewPager) {
        val adapter = ViewPagerAdapter(
            supportFragmentManager
        )
        adapter.addFragment(ManageCategoryIncomeFragment(), "Income")
        adapter.addFragment(ManageCategoryExpenseFragment(), "Expense")
        viewPager.adapter = adapter
    }

    /* adapter.addFragment(new GasFragment(), "Cylinder");*/
    internal class ViewPagerAdapter(manager: FragmentManager?) : FragmentPagerAdapter(
        manager!!
    ) {
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

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }

    /**
     * ADD Income Category API CALL
     */
    private fun addIncomeCategoryAPICall() {
        try {
            if (isConnectedToInternet(activity)) {
                showProgressDialog(activity)
                val params = HashMap<String, String>()
                params["name"] = getText(binding!!.edtCategoryName)
                val call: Call<BasicModelResponse>?
                call = RetrofitRestClient.instance?.addSourceAPICall(
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
                                binding!!.edtCategoryName.setText("")
                                /*FOR PASS DATA BETWEEN TWO FRAGMENTS*/
                                val i1 = Intent("Refresh")
                                i1.putExtra("API", "INCOME")
                                LocalBroadcastManager.getInstance(activity).sendBroadcast(i1)
                                showSnackBar(activity, basicModelResponse.message)
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

    /**
     * ADD Expense CATEGORY API CALL
     */
    private fun addExpenseCategoryAPICall() {
        try {
            if (isConnectedToInternet(activity)) {
                showProgressDialog(activity)
                val params = HashMap<String, String>()
                params["name"] = getText(binding!!.edtCategoryName)
                val call: Call<AddCategoryResponse>?
                call = RetrofitRestClient.instance?.addCategoryAPICall(
                    params,
                    MySharedPreferences.getMySharedPreferences()?.accessToken
                )
                if (call == null) return
                call.enqueue(object : Callback<AddCategoryResponse?> {
                    override fun onResponse(
                        call: Call<AddCategoryResponse?>,
                        response: Response<AddCategoryResponse?>
                    ) {
                        hideProgressDialog()
                        val addCategoryResponse: AddCategoryResponse?
                        if (response.isSuccessful) {
                            addCategoryResponse = response.body()
                            if (Objects.requireNonNull(addCategoryResponse)?.code == 200) {
                                binding!!.edtCategoryName.setText("")
                                /*FOR PASS DATA BETWEEN TWO FRAGMENTS*/
                                val i1 = Intent("Refresh")
                                i1.putExtra("API", "EXPENSE")
                                LocalBroadcastManager.getInstance(activity).sendBroadcast(i1)
                                showSnackBar(activity, addCategoryResponse!!.message)
                            } else if (Objects.requireNonNull(addCategoryResponse)?.code == 999) {
                                logout(activity)
                            } else {
                                hideProgressDialog()
                                showSnackBar(activity, addCategoryResponse!!.message)
                            }
                        } else {
                            showSnackBar(activity, response.message())
                        }
                    }

                    override fun onFailure(call: Call<AddCategoryResponse?>, t: Throwable) {
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

    //Todo:: Hide Show API Calling
    private fun incomeCategoryListAPICall(id: String?, hideshow: String?) {
        try {
            if (isConnectedToInternet(activity)) {
                showProgressDialog(activity)
                val params = HashMap<String?, String?>()
                params["id"] = id
                params["name"] = binding!!.edtCategoryName.text.toString()
                params["is_hide"] = hideshow
                val call: Call<BasicModelResponse>?
                call = RetrofitRestClient.instance?.updateIncomeCategoryAPICall(
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
                                binding!!.edtCategoryName.setText("")
                                binding!!.txtSave.text = "Save"
                                val i1 = Intent("RefreshIntent")
                                i1.putExtra("isRefresh", "y")
                                LocalBroadcastManager.getInstance(activity).sendBroadcast(i1)
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

    //Todo:: Hide Show API Calling
    private fun expenseCategoryListAPICall(id: String?, flag: String?) {
        try {
            if (isConnectedToInternet(activity)) {
                showProgressDialog(activity)
                val params = HashMap<String?, String?>()
                params["id"] = id
                params["name"] = binding!!.edtCategoryName.text.toString()
                params["is_hide"] = flag
                val call: Call<BasicModelResponse>?
                call = RetrofitRestClient.instance?.updateExpenseCategoryAPICall(
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
                                binding!!.edtCategoryName.setText("")
                                binding!!.txtSave.text = "Save"
                                val i1 = Intent("RefreshIntent")
                                i1.putExtra("isRefresh", "y")
                                LocalBroadcastManager.getInstance(activity).sendBroadcast(i1)
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