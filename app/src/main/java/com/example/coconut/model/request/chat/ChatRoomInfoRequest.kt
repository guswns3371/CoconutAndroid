package com.example.coconut.model.request.chat;

import com.google.gson.annotations.SerializedName

data class ChatRoomInfoRequest(
    @SerializedName("chat_user_id") var chatUserId : String?,
    @SerializedName("chat_room_id") var chatRoomId : String?,
    @SerializedName("chat_room_people") var chatRoomPeople : ArrayList<String>
) {
}
