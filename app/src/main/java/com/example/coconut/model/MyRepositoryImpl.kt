package com.example.coconut.model

import com.example.coconut.model.request.account.AccountEditRequest
import com.example.coconut.model.request.auth.EmailVerifyRequest
import com.example.coconut.model.request.auth.LoginRequest
import com.example.coconut.model.request.auth.RegisterRequest
import com.example.coconut.model.request.chat.ChatMessageRequest
import com.example.coconut.model.request.chat.FcmTokenRequest
import com.example.coconut.model.request.chat.ChatRoomSaveRequest
import com.example.coconut.model.response.*
import com.example.coconut.model.response.account.UserDataResponse
import com.example.coconut.model.response.auth.LoginResponse
import com.example.coconut.model.response.auth.RegisterResponse
import com.example.coconut.model.response.chat.ChatRoomSaveResponse
import com.example.coconut.model.response.chat.ChatHistoryResponse
import com.example.coconut.model.response.chat.ChatRoomListResponse
import com.example.coconut.model.api.AuthAPI
import com.example.coconut.model.api.AccountAPI
import com.example.coconut.model.api.ChatAPI
import com.example.coconut.model.request.auth.OAuth2LoginRequest
import com.example.coconut.model.request.chat.ChatRoomInfoRequest
import com.example.coconut.model.response.auth.OAuth2LoginResponse
import io.reactivex.Single

class MyRepositoryImpl(private val authAPI: AuthAPI,
                       private val accountAPI: AccountAPI,
                       private val chatAPI: ChatAPI
                       ) : MyRepository {


    /** authService*/

    override fun googleLogin() : Single<LoginResponse> {
        return authAPI.googleLogin()
    }

    override fun naverLogin() : Single<LoginResponse> {
        return authAPI.naverLogin()
    }

    override fun doLogin(loginRequest: LoginRequest)
            : Single<LoginResponse> {
        return authAPI.login(loginRequest)
    }

    override fun checkEmailValidation(email: String)
            : Single<RegisterResponse> {
        return authAPI.checkEmailValidation(email)
    }

    override fun emailVerify(emailVerifyRequest: EmailVerifyRequest): Single<LoginResponse> {
        return authAPI.emailVerify(emailVerifyRequest)
    }

    override fun doRegister(registerRequest: RegisterRequest)
            : Single<RegisterResponse> {
        return authAPI.register(registerRequest)
    }

    override fun sendFcmTokenToServer(fcmTokenRequest: FcmTokenRequest): Single<BaseResponse> {
        return authAPI.fcmTokenToServer(fcmTokenRequest)
    }

    override fun deleteFcmTokenFromServer(id: String): Single<BaseResponse> {
        return authAPI.deleteFcmTokenFromServer(id)
    }

    override fun sendUserInfoToServer(oAuth2LoginRequest: OAuth2LoginRequest): Single<OAuth2LoginResponse> {
        return authAPI.sendUserInfoToServer(oAuth2LoginRequest)
    }

    /** mainService*/
    override fun getAccountData(myId: String)
            : Single<ArrayList<UserDataResponse>> {
        return accountAPI.getAllUserData(myId)
    }

    override fun updateAccountData(accountEditRequest: AccountEditRequest)
            : Single<BaseResponse> {
        return accountAPI.updateAccount(
            accountEditRequest.id,
            accountEditRequest.userId,
            accountEditRequest.name,
            accountEditRequest.message,
            arrayOf(accountEditRequest.profileImage, accountEditRequest.backImage)
        )
    }

    override fun makeChatRoom(
        myId : String,
       people: ArrayList<String>
    ): Single<ChatRoomSaveResponse> {
        return chatAPI.makeChatRoom(
            ChatRoomSaveRequest(myId,people)
        )
    }

    override fun getChatRoomInfo(
        myId: String,
        chatRoomId: String?,
        people: java.util.ArrayList<String>
    ): Single<ChatRoomSaveResponse> {
        return chatAPI.getChatRoomInfo(
            ChatRoomInfoRequest(myId,chatRoomId,people)
        )
    }

    override fun getChatHistory(chatRoomId: String?)
            : Single<ArrayList<ChatHistoryResponse>> {
       return chatAPI.getChatHistory(chatRoomId)
    }

    override fun sendMessage(chatMessageRequest: ChatMessageRequest): Single<ChatRoomSaveResponse> {
        return chatAPI.sendMessage(
            chatMessageRequest.chatRoomId,
            chatMessageRequest.userId,
            chatMessageRequest.chatMessage,
            chatMessageRequest.chatImages
        )
    }

    override fun getChatRoomLists(myId: String?)
            : Single<ArrayList<ChatRoomListResponse>> {
        return chatAPI.getChatRoomLists(myId)
    }
}