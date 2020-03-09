package com.example.coconut.model.response.chat

import com.google.gson.annotations.SerializedName

data class ChatRoomResponse(
    @SerializedName("id") var id : String?,
    @SerializedName("people") var people : String?,
    @SerializedName("last_content") var last_content : String?,
    @SerializedName("last_time") var last_time : String?
) {
    override fun toString(): String {
        return "ChatRoomResponse(id=$id, people=$people, last_content=$last_content, last_time=$last_time)"
    }
}
