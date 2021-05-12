package com.example.coconut.model.socket

data class ChatMessageSocketData(
    var chatRoomId: String,
    var chatUserId: String,
    var chatMessage: String?,
    var chatRoomMembers: ArrayList<String>?,
    var readMembers: ArrayList<String>?,
    var chatImages: ArrayList<String>?,
    var messageType: String
) {
}