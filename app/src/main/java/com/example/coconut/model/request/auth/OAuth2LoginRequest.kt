package com.example.coconut.model.request.auth

import com.google.gson.annotations.SerializedName

data class OAuth2LoginRequest(
    @SerializedName("name") var name : String?,
    @SerializedName("email") var email : String?,
    @SerializedName("profilePicture") var profilePicture : String?
) {
    override fun toString(): String {
        return "OAuth2LoginRequest(name='$name', email='$email', profilePicture='$profilePicture')"
    }
}