package com.example.coconut.model.response.chat

import com.example.coconut.model.response.account.UserDataResponse
import com.google.gson.annotations.SerializedName

data class ChatRoomListResponse(
    @SerializedName("chat_room_id") var chat_room_id : String?,
    @SerializedName("unread_num") var unread_num : String?,
    @SerializedName("room_name") var room_name : String?,
    @SerializedName("chat_room_info") var chat_room_Info_info : ChatRoomInfoResponse?,
    @SerializedName("user_info") var user_info : ArrayList<UserDataResponse>?
) {
    override fun toString(): String {
        return "ChatListResponse(chat_room_id=$chat_room_id, unread_num=$unread_num, room_name=$room_name, chat_room_info=$chat_room_Info_info, user_info=$user_info)"
    }
}

data class ChatRoomInfoResponse(
    @SerializedName("id") var id : String?,
    @SerializedName("people") var people : String?,
    @SerializedName("last_content") var last_content : String?,
    @SerializedName("last_time") var last_time : String?
) {
    override fun toString(): String {
        return "ChatRoomInfoResponse(id=$id, people=$people, last_content=$last_content, last_time=$last_time)"
    }
}


/**
 *"chat_room_id": "14",
"unread_num": null,
"chat_room_info": {
"id": 14,
"people": "\"1\"",
"last_content": "메롱",
"last_time": "2020-03-06T17:33:01.000Z",
"createdAt": "2020-03-06T08:32:56.000Z",
"updatedAt": "2020-03-06T08:33:01.000Z"
}*/