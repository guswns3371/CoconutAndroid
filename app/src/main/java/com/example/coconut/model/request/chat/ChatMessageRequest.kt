package com.example.coconut.model.request.chat

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody

data class ChatMessageRequest(
    var chatRoomId : String?,
    var userId : String?,
    var chatMessage : String?,
    var chatImages : ArrayList<MultipartBody.Part?>?
    ) {
}