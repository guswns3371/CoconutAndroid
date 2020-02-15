package com.example.coconut.model

import com.example.coconut.model.request.LoginPostData
import com.example.coconut.model.request.RegisterPostData
import com.example.coconut.model.response.LoginResponse
import com.example.coconut.model.response.RegisterResponse
import io.reactivex.Single

interface UserDataModel {
    fun doLogin(loginPostData: LoginPostData) : Single<LoginResponse>

    fun checkEmailValidation(email : String) : Single<RegisterResponse>

    fun doRegister(registerPostData: RegisterPostData) : Single<RegisterResponse>
}