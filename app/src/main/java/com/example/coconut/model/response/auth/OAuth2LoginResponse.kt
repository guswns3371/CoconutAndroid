package com.example.coconut.model.response.auth

import com.google.gson.annotations.SerializedName

data class OAuth2LoginResponse(
    @SerializedName("userId") var userId : String?,
    @SerializedName("email") var email : String?,
    @SerializedName("name") var name : String?,
    @SerializedName("profilePicture") var profilePicture : String?
) {
    override fun toString(): String {
        return "OAuth2LoginResponse(userId=$userId, email=$email, name=$name, profilePicture=$profilePicture)"
    }
}