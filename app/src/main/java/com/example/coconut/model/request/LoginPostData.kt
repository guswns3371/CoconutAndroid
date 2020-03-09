package com.example.coconut.model.request

import com.google.gson.annotations.SerializedName

data class LoginPostData(
    @SerializedName("email") var userEmail : String,
    @SerializedName("password") var userPassword : String
)