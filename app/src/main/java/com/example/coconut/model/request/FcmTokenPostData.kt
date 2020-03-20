package com.example.coconut.model.request

import com.google.gson.annotations.SerializedName

data class FcmTokenPostData (
    @SerializedName("id") var id : String,
    @SerializedName("fcm_token") var fcm_token : String?
)