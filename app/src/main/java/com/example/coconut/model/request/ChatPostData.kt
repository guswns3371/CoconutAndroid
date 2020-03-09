package com.example.coconut.model.request

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody

data class ChatPostData(
    var chat_room_id : RequestBody?,
    var id : RequestBody?,
    var chat_message : RequestBody?,
    var chat_images : ArrayList<MultipartBody.Part?>?
    ) {
}