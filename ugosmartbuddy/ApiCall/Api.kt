package com.inventive.ugosmartbuddy.ApiCall

import com.inventive.ugosmartbuddy.smartbuddy.models.ValidateModel
import retrofit2.Call
import retrofit2.http.*

interface Api {

    @GET("SmartBuddy/GetSDKValidityDate")
    fun getSdkValidityData(): Call<ValidateModel>?
}