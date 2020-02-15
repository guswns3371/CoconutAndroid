package com.example.coconut.model.service

import com.example.coconut.model.request.LoginPostData
import com.example.coconut.model.request.RegisterPostData
import com.example.coconut.model.response.LoginResponse
import com.example.coconut.model.response.RegisterResponse
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CocoaService {

    @POST("/login")
    fun login(
    @Body loginPostData: LoginPostData
    ): Single<LoginResponse>

    @POST("/register")
    fun register(
        @Body registerPostData: RegisterPostData
    ): Single<RegisterResponse>

    @POST("/register/{registerEmail}")
    fun checkEmailValidation(
        @Path("registerEmail") email : String
    ) : Single<RegisterResponse>
}