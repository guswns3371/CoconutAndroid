package com.example.coconut.model.response.chat

import com.example.coconut.model.response.account.UserDataResponse
import com.google.gson.annotations.SerializedName

data class ChatHistoryResponse(
    @SerializedName("userInfo") var userInfo : UserDataResponse,
    @SerializedName("chatRoomId") var chatRoomId : String,
    @SerializedName("chatUserId") var chatUserId : String,
    @SerializedName("readMembers") var readMembers : String,
    @SerializedName("time") var time : String,
    @SerializedName("history") var history : String,
    @SerializedName("isFile") var isFile : Boolean?,
    var readPeopleCount : Int?
) {

    override fun toString(): String {
        return "ChatHistoryResponse(user_info=$userInfo, chat_room_id='$chatRoomId', chat_user_id='$chatUserId', read_people='$readMembers', time='$time', chat_content='$history', isFile=$isFile, readPeopleCount=$readPeopleCount)"
    }
}