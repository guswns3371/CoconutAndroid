package com.example.coconut.model.api

import com.example.coconut.model.request.auth.*
import com.example.coconut.model.request.chat.FcmTokenRequest
import com.example.coconut.model.response.BaseResponse
import com.example.coconut.model.response.auth.LoginResponse
import com.example.coconut.model.response.auth.OAuth2LoginResponse
import com.example.coconut.model.response.auth.RegisterResponse
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthAPI {

    /**
     *  <a href="/oauth2/authorization/google" class="btn btn-success active" role="button">Google Login</a>
     *  <a href="/oauth2/authorization/naver" class="btn btn-secondary active" role="button">Naver Login</a>
     */

    @POST("/oauth2/authorization/google")
    fun googleLogin() : Single<LoginResponse>

    @POST("/oauth2/authorization/naver")
    fun naverLogin() : Single<LoginResponse>

    @POST("/api/account/login")
    fun login(
        @Body loginRequest: LoginRequest
    ): Single<LoginResponse>

    @POST("/api/account/register")
    fun register(
        @Body registerRequest: RegisterRequest
    ): Single<RegisterResponse>

    @POST("/api/account/email-check")
    fun checkEmailValidation(
        @Body request : EmailCheckRequest
    ) : Single<RegisterResponse>

    @POST("/api/account/login/verify")
    fun emailVerify(
        @Body emailVerifyRequest: EmailVerifyRequest
    ) : Single<LoginResponse>


    @POST("/api/account/user/fcm")
    fun fcmTokenToServer(
        @Body fcmTokenRequest: FcmTokenRequest
    ) : Single<BaseResponse>

    @DELETE("/api/account/user/fcm/{id}")
    fun deleteFcmTokenFromServer(
        @Path("id") id : String
    ) : Single<BaseResponse>

    @POST("/api/account/user/info")
    fun sendUserInfoToServer(
        @Body oAuth2LoginRequest: OAuth2LoginRequest
    ) : Single<OAuth2LoginResponse>
}