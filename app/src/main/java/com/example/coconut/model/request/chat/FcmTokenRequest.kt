package com.example.coconut.model.request.chat

import com.google.gson.annotations.SerializedName

data class FcmTokenRequest (
    @SerializedName("id") var id : String,
    @SerializedName("fcmToken") var fcmToken : String?
)