package com.groot.app.iec.rest

import android.util.Log
import okhttp3.logging.HttpLoggingInterceptor
import kotlin.Throws
import com.google.gson.GsonBuilder
import com.groot.app.iec.utils.MySharedPreferences
import retrofit2.Retrofit
import okhttp3.*
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object RetrofitRestClient {
    private const val TIME = 80
    private var baseApiService: ApiService? =
        null
    // set your desired log level
    private var httpClient: OkHttpClient? = null
        private get() {
            val logging = HttpLoggingInterceptor()
            // set your desired log level
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            field = OkHttpClient().newBuilder()
                .connectTimeout(TIME.toLong(), TimeUnit.SECONDS)
                .readTimeout(TIME.toLong(), TimeUnit.SECONDS)
                .writeTimeout(TIME.toLong(), TimeUnit.SECONDS)
                .addInterceptor(HeaderInterceptor())
                .addNetworkInterceptor(Interceptor { chain ->
                    val response = chain.proceed(chain.request())
                    val body = response.body
                    val bodyString = body!!.string()
                    val contentType = body.contentType()
                    //Log.e("-->APIResponse", bodyString)
                    //  Log.e("==>Device Id", MySharedPreferences.getMySharedPreferences().getDeviceId());
                     // Log.e("==>Acceess Token", MySharedPreferences.getMySharedPreferences()?.accessToken.toString())
                    response.newBuilder().body(ResponseBody.create(contentType, bodyString)).build()
                })
                .build()
            return field
        }
    var gson = GsonBuilder()
        .setLenient()
        .create()
    @JvmStatic
    val instance: ApiService?
        get() {
            if (baseApiService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(RestConstant.BASE_URL)
                    .addConverterFactory(ToStringConverterFactory())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient)
                    .build()
                baseApiService = retrofit.create(ApiService::class.java)
            }
            return baseApiService
        }

    class HeaderInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val request: Request = chain.request()
           // Log.e("-->API_URL", request.url.toString())
            return chain.proceed(request)
        }
    }
}