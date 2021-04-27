package com.example.coconut.model.request.chat

import com.google.gson.annotations.SerializedName

data class ChatRoomSaveRequest(
    @SerializedName("chatUserId") var chatUserId : String?,
    @SerializedName("chatRoomMembers") var chatRoomMembers : ArrayList<String>
) {
}