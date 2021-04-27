package com.example.coconut.model.api

import com.example.coconut.model.request.chat.ChatRoomInfoRequest
import com.example.coconut.model.request.chat.ChatRoomSaveRequest
import com.example.coconut.model.response.chat.ChatRoomSaveResponse
import com.example.coconut.model.response.chat.ChatHistoryResponse
import com.example.coconut.model.response.chat.ChatRoomListResponse
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ChatAPI {

    @POST("/api/chat")
    @Multipart
    fun sendMessage(
        @Part("chatRoomId") chatRoomId : RequestBody?,
        @Part("chatUserId") chatUserId : RequestBody?,
        @Part("content") content : RequestBody?,
        @Part images : ArrayList<MultipartBody.Part?>?
    ) : Single<ChatRoomSaveResponse>

    @POST("/api/chat/room/make")
    fun makeChatRoom(
        @Body chatRoomSaveRequest: ChatRoomSaveRequest
    ): Single<ChatRoomSaveResponse>

    @POST("/api/chat/room/info")
    fun getChatRoomInfo(
        @Body chatRoomInfoRequest: ChatRoomInfoRequest
    ) : Single<ChatRoomSaveResponse>

    @GET("/api/chat/{id}")
    fun getChatHistory(
        @Path("id") chatRoomId : String?
    ): Single<ArrayList<ChatHistoryResponse>>

    @GET("/api/chat/list/{id}")
    fun getChatRoomLists(
        @Path("id") myId : String?
    ) : Single<ArrayList<ChatRoomListResponse>>
}