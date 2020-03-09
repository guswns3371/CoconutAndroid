package com.example.coconut.model.service

import com.example.coconut.model.request.MakeChatRoomPostData
import com.example.coconut.model.response.chat.ChatBaseResponse
import com.example.coconut.model.response.chat.ChatHistoryResponse
import com.example.coconut.model.response.chat.ChatListResponse
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ChatService {

    @POST("/chat")
    @Multipart
    fun sendMessage(
        @Part("chat_room_id") chat_id : RequestBody?,
        @Part("chat_user_id") user_id : RequestBody?,
        @Part("content") content : RequestBody?,
        @Part images : ArrayList<MultipartBody.Part?>?
    ) : Single<ChatBaseResponse>

    @POST("/chat/room/make")
    fun makeChatRoom(
        @Body makeChatRoomPostData: MakeChatRoomPostData
    ): Single<ChatBaseResponse>

    @GET("/chat/{id}")
    fun getChatHistory(
        @Path("id") chatRoomId : String?
    ): Single<ArrayList<ChatHistoryResponse>>

    @GET("/chat/list/{id}")
    fun getChatRoomLists(
        @Path("id") myId : String?
    ) : Single<ArrayList<ChatListResponse>>
}