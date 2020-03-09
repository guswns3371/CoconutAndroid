package com.example.coconut.model.request

import com.google.gson.annotations.SerializedName

data class EmailVerifyPostData (
    @SerializedName("email") val email : String,
    @SerializedName("secretToken") val secretToken : String
)