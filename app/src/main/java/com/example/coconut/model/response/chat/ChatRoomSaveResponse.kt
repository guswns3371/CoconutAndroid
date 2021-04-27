package com.example.coconut.model.response.chat

import com.example.coconut.model.response.account.UserDataResponse
import com.google.gson.annotations.SerializedName

data class ChatRoomSaveResponse(
    @SerializedName("chatRoomId") var chatRoomId: String?,
    @SerializedName("chatRoomName") var chatRoomName: String?,
    @SerializedName("chatRoomMembers") var chatRoomMembers: ArrayList<String>?,
    @SerializedName("chatRoomMembersInfo") var chatRoomMembersInfo: ArrayList<UserDataResponse>?
) {

}