package com.example.coconut.model.request.chat

import okhttp3.MultipartBody
import okhttp3.RequestBody

data class ChatUploadImageRequest(
    var userId : RequestBody?,
    var chatRoomId : RequestBody?,
    var images : ArrayList<MultipartBody.Part?>?
) {
}