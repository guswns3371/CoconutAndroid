package com.example.coconut.model.response.auth

import com.google.gson.annotations.SerializedName

data class OAuth2LoginResponse(
    @SerializedName("isCorrect") var isCorrect : Boolean,
    @SerializedName("isConfirmed") var isConfirmed : Boolean
) {

}