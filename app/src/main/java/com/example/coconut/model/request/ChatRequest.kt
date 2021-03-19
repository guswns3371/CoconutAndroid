package com.example.coconut.model.request

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody

data class ChatRequest(
    var chatRoomId : RequestBody?,
    var id : RequestBody?,
    var chatMessage : RequestBody?,
    var chatImages : ArrayList<MultipartBody.Part?>?
    ) {
}