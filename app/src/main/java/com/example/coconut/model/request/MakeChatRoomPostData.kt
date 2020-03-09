package com.example.coconut.model.request

import com.google.gson.annotations.SerializedName

data class MakeChatRoomPostData(
    @SerializedName("chat_user_id") var chat_user_id : String?,
    @SerializedName("chat_room_id") var chat_room_id : String?,
    @SerializedName("chat_room_people") var chat_room_people : ArrayList<String>
) {
}