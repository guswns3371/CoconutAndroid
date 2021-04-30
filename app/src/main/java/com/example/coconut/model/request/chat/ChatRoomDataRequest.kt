package com.example.coconut.model.request.chat;

import com.google.gson.annotations.SerializedName

data class ChatRoomDataRequest(
    @SerializedName("chatUserId") var chatUserId : String?,
    @SerializedName("chatRoomId") var chatRoomId : String?,
    @SerializedName("chatRoomMembers") var chatRoomMembers : ArrayList<String>
) {
}
