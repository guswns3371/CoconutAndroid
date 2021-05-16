package com.example.coconut.model.response.chat

import com.google.gson.annotations.SerializedName

data class ChatRoomInfoResponse(
    @SerializedName("id") var id: String?,
    @SerializedName("members") var members: String?,
    @SerializedName("roomType") var roomType : String?,
    @SerializedName("lastMessage") var lastMessage: String?,
    @SerializedName("lastTime") var lastTime: String?
) {
    override fun toString(): String {
        return "ChatRoomInfoResponse(id=$id, members=$members, roomType=$roomType, lastMessage=$lastMessage, lastTime=$lastTime)"
    }
}