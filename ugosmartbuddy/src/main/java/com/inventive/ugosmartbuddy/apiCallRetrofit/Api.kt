package com.inventive.ugosmartbuddy.apiCallRetrofit

import com.inventive.ugosmartbuddy.smartbuddy.models.ValidateModel
import retrofit2.Call
import retrofit2.http.*

interface Api {

    @GET("SmartBuddy/GetSDKValidityDate")
    fun getSdkValidityData(): Call<ValidateModel>?
}