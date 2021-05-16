package com.example.coconut.model.response.chat

import androidx.annotation.Keep
import com.example.coconut.model.response.account.UserDataResponse
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class ChatHistoryResponse(
    @SerializedName("userInfo") var userInfo : UserDataResponse,
    @SerializedName("chatRoomId") var chatRoomId : String,
    @SerializedName("chatUserId") var chatUserId : String,
    @SerializedName("readMembers") var readMembers : String?,
    @SerializedName("time") var time : String,
    @SerializedName("history") var history : String,
    @SerializedName("chatImages") var chatImages : ArrayList<String>?,
    @SerializedName("messageType") var messageType : String?
) {
    override fun toString(): String {
        return "ChatHistoryResponse(userInfo=$userInfo, chatRoomId='$chatRoomId', chatUserId='$chatUserId', readMembers='$readMembers', time='$time', history='$history', chatImages='$chatImages', messageType=$messageType)"
    }
}