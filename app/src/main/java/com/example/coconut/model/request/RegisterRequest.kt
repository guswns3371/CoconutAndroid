package com.example.coconut.model.request

import com.google.gson.annotations.SerializedName

data class RegisterRequest (
    @SerializedName("email") var email : String,
    @SerializedName("userId") var userId : String,
    @SerializedName("name") var name : String,
    @SerializedName("password") var password : String
)