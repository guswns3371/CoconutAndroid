package com.example.coconut.model.request

import com.google.gson.annotations.SerializedName

data class RegisterPostData (
    @SerializedName("email") var userEmail : String,
    @SerializedName("user_id") var userId : String,
    @SerializedName("name") var userName : String,
    @SerializedName("password") var userPassword : String
)