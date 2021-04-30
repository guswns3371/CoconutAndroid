package com.example.coconut.model.api

import com.example.coconut.model.request.chat.ChatRoomDataRequest
import com.example.coconut.model.request.chat.ChatRoomSaveRequest
import com.example.coconut.model.response.chat.ChatRoomDataResponse
import com.example.coconut.model.response.chat.ChatHistoryResponse
import com.example.coconut.model.response.chat.ChatRoomListResponse
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.http.*

interface ChatAPI {

    @POST("/api/chat")
    @Multipart
    fun sendMessage(
        @Part("chatRoomId") chatRoomId : String?,
        @Part("chatUserId") chatUserId : String?,
        @Part("content") content : String?,
        @Part images : ArrayList<MultipartBody.Part?>?
    ) : Single<ChatRoomDataResponse>

    @POST("/api/chat/room/make")
    fun makeChatRoom(
        @Body chatRoomSaveRequest: ChatRoomSaveRequest
    ): Single<ChatRoomDataResponse>

    @POST("/api/chat/room/info")
    fun getChatRoomData(
        @Body chatRoomDataRequest: ChatRoomDataRequest
    ) : Single<ChatRoomDataResponse>

    @GET("/api/chat/{chatRoomId}")
    fun getChatHistory(
        @Path("chatRoomId") chatRoomId : String?
    ): Single<ArrayList<ChatHistoryResponse>>

    @GET("/api/chat/room/list/{userId}")
    fun getChatRoomLists(
        @Path("userId") userId : String?
    ) : Single<ArrayList<ChatRoomListResponse>>
}