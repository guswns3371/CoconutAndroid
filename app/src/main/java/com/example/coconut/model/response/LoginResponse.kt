package com.example.coconut.model.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(
  @SerializedName("isCorrect") var isCorrect : Boolean,
  @SerializedName("email")  var email: String,
  @SerializedName("isRemember") var isRemember : Boolean
){
  override fun toString(): String {
    return "LoginResponse(isCorrect='$isCorrect', id='$email', isRemember='$isRemember')"
  }
}