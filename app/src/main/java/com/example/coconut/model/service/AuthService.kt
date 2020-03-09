package com.example.coconut.model.service

import com.example.coconut.model.request.EmailVerifyPostData
import com.example.coconut.model.request.LoginPostData
import com.example.coconut.model.request.RegisterPostData
import com.example.coconut.model.response.auth.LoginResponse
import com.example.coconut.model.response.auth.RegisterResponse
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthService {

    @POST("/login")
    fun login(
        @Body loginPostData: LoginPostData
    ): Single<LoginResponse>

    @POST("/register")
    fun register(
        @Body registerPostData: RegisterPostData
    ): Single<RegisterResponse>

    @POST("/register/{email}")
    fun checkEmailValidation(
        @Path("email") email : String
    ) : Single<RegisterResponse>

    @POST("/login/verify")
    fun emailVerify(
        @Body emailVerifyPostData: EmailVerifyPostData
    ) : Single<LoginResponse>


}