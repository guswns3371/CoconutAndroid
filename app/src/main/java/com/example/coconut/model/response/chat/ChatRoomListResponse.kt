package com.example.coconut.model.response.chat

import com.example.coconut.model.response.account.UserDataResponse
import com.google.gson.annotations.SerializedName

data class ChatRoomListResponse(
    @SerializedName("chatRoomId") var chatRoomId: String?,
    @SerializedName("unReads") var unReads: String?,
    @SerializedName("chatRoomName") var chatRoomName: String?,
    @SerializedName("chatRoomInfo") var chatRoomInfo: ChatRoomInfoResponse?,
    @SerializedName("userInfo") var userInfo: ArrayList<UserDataResponse>?
) {
    override fun toString(): String {
        return "ChatRoomListResponse(chatRoomId=$chatRoomId, unReads=$unReads, chatRoomName=$chatRoomName, chatRoomInfo=$chatRoomInfo, userInfo=$userInfo)"
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