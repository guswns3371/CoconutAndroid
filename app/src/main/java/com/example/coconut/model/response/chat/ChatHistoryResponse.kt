package com.example.coconut.model.response.chat

import com.example.coconut.model.response.account.UserDataResponse
import com.google.gson.annotations.SerializedName

data class ChatHistoryResponse(
    @SerializedName("user_info") var user_info : UserDataResponse,
    @SerializedName("chat_room_id") var chat_room_id : String,
    @SerializedName("chat_user_id") var chat_user_id : String,
    @SerializedName("read_people") var read_people : String,
    @SerializedName("time") var time : String,
    @SerializedName("content") var chat_content : String,
    @SerializedName("is_file") var isFile : Boolean?,

    var readPeopleCount : Int?
) {

    override fun toString(): String {
        return "ChatHistoryResponse(user_info=$user_info, chat_room_id='$chat_room_id', chat_user_id='$chat_user_id', read_people='$read_people', time='$time', chat_content='$chat_content', isFile=$isFile, readPeopleCount=$readPeopleCount)"
    }
}