package com.example.coconut.model

import com.example.coconut.model.request.LoginPostData
import com.example.coconut.model.request.RegisterPostData
import com.example.coconut.model.response.LoginResponse
import com.example.coconut.model.response.RegisterResponse
import com.example.coconut.model.service.CocoaService
import io.reactivex.Single

class UserDataModelImpl(private val service: CocoaService) : UserDataModel {

    override fun doLogin(loginPostData: LoginPostData)
            : Single<LoginResponse> {
        return service.login(loginPostData)
    }

    override fun checkEmailValidation(email: String)
            : Single<RegisterResponse> {
        return service.checkEmailValidation(email)
    }

    override fun doRegister(registerPostData: RegisterPostData)
            : Single<RegisterResponse> {
        return service.register(registerPostData)
    }
}