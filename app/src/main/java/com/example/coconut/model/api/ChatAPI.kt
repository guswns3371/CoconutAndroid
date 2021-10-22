package com.example.coconut.model.api

import com.example.coconut.model.request.chat.ChatRoomDataRequest
import com.example.coconut.model.request.chat.ChatRoomExitRequest
import com.example.coconut.model.request.chat.ChatRoomNameChangeRequest
import com.example.coconut.model.request.chat.ChatRoomSaveRequest
import com.example.coconut.model.response.chat.ChatRoomDataResponse
import com.example.coconut.model.response.chat.ChatHistoryResponse
import com.example.coconut.model.response.chat.ChatRoomListResponse
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ChatAPI {
    companion object {
        private const val chat = "/api/chats"
    }

    @POST(chat)
    fun makeChatRoom(
        @Body chatRoomSaveRequest: ChatRoomSaveRequest
    ): Single<ChatRoomDataResponse>

    @GET("${chat}/{id}")
    fun getChatRoomData(
        @Path("id") roomID: String?,
        @Query("userId") userId: String?
    ): Single<ChatRoomDataResponse>

    @GET("${chat}/history/{id}")
    fun getChatHistory(
        @Path("id") chatRoomId: String?
    ): Single<ArrayList<ChatHistoryResponse>>

    @GET("${chat}/users/{userId}")
    fun getChatRoomLists(
        @Path("userId") userId: String?
    ): Single<ArrayList<ChatRoomListResponse>>

    @POST("${chat}/image")
    @Multipart
    fun uploadChatImages(
        @Part("userId") userId: RequestBody?,
        @Part("chatRoomId") chatRoomId: RequestBody?,
        @Part images: ArrayList<MultipartBody.Part?>?
    ): Single<ArrayList<String>>

    @POST("${chat}/name")
    fun changeChatRoomName(
        @Body chatRoomNameChangeRequest: ChatRoomNameChangeRequest
    ): Single<Boolean>

    @POST("${chat}/exit")
    fun exitChatRoom(
        @Body chatRoomExitRequest: ChatRoomExitRequest
    ): Single<Boolean>

    @POST("${chat}/invite")
    fun inviteUser(
        @Body chatRoomDataRequest: ChatRoomDataRequest
    ): Single<ChatRoomDataResponse>


}