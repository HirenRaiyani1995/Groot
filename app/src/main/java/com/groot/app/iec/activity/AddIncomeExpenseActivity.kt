package com.groot.app.iec.activity

import com.groot.app.iec.utils.AppUtils.clearUpdateConstant
import com.groot.app.iec.utils.AppUtils.getText
import com.groot.app.iec.utils.AppUtils.isConnectedToInternet
import com.groot.app.iec.utils.AppUtils.getRequestBody
import android.os.Bundle
import com.groot.app.iec.R
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.text.InputFilter
import com.groot.app.iec.rest.RestConstant
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.groot.app.iec.fragment.AddIncomeFragment
import com.groot.app.iec.fragment.AddExpenseFragment
import androidx.fragment.app.FragmentPagerAdapter
import android.text.TextUtils
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.groot.app.iec.databinding.ActivityAddIncomeExpenseBinding
import com.groot.app.iec.model.BasicModelResponse
import okhttp3.RequestBody
import com.groot.app.iec.model.add_income.AddIncomeResponse
import com.groot.app.iec.rest.RetrofitRestClient
import com.groot.app.iec.model.add_expanse.AddExpanseResponse
import com.groot.app.iec.utils.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.lang.Exception
import java.util.*

class AddIncomeExpenseActivity : BaseActivity(), View.OnClickListener {
    private var binding: ActivityAddIncomeExpenseBinding? = null
    private var SelectedScreen: String? = ""
    private var incomeAmount: String? = ""
    private var id: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        super.onCreate(savedInstanceState)
        binding = ActivityAddIncomeExpenseBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)

        val intent = intent
        if (intent != null) {
            SelectedScreen = intent.getStringExtra("Select")
            incomeAmount = intent.getStringExtra("incomeAmount")
            id = intent.getStringExtra("id")
        }

        binding?.apply {
            edtAmount.requestFocus()
            edtAmount.filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(10, 2))
            btnBack.setOnClickListener(this@AddIncomeExpenseActivity)
            btnIncome.setOnClickListener(this@AddIncomeExpenseActivity)
            btnExpense.setOnClickListener(this@AddIncomeExpenseActivity)
            btnSave.setOnClickListener(this@AddIncomeExpenseActivity)
            btnDelete.setOnClickListener(this@AddIncomeExpenseActivity)
        }

        //Todo:: Clear Rest-constant
        clearRestConstant()
        setupViewPager(binding!!.viewPager)
        if (SelectedScreen.equals("Income", ignoreCase = true)) {
            binding?.apply {
                viewPager.currentItem = 0
                btnIncome.setBackgroundResource(R.color.facebook)
                btnExpense.setBackgroundResource(R.color.splace_bg)
                background.setBackgroundResource(R.color.facebook)
                btnSave.setBackgroundResource(R.color.facebook)
                btnDelete.visibility = View.GONE
            }
        } else if (SelectedScreen.equals("UpdateIncome", ignoreCase = true)) {
            binding?.apply { viewPager.currentItem = 0
                btnIncome.setBackgroundResource(R.color.facebook)
                btnExpense.setBackgroundResource(R.color.splace_bg)
                background.setBackgroundResource(R.color.facebook)
                btnSave.setBackgroundResource(R.color.facebook)
                btnDelete.visibility = View.VISIBLE
            }
        } else if (SelectedScreen.equals("UpdateExpense", ignoreCase = true)) {
            binding?.apply {
                viewPager.currentItem = 1
                btnIncome.setBackgroundResource(R.color.splace_bg)
                btnExpense.setBackgroundResource(R.color.expense_orange)
                background.setBackgroundResource(R.color.expense_orange)
                btnSave.setBackgroundResource(R.color.expense_orange)
                btnDelete.visibility = View.VISIBLE
            }
        } else {
            binding?.apply {
                viewPager.currentItem = 1
                btnIncome.setBackgroundResource(R.color.splace_bg)
                btnExpense.setBackgroundResource(R.color.expense_orange)
                background.setBackgroundResource(R.color.expense_orange)
                btnSave.setBackgroundResource(R.color.expense_orange)
                btnDelete.visibility = View.GONE
            }
        }
        try {
            if (SelectedScreen.equals("UpdateIncome", ignoreCase = true) ||
                SelectedScreen.equals("UpdateExpense", ignoreCase = true)
            ) {
                binding?.apply {
                    edtAmount.setText(incomeAmount)
                    text.text = "Update"
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("WrongConstant")
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnBack -> onBackPressed()
            R.id.btnIncome -> if (!SelectedScreen.equals("UpdateIncome", ignoreCase = true) &&
                !SelectedScreen.equals("UpdateExpense", ignoreCase = true)
            ) {
                binding?.apply {
                    edtAmount.setText("")
                    btnIncome.setBackgroundResource(R.color.facebook)
                    btnExpense.setBackgroundResource(R.color.splace_bg)
                    background.setBackgroundResource(R.color.facebook)
                    btnSave.setBackgroundResource(R.color.facebook)
                    viewPager.currentItem = 0
                }
            }
            R.id.btnExpense -> if (!SelectedScreen.equals("UpdateIncome", ignoreCase = true) &&
                !SelectedScreen.equals("UpdateExpense", ignoreCase = true)
            ) {
                binding?.apply {
                    edtAmount.setText("")
                    btnIncome.setBackgroundResource(R.color.splace_bg)
                    btnExpense.setBackgroundResource(R.color.expense_orange)
                    background.setBackgroundResource(R.color.expense_orange)
                    btnSave.setBackgroundResource(R.color.expense_orange)
                    viewPager.currentItem = 1
                }
            }
            R.id.btnSave ->
                if (binding!!.viewPager.currentItem == 0) {
                if (RestConstant.UPDATE_ITEM.equals("", ignoreCase = true)) {
                    if (addIncomeValidation()) {
                        binding!!.btnSave.isEnabled = false
                        addIncomeAPICall()
                    }
                } else {
                    if (updateIncomeValidation()) {
                        binding!!.btnSave.isEnabled = false
                        updateIncomeAPICall()
                    }
                }
            } else {
                if (RestConstant.UPDATE_ITEM.equals("", ignoreCase = true)) {
                    if (addExpenseValidation()) {
                        binding!!.btnSave.isEnabled = false
                        addExpanseAPICall()
                    }
                } else {
                    if (updateExpenseValidation()) {
                        binding!!.btnSave.isEnabled = false
                        updateExpanseAPICall()
                    }
                }
            }
            R.id.btnDelete -> {
                if (SelectedScreen.equals("Income", ignoreCase = true)) {
                    dialogDelete(
                        "INCOME",
                        id!!
                    )
                } else {
                    dialogDelete(
                        "EXPENSE",
                      id!!
                    )
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        clearUpdateConstant()
        Animatoo.animateSlideDown(activity)
    }

    private fun setupViewPager(viewPager: NonSwipeableViewPager) {
        val adapter = ViewPagerAdapter(
            supportFragmentManager
        )
        adapter.addFragment(AddIncomeFragment(), "Income")
        adapter.addFragment(AddExpenseFragment(), "Expense")
        viewPager.adapter = adapter
    }

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

        override fun getPageTitle(position: Int): CharSequence {
            return mFragmentTitleList[position]
        }
    }

    /*Add Income Validation*/
    private fun addIncomeValidation(): Boolean {
        if (TextUtils.isEmpty(getText(binding!!.edtAmount))) {
            showSnackBar(activity, "Please enter Amount")
            return false
        }
        if (TextUtils.isEmpty(RestConstant.ICDate)) {
            showSnackBar(activity, "Please select Date")
            return false
        }
        if (TextUtils.isEmpty(RestConstant.ICCategory)) {
            showSnackBar(activity, "Please select Category")
            return false
        }
        if (TextUtils.isEmpty(RestConstant.ICMoneyIn)) {
            showSnackBar(activity, "Please select Money In")
            return false
        }
        return true
    }

    private fun updateIncomeValidation(): Boolean {
        if (TextUtils.isEmpty(getText(binding!!.edtAmount))) {
            showSnackBar(activity, "Please enter Amount")
            return false
        }
        if (TextUtils.isEmpty(RestConstant.UICDate)) {
            showSnackBar(activity, "Please select Date")
            return false
        }
        if (TextUtils.isEmpty(RestConstant.UICCategory)) {
            showSnackBar(activity, "Please select Category")
            return false
        }
        if (TextUtils.isEmpty(RestConstant.UICMoneyIn)) {
            showSnackBar(activity, "Please select Money In")
            return false
        }
        return true
    }

    /*Add Expense Validation*/
    private fun addExpenseValidation(): Boolean {
        if (TextUtils.isEmpty(getText(binding!!.edtAmount))) {
            showSnackBar(activity, "Please enter Amount")
            return false
        }
        if (TextUtils.isEmpty(RestConstant.EXDate)) {
            showSnackBar(activity, "Please select Date")
            return false
        }
        if (TextUtils.isEmpty(RestConstant.EXCategory)) {
            showSnackBar(activity, "Please select Category")
            return false
        }
        if (TextUtils.isEmpty(RestConstant.EXPaidBy)) {
            showSnackBar(activity, "Please select Paid By")
            return false
        }
        return true
    }

    private fun updateExpenseValidation(): Boolean {
        if (TextUtils.isEmpty(getText(binding!!.edtAmount))) {
            showSnackBar(activity, "Please enter Amount")
            return false
        }
        if (TextUtils.isEmpty(RestConstant.UICDate)) {
            showSnackBar(activity, "Please select Date")
            return false
        }
        if (TextUtils.isEmpty(RestConstant.UICCategory)) {
            showSnackBar(activity, "Please select Category")
            return false
        }
        if (TextUtils.isEmpty(RestConstant.UICMoneyIn)) {
            showSnackBar(activity, "Please select Paid By")
            return false
        }
        return true
    }

    /**
     * ADD INCOME API CALL
     */
    private fun addIncomeAPICall() {
        try {
            if (isConnectedToInternet(activity)) {
                showProgressDialog(activity)
                val params = HashMap<String, RequestBody>()
                params["bank"] = getRequestBody(RestConstant.ICMoneyIn)
                if (TextUtils.isEmpty(RestConstant.ICNote)) {
                    params["title"] = getRequestBody("No Description")
                } else {
                    params["title"] = getRequestBody(RestConstant.ICNote)
                }
                params["amount"] = getRequestBody(getText(binding!!.edtAmount))
                params["source_name"] = getRequestBody(RestConstant.ICCategory)
                params["date"] = getRequestBody(RestConstant.ICTextDate)
                params["date_in_format"] = getRequestBody(RestConstant.ICDate)
                var partbody1: MultipartBody.Part? = null

                if (RestConstant.ICImageData.isNotEmpty()) {
                    val file = File(RestConstant.ICImageData)
                    val reqFile1 = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                    partbody1 = MultipartBody.Part.createFormData("image", file.name, reqFile1)
                }

                val call: Call<AddIncomeResponse> = RetrofitRestClient.instance?.addIncomeAPICall(
                    params,
                    MySharedPreferences.getMySharedPreferences()?.accessToken,
                    partbody1
                ) ?: return
                call.enqueue(object : Callback<AddIncomeResponse?> {
                    override fun onResponse(
                        call: Call<AddIncomeResponse?>,
                        response: Response<AddIncomeResponse?>
                    ) {
                        hideProgressDialog()
                        val addIncomeResponse: AddIncomeResponse?
                        if (response.isSuccessful) {
                            addIncomeResponse = response.body()
                            if (Objects.requireNonNull(addIncomeResponse)?.code == 200) {
                                RestConstant.IS_Add_FINISH = true
                                onBackPressed()
                            } else if (Objects.requireNonNull(addIncomeResponse)?.code == 999) {
                                logout(activity)
                            } else {
                                hideProgressDialog()
                                showSnackBar(activity, addIncomeResponse!!.message)
                            }
                        } else {
                            showSnackBar(activity, response.message())
                        }
                    }

                    override fun onFailure(call: Call<AddIncomeResponse?>, t: Throwable) {
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

    //Todo:: Add Expense API Calling..
    private fun addExpanseAPICall() {
        try {
            if (isConnectedToInternet(activity)) {
                showProgressDialog(activity)
                val params = HashMap<String, RequestBody>()
                params["category"] = getRequestBody(RestConstant.EXCategory)
                if (TextUtils.isEmpty(RestConstant.EXNote)) {
                    params["title"] = getRequestBody("No Description")
                } else {
                    params["title"] = getRequestBody(RestConstant.EXNote)
                }
                params["amount"] = getRequestBody(getText(binding!!.edtAmount))
                params["bank"] = getRequestBody(RestConstant.EXPaidBy)
                params["date"] = getRequestBody(RestConstant.EXTextDate)
                params["date_in_format"] = getRequestBody(RestConstant.EXDate)
                var partbody1: MultipartBody.Part? = null
                if (RestConstant.EXImageData.length > 2) {
                    val file = File(RestConstant.EXImageData)
                    val reqFile1 = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                    partbody1 = MultipartBody.Part.createFormData("image", file.name, reqFile1)
                }
                val call: Call<AddExpanseResponse>?
                call = RetrofitRestClient.instance?.addExpanseAPICall(
                    params,
                    MySharedPreferences.getMySharedPreferences()?.accessToken,
                    partbody1
                )
                if (call == null) return
                call.enqueue(object : Callback<AddExpanseResponse?> {
                    override fun onResponse(
                        call: Call<AddExpanseResponse?>,
                        response: Response<AddExpanseResponse?>
                    ) {
                        hideProgressDialog()
                        val addExpanseResponse: AddExpanseResponse?
                        if (response.isSuccessful) {
                            addExpanseResponse = response.body()
                            if (Objects.requireNonNull(addExpanseResponse)?.code == 200) {
                                RestConstant.IS_Add_FINISH = true
                                onBackPressed()
                            } else if (Objects.requireNonNull(addExpanseResponse)?.code == 999) {
                                logout(activity)
                            } else {
                                hideProgressDialog()
                                showSnackBar(activity, addExpanseResponse!!.message)
                            }
                        } else {
                            showSnackBar(activity, response.message())
                        }
                    }

                    override fun onFailure(call: Call<AddExpanseResponse?>, t: Throwable) {
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
     * UPDATE INCOME API CALL
     */
    private fun updateIncomeAPICall() {
        try {
            if (isConnectedToInternet(activity)) {
                showProgressDialog(activity)
                val params = HashMap<String, RequestBody>()
                params["id"] = getRequestBody(RestConstant.UCLICK_ID)
                params["bank"] = getRequestBody(RestConstant.UICMoneyIn)
                if (TextUtils.isEmpty(RestConstant.UICNote)) {
                    params["title"] = getRequestBody("No Description")
                } else {
                    params["title"] = getRequestBody(RestConstant.UICNote)
                }
                params["amount"] = getRequestBody(getText(binding!!.edtAmount))
                params["source_name"] = getRequestBody(RestConstant.UICCategory)
                params["date"] = getRequestBody(RestConstant.UICTextDate)
                params["date_in_format"] = getRequestBody(RestConstant.UICDate)

                var partbody1: MultipartBody.Part? = null
                if (RestConstant.UICIMAGEDATA.length > 2) {
                    val file = File(RestConstant.UICIMAGEDATA)
                    val reqFile1 = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                    partbody1 = MultipartBody.Part.createFormData("image", file.name, reqFile1)
                }
                val call: Call<AddIncomeResponse>?
                call = RetrofitRestClient.instance?.updateIncomeAPICall(
                    params,
                    MySharedPreferences.getMySharedPreferences()?.accessToken,
                    partbody1
                )
                if (call == null) return
                call.enqueue(object : Callback<AddIncomeResponse?> {
                    override fun onResponse(
                        call: Call<AddIncomeResponse?>,
                        response: Response<AddIncomeResponse?>
                    ) {
                        hideProgressDialog()
                        val addIncomeResponse: AddIncomeResponse?
                        if (response.isSuccessful) {
                            addIncomeResponse = response.body()
                            if (Objects.requireNonNull(addIncomeResponse)?.code == 200) {
                                RestConstant.UIS_Add_FINISH = true
                                onBackPressed()
                            } else if (Objects.requireNonNull(addIncomeResponse)?.code == 999) {
                                logout(activity)
                            } else {
                                hideProgressDialog()
                                showSnackBar(activity, addIncomeResponse!!.message)
                            }
                        } else {
                            showSnackBar(activity, response.message())
                        }
                    }

                    override fun onFailure(call: Call<AddIncomeResponse?>, t: Throwable) {
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
     * UPDATE EXPENSE API CALL
     */
    private fun updateExpanseAPICall() {
        try {
            if (isConnectedToInternet(activity)) {
                showProgressDialog(activity)
                val params = HashMap<String, RequestBody>()
                params["id"] = getRequestBody(RestConstant.UCLICK_ID)
                params["bank"] = getRequestBody(RestConstant.UICMoneyIn)
                if (TextUtils.isEmpty(RestConstant.UICNote)) {
                    params["title"] = getRequestBody("No Description")
                } else {
                    params["title"] = getRequestBody(RestConstant.UICNote)
                }
                params["amount"] = getRequestBody(getText(binding!!.edtAmount))
                params["category"] = getRequestBody(RestConstant.UICCategory)
                params["date"] = getRequestBody(RestConstant.UICTextDate)
                params["date_in_format"] = getRequestBody(RestConstant.UICDate)
                var partbody1: MultipartBody.Part? = null
                if (RestConstant.UICIMAGEDATA.length > 2) {
                    val file = File(RestConstant.UICIMAGEDATA)
                    val reqFile1 = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                    partbody1 = MultipartBody.Part.createFormData("image", file.name, reqFile1)
                }
                val call: Call<AddExpanseResponse>?
                call = RetrofitRestClient.instance?.updateExpanseAPICall(
                    params,
                    MySharedPreferences.getMySharedPreferences()?.accessToken,
                    partbody1
                )
                if (call == null) return
                call.enqueue(object : Callback<AddExpanseResponse?> {
                    override fun onResponse(
                        call: Call<AddExpanseResponse?>,
                        response: Response<AddExpanseResponse?>
                    ) {
                        hideProgressDialog()
                        val addExpanseResponse: AddExpanseResponse?
                        if (response.isSuccessful) {
                            addExpanseResponse = response.body()
                            if (Objects.requireNonNull(addExpanseResponse)?.code == 200) {
                                RestConstant.UIS_Add_FINISH = true
                                onBackPressed()
                            } else if (Objects.requireNonNull(addExpanseResponse)?.code == 999) {
                                logout(activity)
                            } else {
                                hideProgressDialog()
                                showSnackBar(activity, addExpanseResponse!!.message)
                            }
                        } else {
                            showSnackBar(activity, response.message())
                        }
                    }

                    override fun onFailure(call: Call<AddExpanseResponse?>, t: Throwable) {
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
    private fun dialogDelete(APINAME: String, Id: String) {
        AlertDialog.Builder(Objects.requireNonNull(activity))
            .setMessage(getString(R.string.are_you_delete))
            .setPositiveButton(getString(R.string.yes)) { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
                if (APINAME.equals("INCOME", ignoreCase = true)) {
                    binding!!.btnSave.isEnabled = false
                    deleteIncomeAPICall(Id)
                } else {
                    binding!!.btnSave.isEnabled = false
                    deleteExpenseAPICall(Id)
                }
            }
            .setNegativeButton(getString(R.string.no)) { dialogInterface: DialogInterface, i: Int -> dialogInterface.dismiss() }
            .show()
    }

    /**
     * DELETE INCOME API CALLING
     */
    private fun deleteIncomeAPICall(Id: String) {
        try {
            if (isConnectedToInternet(activity)) {
                showProgressDialog(activity)
                val params = HashMap<String?, String?>()
                params["id"] = Id
                val call: Call<BasicModelResponse>?
                call = RetrofitRestClient.instance!!.deleteIncomeAPICall(
                    params,
                    MySharedPreferences.getMySharedPreferences()?.accessToken
                )
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
                                RestConstant.IS_Add_FINISH = true
                                onBackPressed()
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

    /**
     * DELETE EXPENSE API CALLING
     */
    private fun deleteExpenseAPICall(Id: String) {
        try {
            if (isConnectedToInternet(activity)) {
                showProgressDialog(activity)
                val params = HashMap<String?, String?>()
                params["id"] = Id
                val call: Call<BasicModelResponse>?
                call = RetrofitRestClient.instance!!.deleteExpenseAPICall(
                    params,
                    MySharedPreferences.getMySharedPreferences()?.accessToken
                )
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
                                RestConstant.IS_Add_FINISH = true
                                onBackPressed()
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