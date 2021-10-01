package com.example.coconut.model.request.auth

import com.google.gson.annotations.SerializedName

data class EmailCheckRequest(
    @SerializedName("email") val email : String
) {
}