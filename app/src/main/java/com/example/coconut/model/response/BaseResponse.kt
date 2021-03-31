package com.example.coconut.model.response

import com.google.gson.annotations.SerializedName

data class BaseResponse(
    @SerializedName("success") var success : Boolean,
    @SerializedName("message") var message : String
){
    override fun toString(): String {
        return "BaseResponse(success=$success, message='$message')"
    }
}