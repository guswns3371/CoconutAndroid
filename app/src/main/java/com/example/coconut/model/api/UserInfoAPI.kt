package com.example.coconut.model.api

import com.example.coconut.oauth2.UserInfoResult
import retrofit2.Call
import retrofit2.http.GET

interface UserInfoAPI {

    @GET("/oauth2/v3/userinfo")
    fun getUserInfo(): Call<UserInfoResult>
}