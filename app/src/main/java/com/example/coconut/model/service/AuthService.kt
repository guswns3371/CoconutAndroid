package com.example.coconut.model.service

import com.example.coconut.model.request.EmailVerifyRequest
import com.example.coconut.model.request.FcmTokenRequest
import com.example.coconut.model.request.LoginRequest
import com.example.coconut.model.request.RegisterRequest
import com.example.coconut.model.response.BaseResponse
import com.example.coconut.model.response.auth.LoginResponse
import com.example.coconut.model.response.auth.RegisterResponse
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthService {

    /**
     *  <a href="/oauth2/authorization/google" class="btn btn-success active" role="button">Google Login</a>
     *  <a href="/oauth2/authorization/naver" class="btn btn-secondary active" role="button">Naver Login</a>
     */

    @POST("/oauth2/authorization/google")
    fun googleLogin() : Single<LoginResponse>

    @POST("/oauth2/authorization/naver")
    fun naverLogin() : Single<LoginResponse>

    @POST("/login")
    fun login(
        @Body loginRequest: LoginRequest
    ): Single<LoginResponse>

    @POST("/register")
    fun register(
        @Body registerRequest: RegisterRequest
    ): Single<RegisterResponse>

    @POST("/register/{email}")
    fun checkEmailValidation(
        @Path("email") email : String
    ) : Single<RegisterResponse>

    @POST("/login/verify")
    fun emailVerify(
        @Body emailVerifyRequest: EmailVerifyRequest
    ) : Single<LoginResponse>


    @POST("/user/fcm")
    fun fcmTokenToServer(
        @Body fcmTokenRequest: FcmTokenRequest
    ) : Single<BaseResponse>

    @DELETE("/user/fcm/{id}")
    fun deleteFcmTokenFromServer(
        @Path("id") id : String
    ) : Single<BaseResponse>
}