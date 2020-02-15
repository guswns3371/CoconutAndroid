package com.example.coconut.model.request

import com.google.gson.annotations.SerializedName

data class LoginPostData(
    @SerializedName("userEmail") var userEmail : String,
    @SerializedName("userPassword") var userPassword : String
)