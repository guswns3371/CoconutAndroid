package com.example.coconut.model.request

import com.google.gson.annotations.SerializedName

data class RegisterPostData (
    @SerializedName("userEmail") var userEmail : String,
    @SerializedName("userId") var userId : String,
    @SerializedName("userName") var userName : String,
    @SerializedName("userPassword") var userPassword : String
)