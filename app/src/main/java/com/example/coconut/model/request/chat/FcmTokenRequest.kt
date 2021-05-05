package com.example.coconut.model.request.chat

import com.google.gson.annotations.SerializedName

data class FcmTokenRequest (
    @SerializedName("userId") var userId : String,
    @SerializedName("fcmToken") var fcmToken : String?
)