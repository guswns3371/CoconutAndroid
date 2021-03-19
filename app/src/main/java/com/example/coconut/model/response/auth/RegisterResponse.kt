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

data class RegisterValid(
    var idOk :Boolean,
    var nameOk : Boolean,
    var passwordOk : Boolean,
    var passwordConOk : Boolean
){
    override fun toString(): String {
        return "RegisterVaild(idOk=$idOk, nameOk=$nameOk, passwordOk=$passwordOk, passwordConOk=$passwordConOk)"
    }
}