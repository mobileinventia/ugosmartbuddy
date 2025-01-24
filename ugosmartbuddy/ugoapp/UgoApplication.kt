package com.inventive.ugosmartbuddy.ugoapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class UgoApplication : Application() {

    val CHANNEL_ID = "Service code Running"
    private var mRetrofit: Retrofit? = null
    //CIMI Dev V2
    private val APIURL = "https://csmiv2devapi.ugoerp.com/"

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        createNotificationChannel()
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(200, TimeUnit.SECONDS)
        .writeTimeout(200, TimeUnit.SECONDS)
        .readTimeout(200, TimeUnit.SECONDS)
        .build()

    val retrofitInstanceLogin: Retrofit?
        get() {
            val gson = GsonBuilder()
                .setLenient()
                .create()
            if (mRetrofit == null) {
                mRetrofit = Retrofit.Builder()
                    .baseUrl(APIURL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okHttpClient)
                    .build()
            }
            return mRetrofit
        }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val seviceChannel = NotificationChannel(
                Companion.CHANNEL_ID,
                "My Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(seviceChannel)
        }
    }

    companion object {
        val TAG = UgoApplication::class.java.simpleName
        const val CHANNEL_ID = "CIServiceChannel"

        @get:Synchronized
        lateinit var mInstance: UgoApplication
            private set

    }
}