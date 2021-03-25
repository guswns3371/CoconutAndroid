package com.example.coconut.model.request.chat

import com.google.gson.annotations.SerializedName

data class MakeChatRoomRequest(
    @SerializedName("chatUserId") var chatUserId : String?,
    @SerializedName("chatRoomId") var chatRoomId : String?,
    @SerializedName("chatRoomPeople") var chatRoomPeople : ArrayList<String>
) {
}