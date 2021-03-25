package com.example.coconut.model.response.auth

import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @SerializedName("isEmailOk") var isEmailOk: Boolean,
    @SerializedName("isRegistered") var isRegistered : Boolean
) {
    override fun toString(): String {
        return "RegisterResponse(isEmailOk=$isEmailOk, isRegistered=$isRegistered)"
    }
}

