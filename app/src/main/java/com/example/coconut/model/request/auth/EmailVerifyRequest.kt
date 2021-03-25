package com.example.coconut.model.request.auth

import com.google.gson.annotations.SerializedName

data class EmailVerifyRequest (
    @SerializedName("email") val email : String,
    @SerializedName("secretToken") val secretToken : String
)