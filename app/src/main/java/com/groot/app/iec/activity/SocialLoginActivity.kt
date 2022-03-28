package com.groot.app.iec.activity

import com.groot.app.iec.utils.AppUtils.isConnectedToInternet
import com.groot.app.iec.utils.AppUtils.showToast
import android.os.Bundle
import android.annotation.SuppressLint
import com.groot.app.iec.R
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.groot.app.iec.rest.RetrofitRestClient
import com.groot.app.iec.utils.MySharedPreferences
import android.content.Intent
import com.facebook.login.widget.LoginButton
import com.facebook.CallbackManager
import com.google.android.gms.common.api.GoogleApiClient
import android.content.pm.PackageManager
import com.facebook.FacebookCallback
import com.facebook.login.LoginResult
import com.facebook.FacebookException
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.facebook.GraphRequest
import org.json.JSONObject
import com.facebook.GraphResponse
import org.json.JSONException
import com.groot.app.iec.model.social_signup.SocialSignupResponse
import com.facebook.login.LoginManager
import android.view.View
import android.widget.CompoundButton
import com.groot.app.iec.databinding.ActivitySocialLoginBinding
import com.groot.app.iec.model.social_signup.User
import com.groot.app.iec.utils.BaseActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

class SocialLoginActivity : BaseActivity(), View.OnClickListener {
    private var binding: ActivitySocialLoginBinding? = null
    private val loginType = ""
    private var TYPE = ""
    private var email = ""
    private var firstName = ""
    private var last_name = ""
    private var profile_pic = ""
    private var ID = ""
    private val AnimationFlag = ""
    private val ButtonName = ""
    private var loginButton: LoginButton? = null
    private var callbackManager: CallbackManager? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        super.onCreate(savedInstanceState)
        binding = ActivitySocialLoginBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        /**
         * Social Sign Up Initialization
         */
        socialSignUpInitialization()
        binding!!.btnFacebookLogin.setOnClickListener(this)
        binding!!.btnGoogleLogin.setOnClickListener(this)
        binding!!.txtTermsConditions.setOnClickListener(this)
        binding!!.txtPrivacyPolicy.setOnClickListener(this)

        binding!!.btnGoogleLogin.isEnabled = false

        binding!!.btnPrivacy.setOnCheckedChangeListener { _, isChecked ->
            binding!!.btnGoogleLogin.isEnabled = isChecked
        }
    }

    @SuppressLint("WrongConstant")
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnGoogleLogin -> {
                /*Hide Keyboard*/hideKeyBoard(activity)
                TYPE = "Google"
                signIn()
            }
            R.id.btnFacebookLogin -> {
                hideKeyBoard(activity)
                TYPE = "Facebook"
                loginButton!!.performClick()
            }
            R.id.txtPrivacyPolicy -> {
                startActivity(Intent(activity, PrivacyPolicyActivity::class.java))
                Animatoo.animateSlideLeft(activity)
            }
            R.id.txtTermsConditions -> {
                startActivity(Intent(activity, TermsAndConditionsActivity::class.java))
                Animatoo.animateSlideLeft(activity)
            }
        }
    }

    /**
     * Social Sign Up Initialization
     */
    private fun socialSignUpInitialization() {
        /**
         * For Get Facebook Signature
         */
        try {
            @SuppressLint("PackageManagerGetSignatures") val info = packageManager.getPackageInfo(
                packageName, PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                //  Log.e("fb_key_hash :", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        /***
         * Facebook
         */
        callbackManager = CallbackManager.Factory.create()
        loginButton = findViewById(R.id.login_button)
        loginButton?.setReadPermissions("email")
        loginButton?.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                getDataFromFacebook(loginResult)
            }

            override fun onCancel() {}
            override fun onError(exception: FacebookException) {
                exception.printStackTrace()
            }
        })
        /**
         * G-mail
         */
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this) { connectionResult -> connectionResult.errorMessage }
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()
    }

    /*Activity Results For Facebook and Gmail*/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result = data?.let { Auth.GoogleSignInApi.getSignInResultFromIntent(it) }
            result?.let { getDataFromGMail(it) }
        }
    }

    /*Get Data From Facebook*/
    protected fun getDataFromFacebook(loginResult: LoginResult) {
        val data_request = GraphRequest.newMeRequest(
            loginResult.accessToken
        ) { json_object: JSONObject, response: GraphResponse? ->
            try {
                ID = json_object.getString("id")
                //first_name = json_object.getString("first_name")
                last_name = json_object.getString("last_name")
                val pic = json_object.getJSONObject("picture")
                val img = pic.getJSONObject("data")
                if (json_object.has("email")) {
                    email = json_object.getString("email")
                }
                profile_pic = img.getString("url")
                signUpAPi("first_name", last_name, profile_pic, email, ID)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        val permission_param = Bundle()
        permission_param.putString(
            "fields",
            "id,name,first_name,last_name,email,picture.width(120).height(120)"
        )
        data_request.parameters = permission_param
        data_request.executeAsync()
    }

    /*Get Data From Gmail*/
    private fun getDataFromGMail(result: GoogleSignInResult) {
        if (result.isSuccess) {
            // Signed in successfully, show authenticated UI.
            val acct = result.signInAccount
            firstName =
                if (acct?.givenName == null) {
                ""
                } else {
                    acct.givenName.toString()
                }
            last_name = acct?.familyName.toString()
            email = acct?.email.toString()
            ID = acct?.id.toString()
            try {
                profile_pic = if (acct?.photoUrl != null) {
                    acct.photoUrl.toString()
                } else {
                    ""
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            signUpAPi(firstName, last_name, profile_pic, email, ID)
        } else {
        }
    }

    /*Gmail Sign In*/
    private fun signIn() {
        val signInIntent = mGoogleApiClient?.let { Auth.GoogleSignInApi.getSignInIntent(it) }
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    /**
     * Social Sign Up
     */
    private fun signUpAPi(
        firstName: String,
        lastname: String,
        Img_url: String,
        email: String,
        id: String
    ) {
        try {
            if (isConnectedToInternet(activity)) {
                showProgressDialog(activity)
                val params = HashMap<String, String>()
                params["app_id"] = id
                params["login_type"] = TYPE
                params["user_name"] = "$firstName $lastname"
                params["email"] = email
                params["image"] = Img_url
                params["device_id"] = MySharedPreferences.getMySharedPreferences()?.deviceId.toString()
                params["phone"] = "7874614320"
                params["country"] = ""
                val call: Call<SocialSignupResponse>?
                call = RetrofitRestClient.instance?.socialSignUp(params)
                if (call == null) return
                call.enqueue(object : Callback<SocialSignupResponse?> {
                    override fun onResponse(
                        call: Call<SocialSignupResponse?>,
                        response: Response<SocialSignupResponse?>
                    ) {
                        hideProgressDialog()
                        val socialsignUpResponse: SocialSignupResponse?
                        if (response.isSuccessful) {
                            socialsignUpResponse = response.body()
                            if (socialsignUpResponse?.code == 200) {
                                showToast(activity, socialsignUpResponse.message)
                                setUserData1(socialsignUpResponse.user!!, socialsignUpResponse)
                                if (socialsignUpResponse.user?.firstTimeLogin.equals(
                                        "0",
                                        ignoreCase = true
                                    )
                                ) {
                                    startActivity(
                                        Intent(
                                            this@SocialLoginActivity,
                                            SelectCountryListActivity::class.java
                                        )
                                    )
                                } else {
                                    if (socialsignUpResponse.user?.defaultCardCreated.equals(
                                            "1",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (MySharedPreferences.getMySharedPreferences()?.currency.equals("")
                                        ) {
                                            startActivity(
                                                Intent(
                                                    this@SocialLoginActivity,
                                                    SelectCountryListActivity::class.java
                                                )
                                            )
                                        } else {
                                            startActivity(
                                                Intent(
                                                    this@SocialLoginActivity,
                                                    DashboardActivity::class.java
                                                )
                                            )
                                        }
                                    } else {
                                        startActivity(
                                            Intent(
                                                this@SocialLoginActivity,
                                                AddMultipleCardActivity::class.java
                                            )
                                        )
                                    }
                                }
                                finish()
                                Animatoo.animateFade(activity)
                            } else if (socialsignUpResponse?.code == 999) {
                                logout(activity)
                            } else {
                                hideProgressDialog()
                                showSnackBar(activity, socialsignUpResponse!!.message)
                                LoginManager.getInstance().logOut()
                            }
                        } else {
                            showSnackBar(activity, response.message())
                            LoginManager.getInstance().logOut()
                        }
                    }

                    override fun onFailure(call: Call<SocialSignupResponse?>, t: Throwable) {
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
     * Set All Data into Shared Preferences
     */
    private fun setUserData1(data: User, socialsignUpResponse: SocialSignupResponse?) {
        try {
            val mySharedPreferences = MySharedPreferences.getMySharedPreferences()
            mySharedPreferences!!.isLogin = true
            mySharedPreferences.userId = data.id.toString()
            mySharedPreferences.userName = data.userName
            mySharedPreferences.email = data.email
            mySharedPreferences.accessToken = "Bearer " + socialsignUpResponse!!.token
            mySharedPreferences.contactNumber = data.phone
            mySharedPreferences.profile = data.image
            if (data.currency != null) {
                mySharedPreferences.countryCode = data.currency
            } else {
                mySharedPreferences.currency = ""
            }
            if (data.country != null) {
                mySharedPreferences.country = data.country
            }
            if (data.firstTimeLogin.equals("0", ignoreCase = true)) {
                mySharedPreferences.isFirstTimeLogin = true
                mySharedPreferences.isFirstTimeLoginVideoShow = true
            } else {
                mySharedPreferences.isFirstTimeLogin = false
                mySharedPreferences.isFirstTimeLoginVideoShow = false
            }
            if (data.defaultCardCreated != null) {
                mySharedPreferences.isCardAdded = data.defaultCardCreated.equals("1", ignoreCase = true)
            } else {
                mySharedPreferences.isCardAdded = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        /*For Social Signup Use*/
        private const val RC_SIGN_IN = 145
    }
}