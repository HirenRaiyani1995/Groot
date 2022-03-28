package com.groot.app.iec.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.groot.app.iec.BuildConfig
import com.groot.app.iec.R
import com.groot.app.iec.adapter.HomeHistoryListAdapter
import com.groot.app.iec.adapter.HomeScreenHorizontalListAdapter
import com.groot.app.iec.databinding.ActivityDashboardBinding
import com.groot.app.iec.model.BasicModelResponse
import com.groot.app.iec.model.add_bank.AddBankResponse
import com.groot.app.iec.model.analytics.AnalyticsResponse
import com.groot.app.iec.model.card_hide_show.CardHideShowResponse
import com.groot.app.iec.model.payment_method.DataItem
import com.groot.app.iec.model.payment_method.PaymentMethodResponse
import com.groot.app.iec.rest.RestConstant
import com.groot.app.iec.rest.RetrofitRestClient.instance
import com.groot.app.iec.utils.*
import com.groot.app.iec.utils.AppUtils.clearUpdateConstant
import com.groot.app.iec.utils.AppUtils.doubleToStringNoDecimal
import com.groot.app.iec.utils.AppUtils.getText
import com.groot.app.iec.utils.AppUtils.isConnectedToInternet
import com.groot.app.iec.utils.AppUtils.showToast
import com.groot.app.iec.utils.swipableRecyclerview.RecyclerTouchListener
import com.groot.app.iec.utils.swipableRecyclerview.RecyclerTouchListener.OnRowClickListener
import com.groot.app.iec.utils.swipableRecyclerview.RecyclerTouchListener.OnSwipeOptionsClickListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import com.groot.app.iec.dialog.VideoDialog
import com.groot.app.iec.model.vidoes.Datum
import com.groot.app.iec.model.vidoes.VideosResponse
import com.groot.app.iec.rest.RetrofitRestClient
import kotlin.collections.ArrayList
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.tasks.Task
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubeStandalonePlayer
import com.google.android.youtube.player.YouTubeThumbnailLoader
import com.google.android.youtube.player.YouTubeThumbnailView
import com.groot.app.iec.model.googleAds.AdmobAdsResponse
import java.util.regex.Pattern


class DashboardActivity : BaseActivity(), View.OnClickListener{
    private var binding: ActivityDashboardBinding? = null
    private var homeScreenHorizontalListAdapter: HomeScreenHorizontalListAdapter? = null
    private var horizontalListNew: ArrayList<DataItem>? = null
    private var horizontalList: ArrayList<DataItem>? = null
    private var homeHistoryListAdapter: HomeHistoryListAdapter? = null
    private var dialogAddBank: View? = null
    private var dialogCreateNewCard: View? = null
    private var incomeSourceList: ArrayList<com.groot.app.iec.model.income_source.DataItem>? = null
    private var analyticsList: ArrayList<com.groot.app.iec.model.analytics.DataItem>? = null
    private var EditBankId = ""
    private var totalBalance = 0.0
    private var touchListener: RecyclerTouchListener? = null

    private var VideoId = ""
    private var readyForLoadingYoutubeThumbnail = true


    private var videoList: ArrayList<Datum>? = ArrayList()
    private var adsList: ArrayList<com.groot.app.iec.model.googleAds.Datum>? = ArrayList()

    var videoDialog: VideoDialog?= null

    private var videolink = ""

    private var reviewManager: ReviewManager? = null

    private var key1 = "AIzaSyD8Tlyo"
    private var key2 = "OYMHbTXHPNSu"
    private var key3 = "K7VjXKd"
    private var key4 = "UW9ZJc"

    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        videoDialog = VideoDialog(activity)

        showRateApp()

        /*All Dialog initialised*/dialogAddBank = findViewById(R.id.dialogAddBank)
        dialogCreateNewCard = findViewById(R.id.dialogCreateNewCard)

        /*All Arraylist initialization*/horizontalList = ArrayList()
        horizontalListNew = ArrayList()
        incomeSourceList = ArrayList()
        analyticsList = ArrayList()

        //getVideoListAPICall()

        if(MySharedPreferences.getMySharedPreferences()?.isFirstTimeLoginVideoShow == true){
            MySharedPreferences.getMySharedPreferences()?.isFirstTimeLoginVideoShow = false
            getVideoListAPICall()
            videoDialog?.apply {
                btnClose.setOnClickListener {
                    dismiss()
                }
                btnYoutubePlay.setOnClickListener {
                    val intent = YouTubeStandalonePlayer.createVideoIntent(
                        activity,
                        key1 + key2 + key3 + key4,
                        VideoId,  //video id
                        100,  //after this time, video will start automatically
                        true,  //autoplay or not
                        true
                    ) //lightbox mode or not; show the video in a small box

                    startActivity(intent)
                }
            }
        }

        /*All On-click Mehtods*/
        binding?.apply {
            layoutDrawer.btnManageCategory.setOnClickListener(this@DashboardActivity)
            btnAllTransaction.setOnClickListener(this@DashboardActivity)
            btnNewExpence.setOnClickListener(this@DashboardActivity)
            btnNewExpence.setOnClickListener(this@DashboardActivity)
            btnNewIncome.setOnClickListener(this@DashboardActivity)
            txtTryAgain.setOnClickListener(this@DashboardActivity)
            layoutDrawer.btnLogout.setOnClickListener(this@DashboardActivity)
            ivProfilePic.setOnClickListener(this@DashboardActivity)
            btnPieChart.setOnClickListener(this@DashboardActivity)
            layoutDrawer.btnDrawerBack.setOnClickListener(this@DashboardActivity)
            layoutDrawer.btnDrawerNotifications.setOnClickListener(this@DashboardActivity)
            layoutDrawer.btnHowItWorks.setOnClickListener(this@DashboardActivity)
            layoutDrawer.btnAboutUs.setOnClickListener(this@DashboardActivity)
            layoutDrawer.btnManageCard.setOnClickListener(this@DashboardActivity)
            layoutDrawer.btnPrivacyPolicy.setOnClickListener(this@DashboardActivity)
            layoutDrawer.btnTermsAndConditions.setOnClickListener(this@DashboardActivity)
            layoutDrawer.btnContactUs.setOnClickListener(this@DashboardActivity)
            dialogCreateNewCard.edtAmount.filters =
                arrayOf<InputFilter>(DecimalDigitsInputFilter(10, 2))
        }

        //Todo:: Load Interstitial Ads
        showInterstitialAds(this)

        //Drawer Clicks
        binding!!.layoutDrawer.btnSettings.setOnClickListener(this)

        /*Drawer share button click*/
        binding!!.layoutDrawer.btnShare.setOnClickListener {
            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Groot")
                var shareMessage = "\nLet me recommend you this application\n\n"
                shareMessage = """
                    ${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
                    
                    
                    """.trimIndent()
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(shareIntent, "choose one"))
            } catch (e: Exception) {
                //e.toString();
            }
        }
        binding!!.dialogAddBank.btnSaveAddCard.setOnClickListener {
            hideKeyBoard(activity)
            if (addBankValidation()) {
                updateBankAPICall()
            }
        }
        binding!!.dialogCreateNewCard.btnSaveAddCard.setOnClickListener {
            hideKeyBoard(activity)
            if (createNewCardValidation()) {
                addBankAPICall("0")
            }
        }

        /*All API Calling*/
        paymentMethodAPICall()

        /*For Swipe Recycler view to show an option*/touchListener =
            RecyclerTouchListener(activity, binding!!.RCVPaidHistory)
        touchListener!!
            .setClickable(object : OnRowClickListener {
                override fun onRowClicked(position: Int) {}
                override fun onIndependentViewClicked(independentViewID: Int, position: Int) {}
            })
            .setSwipeOptionViews(R.id.delete_task, R.id.edit_task)
            .setSwipeable(R.id.rowFG, R.id.rowBG, object : OnSwipeOptionsClickListener {
                override fun onSwipeOptionClicked(viewID: Int, position: Int) {
                    if (!analyticsList!![position].category.equals("Opening", ignoreCase = true)) {
                        when (viewID) {
                            R.id.delete_task -> if (analyticsList!![position].type.equals(
                                    "income",
                                    ignoreCase = true
                                )
                            ) {
                                dialogDelete(
                                    "INCOME",
                                    analyticsList!![position].id.toString(),
                                    position
                                )
                            } else {
                                dialogDelete(
                                    "EXPENSE",
                                    analyticsList!![position].id.toString(),
                                    position
                                )
                            }
                            R.id.edit_task -> if (analyticsList!![position].type.equals(
                                    "income",
                                    ignoreCase = true
                                )
                            ) {
                                clearUpdateConstant()
                                RestConstant.UCLICK_ID = analyticsList!![position].id.toString()
                                RestConstant.UICDate = analyticsList!![position].updatedAt!!
                                RestConstant.UICCategory = analyticsList!![position].category!!
                                RestConstant.UICMoneyIn = analyticsList!![position].bankId!!
                                val incomeAmount = analyticsList!![position].amount.toString()
                                RestConstant.UICNote = analyticsList!![position].title!!
                                RestConstant.UICTextDate = analyticsList!![position].date!!
                                RestConstant.UICIMAGEDATA = analyticsList!![position].image!!
                                RestConstant.UPDATE_ITEM = "UpdateIncome"
                                val i1 = Intent(activity, AddIncomeExpenseActivity::class.java)
                                i1.putExtra("Select", "UpdateIncome")
                                i1.putExtra("incomeAmount", incomeAmount)
                                i1.putExtra("id", analyticsList!![position].id.toString())
                                startActivity(i1)
                                Animatoo.animateSlideUp(activity)
                            } else {
                                clearUpdateConstant()
                                RestConstant.UCLICK_ID = analyticsList!![position].id.toString()
                                RestConstant.UICDate = analyticsList!![position].updatedAt!!
                                RestConstant.UICCategory = analyticsList!![position].category!!
                                RestConstant.UICMoneyIn = analyticsList!![position].bankId!!
                                val incomeAmount = analyticsList!![position].amount.toString()
                                RestConstant.UICNote = analyticsList!![position].title!!
                                RestConstant.UICTextDate = analyticsList!![position].date!!
                                RestConstant.UICIMAGEDATA = analyticsList!![position].image!!
                                RestConstant.UPDATE_ITEM = "UpdateExpense"
                                val i1 = Intent(activity, AddIncomeExpenseActivity::class.java)
                                i1.putExtra("Select", "UpdateExpense")
                                i1.putExtra("incomeAmount", incomeAmount)
                                i1.putExtra("id", analyticsList!![position].id.toString())
                                startActivity(i1)
                                Animatoo.animateSlideUp(activity)
                            }
                        }
                    } else {
                        showSnackBar(activity, "This is opening card and this card is not editable")
                    }
                }
            })
        binding!!.RCVPaidHistory.addOnItemTouchListener(touchListener!!)
    }

    private fun showRateApp() {
        reviewManager = ReviewManagerFactory.create(this)
        val request: Task<ReviewInfo> = reviewManager!!.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Getting the ReviewInfo object
                val reviewInfo: ReviewInfo = task.result
                val flow: Task<Void> = reviewManager!!.launchReviewFlow(this, reviewInfo)
                flow.addOnCompleteListener { task1 -> }
            }
        }
    }

    /**
     * Dialog Logout
     */
    private fun dialogDelete(APINAME: String, Id: String, position: Int) {
        AlertDialog.Builder(Objects.requireNonNull(activity))
            .setMessage(getString(R.string.are_you_delete))
            .setPositiveButton(getString(R.string.yes)) { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
                if (APINAME.equals("INCOME", ignoreCase = true)) {
                    deleteIncomeAPICall(Id, position)
                } else {
                    deleteExpenseAPICall(Id, position)
                }
            }
            .setNegativeButton(getString(R.string.no)) { dialogInterface: DialogInterface, i: Int -> dialogInterface.dismiss() }
            .show()
    }

    @SuppressLint("WrongConstant")
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnLogout -> dialogLogout()
            R.id.btnPieChart -> {
                AppUtils.clearConstant()
                startActivity(Intent(activity, PieChartActivity::class.java))
                Animatoo.animateSlideLeft(activity)
                binding!!.drawerLayout.closeDrawer(Gravity.START)
            }
            R.id.btnAllTransaction -> {
                AppUtils.clearConstant()
                val AllAnalyticsIntent = Intent(activity, AllAnalyticsActivity::class.java)
                startActivity(AllAnalyticsIntent)
                Animatoo.animateSlideLeft(activity)
            }
            R.id.btnNewExpence -> {
                val intent = Intent(activity, AddIncomeExpenseActivity::class.java)
                intent.putExtra("Select", "Expense")
                startActivity(intent)
                Animatoo.animateSlideUp(activity)
            }
            R.id.btnNewIncome -> {
                val incomeIntent = Intent(activity, AddIncomeExpenseActivity::class.java)
                incomeIntent.putExtra("Select", "Income")
                startActivity(incomeIntent)
                Animatoo.animateSlideUp(activity)
            }
            R.id.txtTryAgain -> analyticsListAPICall()
            R.id.ivProfilePic -> binding!!.drawerLayout.openDrawer(Gravity.START)
            R.id.btnDrawerBack -> binding!!.drawerLayout.closeDrawer(Gravity.START)
            R.id.btnSettings -> {
                startActivity(Intent(activity, SettingsActivity::class.java))
                Animatoo.animateSlideLeft(activity)
                binding!!.drawerLayout.closeDrawer(Gravity.START)
            }
            R.id.btnManageCard -> {
                startActivity(Intent(activity, ManageCardsActivity::class.java))
                Animatoo.animateSlideLeft(activity)
                binding!!.drawerLayout.closeDrawer(Gravity.START)
            }
            R.id.btnPrivacyPolicy -> {
                startActivity(Intent(activity, PrivacyPolicyActivity::class.java))
                Animatoo.animateSlideLeft(activity)
                binding!!.drawerLayout.closeDrawer(Gravity.START)
            }
            R.id.btnAboutUs -> {
                startActivity(Intent(activity, AboutUsActivity::class.java))
                Animatoo.animateSlideLeft(activity)
                binding!!.drawerLayout.closeDrawer(Gravity.START)
            }
            R.id.btnHowItWorks -> {
                startActivity(Intent(activity, HowItWorksActivity::class.java))
                Animatoo.animateSlideLeft(activity)
                binding!!.drawerLayout.closeDrawer(Gravity.START)
            }
            R.id.btnTermsAndConditions -> {
                startActivity(Intent(activity, TermsAndConditionsActivity::class.java))
                Animatoo.animateSlideLeft(activity)
                binding!!.drawerLayout.closeDrawer(Gravity.START)
            }
            R.id.btnContactUs -> {
                startActivity(Intent(activity, ContactUsActivity::class.java))
                Animatoo.animateSlideLeft(activity)
                binding!!.drawerLayout.closeDrawer(Gravity.START)
            }
            R.id.btnManageCategory -> {
                startActivity(
                    Intent(activity, ManageCategoryActivity::class.java)
                        .putExtra("SCREEN", "INCOME")
                )
                Animatoo.animateSlideLeft(activity)
                binding!!.drawerLayout.closeDrawer(Gravity.START)
            }
            R.id.btnDrawerNotifications -> {
            }
        }
    }

    override fun onBackPressed() {
        if (dialogAddBank!!.visibility == View.VISIBLE) {
            dialogAddBank!!.visibility = View.GONE
        } else if (dialogCreateNewCard!!.visibility == View.VISIBLE) {
            dialogCreateNewCard!!.visibility = View.GONE
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        //Todo:: Fetch Google Ads
        getGoogleAdsDataListAPICall()

        binding!!.RCVPaidHistory.addOnItemTouchListener(touchListener!!)
        binding!!.txtUserName.text = MySharedPreferences.getMySharedPreferences()?.userName
        binding!!.layoutDrawer.tvUsername.text = MySharedPreferences.getMySharedPreferences()?.userName
        try {
            val options1 = RequestOptions().placeholder(R.drawable.ic_user_pic)
            Glide.with(activity).load(MySharedPreferences.getMySharedPreferences()?.profile)
                .apply(options1).into(
                    binding!!.ivProfilePic
                )
            Glide.with(activity).load(MySharedPreferences.getMySharedPreferences()?.profile)
                .apply(options1).into(
                    binding!!.layoutDrawer.profileImage
                )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (RestConstant.IS_Add_FINISH || RestConstant.UIS_Add_FINISH) {
            RestConstant.IS_Add_FINISH = false
            RestConstant.UIS_Add_FINISH = false
            paymentMethodAPICall()
        }
    }

    fun cardAddButtonClick() {
        binding!!.dialogCreateNewCard.txtAddTitle.setText("")
        binding!!.dialogCreateNewCard.edtAmount.setText("")
        dialogCreateNewCard!!.visibility = View.VISIBLE
    }

    fun openCardTransactionScreen(source: String){
        AppUtils.clearConstant()
        val intent = Intent(activity, CardTransactionActivity::class.java)
        intent.putExtra("source",source)
        startActivity(intent)
        Animatoo.animateSlideLeft(activity)
    }

    fun AddButtonClick() {
        val intent = Intent(activity, AddIncomeExpenseActivity::class.java)
        intent.putExtra("Select", "Income")
        startActivity(intent)
        Animatoo.animateSlideUp(activity)
    }

    /*Add Bank Validation*/
    private fun addBankValidation(): Boolean {
        if (TextUtils.isEmpty(getText(binding!!.dialogAddBank.txtAddTitle))) {
            showSnackBar(activity, "Please Add Title")
            return false
        }
        return true
    }

    //Todo:: Create new card validation
    private fun createNewCardValidation(): Boolean {
        if (TextUtils.isEmpty(getText(binding!!.dialogCreateNewCard.txtAddTitle))) {
            showSnackBar(activity, "Please enter Card name")
            return false
        }
      /*  if (TextUtils.isEmpty(getText(binding!!.dialogCreateNewCard.edtAmount))) {
            showSnackBar(activity, "Please enter Amount")
            return false
        }*/
        return true
    }

    fun editCard(source: String?) {
        /*try {
            EditBankId = horizontalList!![position!!].id.toString()
            binding!!.dialogAddBank.txtAddTitle.setText(horizontalList!![position].title)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        dialogAddBank!!.visibility = View.VISIBLE*/

        startActivity(Intent(activity, CardTransactionActivity::class.java).putExtra("source",source))
        Animatoo.animateSlideLeft(activity)
    }

    fun deleteCard(position: Int?) {
        if (horizontalList?.get(position!!)?.isHide === 0) {
            getHideShowButtonClick("1", horizontalList!![position!!].id.toString())
        } else {
            getHideShowButtonClick("0", horizontalList!![position!!].id.toString())
        }
    }

    /**
     * PAYMENT METHOD API CALLING
     */
    private fun paymentMethodAPICall() {
        try {
            if (isConnectedToInternet(activity)) {
                showProgressDialog(activity)
                val call: Call<PaymentMethodResponse>?
                call = instance?.getPaymentMethodAPICall(
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
                            if (paymentMethodResponse?.code == 200) {
                                horizontalList!!.clear()
                                horizontalListNew!!.clear()
                                horizontalListNew!!.addAll(paymentMethodResponse!!.data!!)
                                totalBalance = 0.00
                                try {
                                    if (horizontalListNew != null && horizontalListNew!!.size > 0) {
                                        for (i in horizontalListNew!!.indices) {
                                            if (horizontalListNew!![i].isHide === 0) {
                                                totalBalance += horizontalListNew!![i].amount
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                for (j in horizontalListNew!!.indices) {
                                    if (horizontalListNew!![j].isHide === 0) {
                                        horizontalList!!.add(horizontalListNew!![j])
                                    }
                                }
                                binding!!.RCVHorizontal.layoutManager = CenterZoomLayoutManager(
                                    activity,
                                    LinearLayoutManager.HORIZONTAL,
                                    false
                                )
                                homeScreenHorizontalListAdapter = HomeScreenHorizontalListAdapter(
                                    activity,
                                    horizontalList!!,
                                    totalBalance,
                                    this@DashboardActivity
                                )
                                binding!!.RCVHorizontal.adapter = homeScreenHorizontalListAdapter
                                analyticsListAPICall()
                            } else if (paymentMethodResponse?.code == 999) {
                                logout(activity)
                            } else {
                                hideProgressDialog()
                                showSnackBar(activity, paymentMethodResponse!!.message)
                            }
                        } else {
                            if (response.code() == 401) {
                                logout(activity)
                                showToast(activity, response.message())
                            } else {
                                showSnackBar(activity, response.message())
                            }
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

    /**
     * ADD BANK API CALL
     */
    private fun addBankAPICall(Amount: String) {
        try {
            if (isConnectedToInternet(activity)) {
                showProgressDialog(activity)
                val params = HashMap<String, String>()
                params["category"] = getText(binding!!.dialogCreateNewCard.txtAddTitle)
                params["title"] = getText(binding!!.dialogCreateNewCard.txtAddTitle)
                params["amount"] = Amount
                val call: Call<AddBankResponse>?
                call = instance!!.addBankAPICall(
                    params,
                     MySharedPreferences.getMySharedPreferences()?.accessToken
                )
                call.enqueue(object : Callback<AddBankResponse?> {
                    override fun onResponse(
                        call: Call<AddBankResponse?>,
                        response: Response<AddBankResponse?>
                    ) {
                        hideProgressDialog()
                        val addBankResponse: AddBankResponse?
                        if (response.isSuccessful) {
                            addBankResponse = response.body()
                            if (addBankResponse != null) {
                                if (addBankResponse.code == 200) {
                                    showSnackBar(activity, addBankResponse.message)
                                    dialogCreateNewCard!!.visibility = View.GONE
                                    paymentMethodAPICall()
                                } else if (Objects.requireNonNull(addBankResponse).code == 999) {
                                    logout(activity)
                                } else {
                                    hideProgressDialog()
                                    showSnackBar(activity, addBankResponse.message)
                                }
                            }
                        } else {
                            showSnackBar(activity, response.message())
                        }
                    }

                    override fun onFailure(call: Call<AddBankResponse?>, t: Throwable) {
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
     * UPDATE BANK API CALL
     */
    private fun updateBankAPICall() {
        try {
            if (isConnectedToInternet(activity)) {
                showProgressDialog(activity)
                val params = HashMap<String, String>()
                params["category"] = "Cash"
                params["title"] = getText(binding!!.dialogAddBank.txtAddTitle)
                //    params.put("amount", "0");
                params["id"] = EditBankId
                val call: Call<BasicModelResponse>?
                call = instance!!.updateBankAPICall(
                    params,
                     MySharedPreferences.getMySharedPreferences()?.accessToken
                )
                call.enqueue(object : Callback<BasicModelResponse?> {
                    override fun onResponse(
                        call: Call<BasicModelResponse?>,
                        response: Response<BasicModelResponse?>
                    ) {
                        hideProgressDialog()
                        val updateBankResponse: BasicModelResponse?
                        if (response.isSuccessful) {
                            updateBankResponse = response.body()
                            if (updateBankResponse?.code == 200) {
                                paymentMethodAPICall()
                                dialogAddBank!!.visibility = View.GONE
                            } else if (updateBankResponse?.code == 999) {
                                logout(activity)
                            } else {
                                hideProgressDialog()
                                showSnackBar(activity, updateBankResponse!!.message)
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
     * DELETE BANK API CALL
     */
    private fun deleteBankAPICall(DeleteBankId: String) {
        try {
            if (isConnectedToInternet(activity)) {
                showProgressDialog(activity)
                val params = HashMap<String, String>()
                params["id"] = DeleteBankId
                val call: Call<BasicModelResponse>?
                call = instance!!.deleteBankAPICall(
                    params,
                     MySharedPreferences.getMySharedPreferences()?.accessToken
                )
                call.enqueue(object : Callback<BasicModelResponse?> {
                    override fun onResponse(
                        call: Call<BasicModelResponse?>,
                        response: Response<BasicModelResponse?>
                    ) {
                        hideProgressDialog()
                        val updateBankResponse: BasicModelResponse?
                        if (response.isSuccessful) {
                            updateBankResponse = response.body()
                            if (updateBankResponse?.code == 200) {
                                paymentMethodAPICall()
                            } else if (updateBankResponse?.code == 999) {
                                logout(activity)
                            } else {
                                hideProgressDialog()
                                showSnackBar(activity, updateBankResponse!!.message)
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
     * ANALYTICS LIST API CALLING
     */
    private fun analyticsListAPICall() {
        try {
            if (isConnectedToInternet(activity)) {
                val params = HashMap<String?, String?>()
                params["date"] = AppUtils.getCurrentDate() + " " + AppUtils.getCurrentTime()
                val call: Call<AnalyticsResponse>?
                call = instance!!.getAnalyticsListAPICall(
                    params,
                     MySharedPreferences.getMySharedPreferences()?.accessToken
                )
                call.enqueue(object : Callback<AnalyticsResponse?> {
                    override fun onResponse(
                        call: Call<AnalyticsResponse?>,
                        response: Response<AnalyticsResponse?>
                    ) {
                        val analyticsResponse: AnalyticsResponse?
                        if (response.isSuccessful) {
                            analyticsResponse = response.body()
                            if (analyticsResponse?.code == 200) {
                                analyticsList!!.clear()
                                analyticsList!!.addAll(analyticsResponse.data!!)
                                if (analyticsResponse.incomeAmount.toString()
                                        .equals("0.0", ignoreCase = true)
                                ) {
                                    binding!!.txtTodayIncome.text = MySharedPreferences.getMySharedPreferences()?.currency
                                        .toString() + "0.00"
                                } else {
                                    binding!!.txtTodayIncome.text = MySharedPreferences.getMySharedPreferences()?.currency
                                        .toString() + doubleToStringNoDecimal(
                                        analyticsResponse.incomeAmount.toString()
                                    )
                                }
                                if (analyticsResponse.expenseAmount.toString()
                                        .equals("0.0", ignoreCase = true)
                                ) {
                                    binding!!.txtTodayExpence.text = MySharedPreferences.getMySharedPreferences()?.currency
                                        .toString() + "0.00"
                                } else {
                                    binding!!.txtTodayExpence.text = MySharedPreferences.getMySharedPreferences()?.currency
                                        .toString() + doubleToStringNoDecimal(
                                        analyticsResponse.expenseAmount.toString()
                                    )
                                }
                                binding!!.RCVPaidHistory.layoutManager = LinearLayoutManager(
                                    activity,
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                                homeHistoryListAdapter = HomeHistoryListAdapter(
                                    activity,
                                    analyticsList!!,
                                    this@DashboardActivity
                                )
                                binding!!.RCVPaidHistory.adapter = homeHistoryListAdapter
                                binding!!.RCVPaidHistory.visibility = View.VISIBLE
                                binding!!.txtNoDataFound.visibility = View.GONE
                                binding!!.txtTryAgain.visibility = View.GONE
                            } else if (analyticsResponse?.code == 999) {
                                logout(activity)
                            } else if (analyticsResponse?.code == 404) {
                                binding!!.txtTodayIncome.text = MySharedPreferences.getMySharedPreferences()?.currency
                                    .toString() + "0.00"
                                binding!!.txtTodayExpence.text = MySharedPreferences.getMySharedPreferences()?.currency
                                    .toString() + "0.00"
                                binding!!.RCVPaidHistory.visibility = View.GONE
                                binding!!.txtNoDataFound.text = analyticsResponse.message
                                binding!!.txtNoDataFound.visibility = View.VISIBLE
                                binding!!.txtTryAgain.visibility = View.GONE
                            } else {
                                binding!!.RCVPaidHistory.visibility = View.GONE
                                binding!!.txtNoDataFound.text = analyticsResponse!!.message
                                binding!!.txtNoDataFound.visibility = View.VISIBLE
                                binding!!.txtTryAgain.visibility = View.VISIBLE
                            }
                        } else {
                            binding!!.RCVPaidHistory.visibility = View.GONE
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
     * DELETE INCOME API CALLING
     */
    private fun deleteIncomeAPICall(Id: String, position: Int) {
        try {
            if (isConnectedToInternet(activity)) {
                showProgressDialog(activity)
                val params = HashMap<String?, String?>()
                params["id"] = Id
                val call: Call<BasicModelResponse>?
                call = instance!!.deleteIncomeAPICall(
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
                                paymentMethodAPICall()
                                analyticsListAPICall()
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
                call = instance!!.deleteExpenseAPICall(
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
                                paymentMethodAPICall()
                                analyticsListAPICall()
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

    /*On click delete source*/
    fun deleteSource(position: Int) {
        deleteSourceAPICall(position)
    }

    /*Delete Source API Calling*/
    private fun deleteSourceAPICall(position: Int) {
        try {
            if (isConnectedToInternet(activity)) {
                showProgressDialog(activity)
                val params = HashMap<String?, String?>()
                params["id"] = incomeSourceList!![position].id.toString()
                val call: Call<BasicModelResponse>?
                call = instance!!.deleteSourceAPICall(
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
                        val deleteCategoryResponse: BasicModelResponse?
                        if (response.isSuccessful) {
                            deleteCategoryResponse = response.body()
                            if (deleteCategoryResponse?.code == 200) {
                                showSnackBar(activity, deleteCategoryResponse.message)
                            } else if (deleteCategoryResponse?.code == 999) {
                                logout(activity)
                            } else {
                                hideProgressDialog()
                                showSnackBar(activity, deleteCategoryResponse!!.message)
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
     * Dialog Logout
     */
    private fun dialogLogout() {
        AlertDialog.Builder(Objects.requireNonNull(activity))
            .setMessage(getString(R.string.are_you_logout))
            .setPositiveButton(getString(R.string.yes)) { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
                logout(activity)
            }
            .setNegativeButton(getString(R.string.no)) { dialogInterface: DialogInterface, i: Int -> dialogInterface.dismiss() }
            .show()
    }

    fun transactionListClick(position: Int) {
        if (!analyticsList!![position].category.equals("Opening", ignoreCase = true)) {
            if (analyticsList!![position].type.equals("income", ignoreCase = true)) {
                clearUpdateConstant()
                RestConstant.UCLICK_ID = analyticsList!![position].id.toString()
                RestConstant.UICDate = analyticsList!![position].updatedAt!!
                RestConstant.UICCategory = analyticsList!![position].category!!
                RestConstant.UICMoneyIn = analyticsList!![position].bankId!!
                val incomeAmount = analyticsList!![position].amount.toString()
                RestConstant.UICNote = analyticsList!![position].title!!
                RestConstant.UICTextDate = analyticsList!![position].date!!
                RestConstant.UICIMAGEDATA = analyticsList!![position].image!!
                RestConstant.UPDATE_ITEM = "UpdateIncome"
                val i1 = Intent(activity, AddIncomeExpenseActivity::class.java)
                i1.putExtra("Select", "UpdateIncome")
                i1.putExtra("incomeAmount", incomeAmount)
                i1.putExtra("id", analyticsList!![position].id.toString())
                startActivity(i1)
                Animatoo.animateSlideUp(activity)
            } else {
                clearUpdateConstant()
                RestConstant.UCLICK_ID = analyticsList!![position].id.toString()
                RestConstant.UICDate = analyticsList!![position].updatedAt!!
                RestConstant.UICCategory = analyticsList!![position].category!!
                RestConstant.UICMoneyIn = analyticsList!![position].bankId!!
                val incomeAmount = analyticsList!![position].amount.toString()
                RestConstant.UICNote = analyticsList!![position].title!!
                RestConstant.UICTextDate = analyticsList!![position].date!!
                RestConstant.UICIMAGEDATA = analyticsList!![position].image!!
                RestConstant.UPDATE_ITEM = "UpdateExpense"
                val i1 = Intent(activity, AddIncomeExpenseActivity::class.java)
                i1.putExtra("Select", "UpdateExpense")
                i1.putExtra("incomeAmount", incomeAmount)
                i1.putExtra("id", analyticsList!![position].id.toString())
                startActivity(i1)
                Animatoo.animateSlideUp(activity)
            }
        } else {
            showSnackBar(activity, "This is opening card and this card is not editable")
        }
    }

    fun getHideShowButtonClick(flag: String, pay_method_id: String) {
        cardHideShowAPICall(flag, pay_method_id)
    }

    /**
     * CARD HIDE SHOW API CALLING
     */
    private fun cardHideShowAPICall(flag: String, pay_method_id: String) {
        try {
            if (isConnectedToInternet(activity)) {
                showProgressDialog(activity)
                val params = HashMap<String?, String?>()
                params["is_hide"] = flag
                params["pay_method_id"] = pay_method_id
                val call: Call<CardHideShowResponse>?
                call = instance!!.applyHideShowAPICall(
                    params,
                     MySharedPreferences.getMySharedPreferences()?.accessToken
                )
                call.enqueue(object : Callback<CardHideShowResponse?> {
                    override fun onResponse(
                        call: Call<CardHideShowResponse?>,
                        response: Response<CardHideShowResponse?>
                    ) {
                        hideProgressDialog()
                        val cardHideShowResponse: CardHideShowResponse?
                        if (response.isSuccessful) {
                            cardHideShowResponse = response.body()
                            if (cardHideShowResponse?.code == 200) {
                                paymentMethodAPICall()
                            } else if (cardHideShowResponse?.code == 999) {
                                logout(activity)
                            } else {
                                hideProgressDialog()
                                showSnackBar(activity, cardHideShowResponse!!.message)
                            }
                        } else {
                            showSnackBar(activity, response.message())
                        }
                    }

                    override fun onFailure(call: Call<CardHideShowResponse?>, t: Throwable) {
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

    private fun getVideoListAPICall() {
        try {
            if (AppUtils.isConnectedToInternet(activity)) {
                //showProgressDialog(activity)
                val call: Call<VideosResponse> = RetrofitRestClient.instance?.getVideoListData(
                    MySharedPreferences.getMySharedPreferences()?.accessToken
                ) ?: return
                call.enqueue(object : Callback<VideosResponse?> {
                    override fun onResponse(
                        call: Call<VideosResponse?>,
                        response: Response<VideosResponse?>
                    ) {
                        hideProgressDialog()
                        val videosResponse: VideosResponse?
                        if (response.isSuccessful) {
                            videosResponse = response.body()
                            if (videosResponse?.code == 200) {
                                try {
                                    videoList?.clear()
                                    videoList?.addAll(videosResponse.data!!)

                                    if (videoList != null && videoList!!.size > 0) {
                                        videoDialog?.apply {
                                            show()
                                            videolink = videoList!!.get(0).url.toString()

                                            VideoId = extractYTId(videolink).toString()

                                            try {
                                                if (readyForLoadingYoutubeThumbnail) {
                                                    readyForLoadingYoutubeThumbnail = false
                                                    /*  initialize the thumbnail image view , we need to pass Developer Key */
                                                    youtubePlayerThumbnail.initialize(
                                                        key1 + key2 + key3 + key4,
                                                        object : YouTubeThumbnailView.OnInitializedListener {
                                                            override fun onInitializationSuccess(
                                                                youTubeThumbnailView: YouTubeThumbnailView,
                                                                youTubeThumbnailLoader: YouTubeThumbnailLoader
                                                            ) {
                                                                //when initialization is sucess, set the video id to thumbnail to load
                                                                youTubeThumbnailLoader.setVideo(
                                                                    extractYTId(
                                                                        videolink
                                                                    )
                                                                )
                                                                youTubeThumbnailLoader.setOnThumbnailLoadedListener(object :
                                                                    YouTubeThumbnailLoader.OnThumbnailLoadedListener {
                                                                    override fun onThumbnailLoaded(
                                                                        youTubeThumbnailView: YouTubeThumbnailView,
                                                                        s: String
                                                                    ) {
                                                                        //when thumbnail loaded successfully release the thumbnail loader as we are showing thumbnail in adapter
                                                                        youTubeThumbnailLoader.release()
                                                                    }

                                                                    override fun onThumbnailError(
                                                                        youTubeThumbnailView: YouTubeThumbnailView,
                                                                        errorReason: YouTubeThumbnailLoader.ErrorReason
                                                                    ) {
                                                                        //print or show error when thumbnail load failed
                                                                        Log.e("-->ThumbnailError", "Youtube Thumbnail Error")
                                                                    }
                                                                })
                                                                readyForLoadingYoutubeThumbnail = true
                                                            }

                                                            override fun onInitializationFailure(
                                                                youTubeThumbnailView: YouTubeThumbnailView,
                                                                youTubeInitializationResult: YouTubeInitializationResult
                                                            ) {
                                                                //print or show error when initialization failed
                                                                Log.e("-->", "Youtube Initialization Failure")
                                                                readyForLoadingYoutubeThumbnail = true
                                                            }
                                                        })
                                                }
                                            } catch (e: java.lang.Exception) {
                                                e.printStackTrace()
                                            }

                                            btnClose.setOnClickListener {
                                                dismiss()
                                            }

                                            btnYoutubePlay.setOnClickListener {
                                                val intent = YouTubeStandalonePlayer.createVideoIntent(
                                                    activity,
                                                    key1 + key2 + key3 + key4,
                                                    VideoId,  //video id
                                                    100,  //after this time, video will start automatically
                                                    true,  //autoplay or not
                                                    true
                                                ) //lightbox mode or not; show the video in a small box

                                                startActivity(intent)
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            } else if (videosResponse?.code == 999) {
                                logout(activity)
                            } else {
                                hideProgressDialog()
                                showSnackBar(activity, videosResponse!!.message)
                            }
                        } else {
                            showSnackBar(activity, response.message())
                        }
                    }

                    override fun onFailure(call: Call<VideosResponse?>, t: Throwable) {
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

    private fun getGoogleAdsDataListAPICall() {
        try {
            if (AppUtils.isConnectedToInternet(activity)) {
                val call: Call<AdmobAdsResponse> = RetrofitRestClient.instance?.getAdmobAdsListData(
                    MySharedPreferences.getMySharedPreferences()?.accessToken
                ) ?: return
                call.enqueue(object : Callback<AdmobAdsResponse?> {
                    override fun onResponse(
                        call: Call<AdmobAdsResponse?>,
                        response: Response<AdmobAdsResponse?>
                    ) {
                        hideProgressDialog()
                        val admobAdsResponse: AdmobAdsResponse?
                        if (response.isSuccessful) {
                            admobAdsResponse = response.body()
                            if (admobAdsResponse?.code == 200) {
                                try {
                                    adsList?.clear()
                                    adsList?.addAll(admobAdsResponse.data!!)

                                    if (adsList != null && adsList!!.size > 0) {
                                        MySharedPreferences.getMySharedPreferences()?.bannerAdsKey = adsList?.get(0)?.adsKey.toString()
                                        MySharedPreferences.getMySharedPreferences()?.interstitialAdsKey = adsList?.get(1)?.adsKey.toString()
                                        MySharedPreferences.getMySharedPreferences()?.rewardedAdsKey = adsList?.get(4)?.adsKey.toString()
                                        MySharedPreferences.getMySharedPreferences()?.appIdKey = adsList?.get(6)?.adsKey.toString()
                                        MySharedPreferences.getMySharedPreferences()?.showBannerAds = adsList?.get(0)?.enable.toString()
                                        MySharedPreferences.getMySharedPreferences()?.showInterstitialAds = adsList?.get(1)?.enable.toString()
                                        MySharedPreferences.getMySharedPreferences()?.showRewardedAds = adsList?.get(4)?.enable.toString()
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            } else if (admobAdsResponse?.code == 999) {
                                logout(activity)
                            } else {
                                hideProgressDialog()
                                showSnackBar(activity, admobAdsResponse!!.message)
                            }
                        } else {
                            showSnackBar(activity, response.message())
                        }
                    }

                    override fun onFailure(call: Call<AdmobAdsResponse?>, t: Throwable) {
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

    fun extractYTId(ytUrl: String?): String? {
        val pattern =
            "^(?:(?:\\w*.?://)?\\w*.?\\w*-?.?\\w*/(?:embed|e|v|watch|.*/)?\\??(?:feature=\\w*\\.?\\w*)?&?(?:v=)?/?)([\\w\\d_-]+).*"
        val compiledPattern = Pattern.compile(
            pattern,
            Pattern.CASE_INSENSITIVE
        )
        val matcher = compiledPattern.matcher(ytUrl)
        return if (matcher.find()) {
            matcher.group(1)
        } else null
    }
}