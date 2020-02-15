package com.example.coconut.ui.auth

import androidx.lifecycle.LiveData
import com.example.coconut.model.response.LoginResponse
interface AuthListener{
    interface LoginListener {
        fun onStarted()
        fun onSuccess(loginResponse: LiveData<LoginResponse>)
        fun onFailure(message: String)
    }
    interface RegisterListner{
        fun onStarted()
        fun onSuccess()
        fun onFailure(message: String)
    }
}
