package com.example.coconut.model.socket

import com.google.auto.value.AutoValue
import okhttp3.MultipartBody
import okhttp3.RequestBody

data class ChatMessageSocketData(
    var chatRoomId : String?,
    var chatUserId : String,
    var chatMessage : String,
    var chatRoomMembers : ArrayList<String>?,
    var readMembers : ArrayList<String>?,
    var chatImages : ArrayList<String>?
) {
}