package com.example.coconut.model.socket

import okhttp3.MultipartBody
import okhttp3.RequestBody

data class ChatSocketData(
    var chat_room_id : String?,
    var chat_user_id : String,
    var content : String,
    var chat_room_people : ArrayList<String>?,
    var chat_current_room_people : ArrayList<String>?,
    var chat_images : ArrayList<String>?
) {
}