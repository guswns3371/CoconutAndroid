package com.example.coconut.model.request.chat

import com.google.gson.annotations.SerializedName

data class ChatRoomNameChangeRequest(
    @SerializedName("chatRoomName") var chatRoomName : String?,
    @SerializedName("chatRoomId") var chatRoomId : String?,
    @SerializedName("userId") var userId : String?
) {
}