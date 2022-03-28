package com.groot.app.iec.fragment

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.groot.app.iec.R
import com.groot.app.iec.adapter.ExpenseCategoryListAdapter
import com.groot.app.iec.adapter.ExpensePaidByListAdapter
import com.groot.app.iec.databinding.FragmentAddExpenseBinding
import com.groot.app.iec.model.category_list.CategoryListResponse
import com.groot.app.iec.model.category_list.DataItem
import com.groot.app.iec.model.payment_method.PaymentMethodResponse
import com.groot.app.iec.rest.RestConstant
import com.groot.app.iec.rest.RetrofitRestClient.instance
import com.groot.app.iec.utils.AppUtils.isConnectedToInternet
import com.groot.app.iec.utils.BaseFragment
import com.groot.app.iec.utils.MySharedPreferences
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import com.vincent.filepicker.Constant
import com.vincent.filepicker.activity.BaseActivity
import com.vincent.filepicker.activity.ImagePickActivity
import com.vincent.filepicker.filter.entity.ImageFile
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class AddExpenseFragment : BaseFragment() {
    private var binding: FragmentAddExpenseBinding? = null
    private var expenseCategoryListAdapter: ExpenseCategoryListAdapter? = null
    private var expensePaidByListAdapter: ExpensePaidByListAdapter? = null
    private var expenseCategoryList: ArrayList<DataItem>? = null
    private var paidByList: ArrayList<com.groot.app.iec.model.payment_method.DataItem>? = null

    //Todo:: For Date
    val myCalendar = Calendar.getInstance()
    private var ExpenseDate = ""
    private var CurrentDate = ""
    var list: ArrayList<ImageFile>? = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentAddExpenseBinding.inflate(inflater, container, false)
        expenseCategoryList = ArrayList()
        paidByList = ArrayList()
        binding!!.btnCamera.setOnClickListener { view: View? -> checkpermission(view) }
        if (RestConstant.UICIMAGEDATA != null && !RestConstant.UICIMAGEDATA.equals(
                "",
                ignoreCase = true
            )
        ) {
            binding!!.layoutImage.visibility = View.VISIBLE
            Glide.with(this).load(RestConstant.UICIMAGEDATA).into(binding!!.imageView1)
            RestConstant.UICIMAGEDATA = ""
        } else {
            binding!!.layoutImage.visibility = View.GONE
        }
        if (RestConstant.UPDATE_ITEM.equals("", ignoreCase = true)) {

            //Todo:: Set Current Date
            CurrentDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
            binding!!.txtSelectExpenseDate.text = CurrentDate
            RestConstant.EXTextDate = CurrentDate
            ExpenseDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            ExpenseDate = "$ExpenseDate 00:00:00"
            RestConstant.EXDate = ExpenseDate
        } else {
            ExpenseDate = RestConstant.UICDate
            binding!!.txtSelectExpenseDate.text = RestConstant.UICTextDate
            binding!!.edtAddNote.setText(RestConstant.UICNote)
        }
        binding!!.edtAddNote.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                RestConstant.EXNote = binding!!.edtAddNote.text.toString()
                RestConstant.UICNote = binding!!.edtAddNote.text.toString()
            }

            override fun afterTextChanged(editable: Editable) {}
        })


        //Todo:: Select Date from Date Picker
        setDate()

        //Todo:: Expense Category fetch from here...
        expenseCategoryListAPICall()
        binding!!.imgClose.setOnClickListener { view: View? ->
            binding!!.layoutImage.visibility = View.GONE
            list!!.clear()
            RestConstant.UICIMAGEDATA = ""
        }
        return binding!!.root
    }

    //Todo:: Select Date from Date Picker
    private fun setDate() {
        val expenseDate =
            OnDateSetListener { view, year, monthOfYear, dayOfMonth -> // TODO Auto-generated method stub
                myCalendar[Calendar.YEAR] = year
                myCalendar[Calendar.MONTH] = monthOfYear
                myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                updateLabel(binding!!.txtSelectExpenseDate)
                val myFormat = "yyyy-MM-dd" //In which you need put here
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                ExpenseDate = sdf.format(myCalendar.time) + " 00:00:00"
                RestConstant.EXDate = sdf.format(myCalendar.time) + " 00:00:00"
                RestConstant.UICDate = sdf.format(myCalendar.time) + " 00:00:00"
            }
        binding!!.txtSelectExpenseDate.setOnClickListener { // TODO Auto-generated method stub
            DatePickerDialog(
                requireActivity(),
                R.style.DialogThemeOrange,
                expenseDate,
                myCalendar[Calendar.YEAR],
                myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH]
            ).show()
        }
    }

    /*For Date picker*/
    private fun updateLabel(textView: AppCompatTextView) {
        val myFormat = "dd MMM yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        textView.text = sdf.format(myCalendar.time)
        textView.setTextColor(resources.getColor(R.color.white))
        RestConstant.EXTextDate = sdf.format(myCalendar.time)
        RestConstant.UICTextDate = sdf.format(myCalendar.time)
    }

    fun checkpermission(v: View?) {
        val permissions =
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
        val rationale = "Please provide location permission so that you can ..."
        val options = Permissions.Options()
            .setRationaleDialogTitle("Info")
            .setSettingsDialogTitle("Warning")
        Permissions.check(
            activity /*context*/,
            permissions,
            rationale,
            options,
            object : PermissionHandler() {
                override fun onGranted() {
                    val intent1 = Intent(activity, ImagePickActivity::class.java)
                    intent1.putExtra(ImagePickActivity.IS_NEED_CAMERA, true)
                    intent1.putExtra(Constant.MAX_NUMBER, 1)
                    intent1.putExtra(BaseActivity.IS_NEED_FOLDER_LIST, true)
                    startActivityForResult(intent1, Constant.REQUEST_CODE_PICK_IMAGE)
                }

                override fun onDenied(context: Context, deniedPermissions: ArrayList<String>) {
                    Toast.makeText(
                        activity,
                        "Please allow permission to access media from phone",
                        Toast.LENGTH_SHORT
                    ).show()
                    checkpermission(v)
                    // permission denied, block the feature.
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constant.REQUEST_CODE_PICK_IMAGE -> if (resultCode == Activity.RESULT_OK) {
                list = data!!.getParcelableArrayListExtra(Constant.RESULT_PICK_IMAGE)
                val builder = StringBuilder()
                for (file in list!!) {
                    val path = file.path
                    builder.append(
                        """
                            $path
                            
                            """.trimIndent()
                    )
                    Glide.with(requireActivity()).load(path).apply(RequestOptions())
                        .into(binding!!.imageView1)
                    binding!!.layoutImage.visibility = View.VISIBLE
                    RestConstant.EXImageData = list!![0].path
                    RestConstant.UICIMAGEDATA = list!![0].path
                }
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
                                expenseCategoryList!!.add(
                                    DataItem(
                                        "",
                                        "Add New",
                                        "",
                                        expenseCategoryList!!.size + 1,
                                        0,
                                        0,
                                        false
                                    )
                                )
                                if (RestConstant.UPDATE_ITEM.equals("", ignoreCase = true)) {
                                    if (expenseCategoryList != null && expenseCategoryList!!.size > 0) {
                                        binding!!.rcvCategory.layoutManager = LinearLayoutManager(
                                            activity, LinearLayoutManager.HORIZONTAL, false
                                        )
                                        expenseCategoryListAdapter = ExpenseCategoryListAdapter(
                                            requireActivity(),
                                            expenseCategoryList!!,
                                            this@AddExpenseFragment
                                        )
                                        binding!!.rcvCategory.adapter = expenseCategoryListAdapter
                                    }
                                } else {
                                    for (i in expenseCategoryList!!.indices) {
                                        if (RestConstant.UICCategory.equals(
                                                expenseCategoryList!![i].name,
                                                ignoreCase = true
                                            )
                                        ) {
                                            expenseCategoryList!![i].isSelect = true
                                            break
                                        }
                                    }
                                    if (expenseCategoryList != null && expenseCategoryList!!.size > 0) {
                                        binding!!.rcvCategory.layoutManager = LinearLayoutManager(
                                            activity, LinearLayoutManager.HORIZONTAL, false
                                        )
                                        expenseCategoryListAdapter = ExpenseCategoryListAdapter(
                                            requireActivity(),
                                            expenseCategoryList!!,
                                            this@AddExpenseFragment
                                        )
                                        binding!!.rcvCategory.adapter = expenseCategoryListAdapter
                                    }
                                }
                                //Todo:: Get All Expense Paid by List
                                paymentMethodAPICall()
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

    //Todo:: Get All Income Money In List
    private fun paymentMethodAPICall() {
        try {
            if (isConnectedToInternet(activity)) {
                showProgressDialog(activity)
                val call: Call<PaymentMethodResponse>
                call = instance!!.getPaymentMethodAPICall(
                    MySharedPreferences.getMySharedPreferences()?.accessToken
                )
                if (call == null) return
                call.enqueue(object : Callback<PaymentMethodResponse?> {
                    override fun onResponse(
                        call: Call<PaymentMethodResponse?>,
                        response: Response<PaymentMethodResponse?>
                    ) {
                        hideProgressDialog()
                        val paymentMethodResponse: PaymentMethodResponse?
                        if (response.isSuccessful) {
                            paymentMethodResponse = response.body()
                            if (Objects.requireNonNull(paymentMethodResponse)?.code == 200) {
                                paidByList!!.clear()
                                paidByList!!.addAll(paymentMethodResponse!!.data!!)
                                if (RestConstant.UPDATE_ITEM.equals("", ignoreCase = true)) {
                                    if (paidByList != null && paidByList!!.size > 0) {
                                        binding!!.rcvPaidBy.layoutManager = LinearLayoutManager(
                                            activity, LinearLayoutManager.HORIZONTAL, false
                                        )
                                        expensePaidByListAdapter = ExpensePaidByListAdapter(
                                            requireActivity(),
                                            paidByList!!,
                                            null,
                                            this@AddExpenseFragment
                                        )
                                        binding!!.rcvPaidBy.adapter = expensePaidByListAdapter
                                    }
                                } else {
                                    for (i in paidByList!!.indices) {
                                        if (RestConstant.UICMoneyIn.equals(
                                                paidByList!![i].title,
                                                ignoreCase = true
                                            )
                                        ) {
                                            paidByList!![i].isSelect = true
                                            break
                                        }
                                    }
                                    if (paidByList != null && paidByList!!.size > 0) {
                                        binding!!.rcvPaidBy.layoutManager = LinearLayoutManager(
                                            activity, LinearLayoutManager.HORIZONTAL, false
                                        )
                                        expensePaidByListAdapter = ExpensePaidByListAdapter(
                                            requireActivity(),
                                            paidByList!!,
                                            null,
                                            this@AddExpenseFragment
                                        )
                                        binding!!.rcvPaidBy.adapter = expensePaidByListAdapter
                                    }
                                }
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

    override fun onResume() {
        super.onResume()
        if (RestConstant.OPEN_ADD_EXPENSE_CATEGORY_SCREEN) {
            RestConstant.OPEN_ADD_EXPENSE_CATEGORY_SCREEN = false
            expenseCategoryListAPICall()
        }
    }

    fun paidByclick(position: Int) {
        binding!!.txtAvailableAmount.text =
            "Available: " + MySharedPreferences.getMySharedPreferences()?.currency +
                    paidByList!![position].amount
        binding!!.txtAvailableAmount.visibility = View.VISIBLE
        binding!!.ivDot.visibility = View.VISIBLE
    }
}