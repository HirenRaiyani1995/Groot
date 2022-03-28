package com.groot.app.iec.activity

import com.groot.app.iec.utils.AppUtils.isConnectedToInternet
import com.groot.app.iec.adapter.ActiveCardListAdapter
import com.groot.app.iec.adapter.HiddenCardListAdapter
import android.os.Bundle
import com.groot.app.iec.R
import android.annotation.SuppressLint
import android.app.AlertDialog
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.groot.app.iec.model.payment_method.PaymentMethodResponse
import com.groot.app.iec.rest.RetrofitRestClient
import com.groot.app.iec.utils.MySharedPreferences
import com.groot.app.iec.utils.CenterZoomLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.groot.app.iec.model.card_hide_show.CardHideShowResponse
import com.groot.app.iec.rest.RestConstant
import android.content.DialogInterface
import android.view.View
import com.groot.app.iec.databinding.ActivityManageCardsBinding
import com.groot.app.iec.model.BasicModelResponse
import com.groot.app.iec.model.payment_method.DataItem
import com.groot.app.iec.utils.BaseActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.*

class ManageCardsActivity : BaseActivity(), View.OnClickListener {
    private var binding: ActivityManageCardsBinding? = null
    private var activeCardListAdapter: ActiveCardListAdapter? = null
    private var hiddenCardListAdapter: HiddenCardListAdapter? = null
    private var horizontalList: ArrayList<DataItem>? = null
    private var activeCardList: ArrayList<DataItem>? = null
    private var hiddenCardList: ArrayList<DataItem>? = null
    private var dialogAddBank: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        super.onCreate(savedInstanceState)
        binding = ActivityManageCardsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        horizontalList = ArrayList()
        activeCardList = ArrayList()
        hiddenCardList = ArrayList()
        dialogAddBank = findViewById(R.id.dialogAddBank)
        binding!!.btnBack.setOnClickListener(this)
        paymentMethodAPICall()

        //Todo:: Load Interstitial Ads
        showInterstitialAds(this)
    }

    @SuppressLint("WrongConstant")
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnBack -> onBackPressed()
        }
    }

    override fun onBackPressed() {
        if (dialogAddBank!!.visibility == View.VISIBLE) {
            dialogAddBank!!.visibility = View.GONE
        } else {
            super.onBackPressed()
            Animatoo.animateSlideRight(activity)
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
                call = RetrofitRestClient.instance?.getPaymentMethodAPICall(
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
                                horizontalList!!.clear()
                                activeCardList!!.clear()
                                hiddenCardList!!.clear()
                                horizontalList!!.addAll(paymentMethodResponse!!.data!!)
                                if (horizontalList!!.size > 0) {
                                    for (i in horizontalList!!.indices) {
                                        if (horizontalList!![i].isHide == 1) {
                                            hiddenCardList!!.add(horizontalList!![i])
                                        }
                                    }
                                    if (hiddenCardList!!.size == 0) {
                                        binding!!.txtHiddenCard.visibility = View.GONE
                                    } else {
                                        binding!!.rcvHiddenCard.layoutManager =
                                            CenterZoomLayoutManager(
                                                activity,
                                                LinearLayoutManager.HORIZONTAL,
                                                false
                                            )
                                        hiddenCardListAdapter = HiddenCardListAdapter(
                                            activity,
                                            hiddenCardList!!,
                                            this@ManageCardsActivity
                                        )
                                        binding!!.rcvHiddenCard.adapter = hiddenCardListAdapter
                                        binding!!.txtHiddenCard.visibility = View.VISIBLE
                                    }
                                } else {
                                    binding!!.txtHiddenCard.visibility = View.GONE
                                }
                                if (horizontalList!!.size > 0) {
                                    for (j in horizontalList!!.indices) {
                                        if (horizontalList!![j].isHide == 0) {
                                            activeCardList!!.add(horizontalList!![j])
                                        }
                                    }
                                    if (activeCardList!!.size == 0) {
                                        binding!!.txtActiveCard.visibility = View.GONE
                                    } else {
                                        binding!!.rcvActiveCard.layoutManager =
                                            CenterZoomLayoutManager(
                                                activity,
                                                LinearLayoutManager.HORIZONTAL,
                                                false
                                            )
                                        activeCardListAdapter = ActiveCardListAdapter(
                                            activity,
                                            activeCardList!!,
                                            this@ManageCardsActivity
                                        )
                                        binding!!.rcvActiveCard.adapter = activeCardListAdapter
                                        binding!!.txtActiveCard.visibility = View.VISIBLE
                                    }
                                } else {
                                    binding!!.txtActiveCard.visibility = View.GONE
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
                call = RetrofitRestClient.instance?.applyHideShowAPICall(
                    params,
                    MySharedPreferences.getMySharedPreferences()?.accessToken
                )
                if (call == null) return
                call.enqueue(object : Callback<CardHideShowResponse?> {
                    override fun onResponse(
                        call: Call<CardHideShowResponse?>,
                        response: Response<CardHideShowResponse?>
                    ) {
                        hideProgressDialog()
                        val cardHideShowResponse: CardHideShowResponse?
                        if (response.isSuccessful) {
                            cardHideShowResponse = response.body()
                            if (Objects.requireNonNull(cardHideShowResponse)?.code == 200) {
                                RestConstant.IS_Add_FINISH = true
                                paymentMethodAPICall()
                            } else if (Objects.requireNonNull(cardHideShowResponse)?.code == 999) {
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

    fun getHideShowButtonClick(flag: String, pay_method_id: String) {
        cardHideShowAPICall(flag, pay_method_id)
    }

    fun deleteCardButtonClick(DeleteBankId: String) {
        dialogDeleteBank(DeleteBankId)
    }

    /**
     * Dialog source delete
     */
    private fun dialogDeleteBank(DeleteBankId: String) {
        AlertDialog.Builder(Objects.requireNonNull(activity))
            .setMessage(getString(R.string.are_you_delete))
            .setPositiveButton(getString(R.string.yes)) { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
                deleteBankAPICall(DeleteBankId)
            }
            .setNegativeButton(getString(R.string.no)) { dialogInterface: DialogInterface, i: Int -> dialogInterface.dismiss() }
            .show()
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
                call = RetrofitRestClient.instance?.deleteBankAPICall(
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
                        val updateBankResponse: BasicModelResponse?
                        if (response.isSuccessful) {
                            updateBankResponse = response.body()
                            if (Objects.requireNonNull(updateBankResponse)?.code == 200) {
                                RestConstant.IS_Add_FINISH = true
                                paymentMethodAPICall()
                            } else if (Objects.requireNonNull(updateBankResponse)?.code == 999) {
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
}