package com.example.coconut.model.service

import com.example.coconut.model.request.MakeChatRoomRequest
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
        @Part("chatRoomId") chatRoomId : RequestBody?,
        @Part("chatUserId") chatUserId : RequestBody?,
        @Part("content") content : RequestBody?,
        @Part images : ArrayList<MultipartBody.Part?>?
    ) : Single<ChatBaseResponse>

    @POST("/chat/room/make")
    fun makeChatRoom(
        @Body makeChatRoomRequest: MakeChatRoomRequest
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