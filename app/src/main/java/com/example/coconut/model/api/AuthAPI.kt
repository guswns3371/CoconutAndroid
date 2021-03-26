package com.example.coconut.model.api

import com.example.coconut.model.request.auth.EmailVerifyRequest
import com.example.coconut.model.request.chat.FcmTokenRequest
import com.example.coconut.model.request.auth.LoginRequest
import com.example.coconut.model.request.auth.OAuth2LoginRequest
import com.example.coconut.model.request.auth.RegisterRequest
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

    @POST("/api/auth/login")
    fun login(
        @Body loginRequest: LoginRequest
    ): Single<LoginResponse>

    @POST("/api/auth/register")
    fun register(
        @Body registerRequest: RegisterRequest
    ): Single<RegisterResponse>

    @POST("/api/auth/register/{email}")
    fun checkEmailValidation(
        @Path("email") email : String
    ) : Single<RegisterResponse>

    @POST("/api/auth/login/verify")
    fun emailVerify(
        @Body emailVerifyRequest: EmailVerifyRequest
    ) : Single<LoginResponse>


    @POST("/api/auth/user/fcm")
    fun fcmTokenToServer(
        @Body fcmTokenRequest: FcmTokenRequest
    ) : Single<BaseResponse>

    @DELETE("/api/auth/user/fcm/{id}")
    fun deleteFcmTokenFromServer(
        @Path("id") id : String
    ) : Single<BaseResponse>

    @POST("/api/auth/user/info")
    fun sendUserInfoToServer(
        @Body oAuth2LoginRequest: OAuth2LoginRequest
    ) : Single<OAuth2LoginResponse>
}