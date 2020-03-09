package com.example.coconut.model.response.chat

import com.google.gson.annotations.SerializedName

data class ChatBaseResponse(
    @SerializedName("success") var success : Boolean,
    @SerializedName("message") var message : String,
    @SerializedName("chat_room_id") var ChatRoomIdResponse : String,
    @SerializedName("chat_room_name") var ChatRoomNameResponse : String,
    @SerializedName("chat_room_people") var ChatRoomPeopleResponse : String
    ) {
    override fun toString(): String {
        return "ChatBaseResponse(success=$success, message='$message', ChatRoomIdResponse='$ChatRoomIdResponse', ChatRoomNameResponse='$ChatRoomNameResponse', ChatRoomPeopleResponse='$ChatRoomPeopleResponse')"
    }
}