package com.example.coconut.model

import com.example.coconut.model.request.*
import com.example.coconut.model.response.*
import com.example.coconut.model.response.account.UserDataResponse
import com.example.coconut.model.response.auth.LoginResponse
import com.example.coconut.model.response.auth.RegisterResponse
import com.example.coconut.model.response.chat.ChatBaseResponse
import com.example.coconut.model.response.chat.ChatHistoryResponse
import com.example.coconut.model.response.chat.ChatListResponse
import com.example.coconut.model.service.AuthService
import com.example.coconut.model.service.AccountService
import com.example.coconut.model.service.ChatService
import io.reactivex.Single

class MyRepositoryImpl(private val authService: AuthService,
                       private val accountService: AccountService,
                       private val chatService: ChatService
                       ) : MyRepository {


    /** authService*/

    override fun googleLogin() : Single<LoginResponse> {
        return authService.googleLogin()
    }

    override fun naverLogin() : Single<LoginResponse> {
        return authService.naverLogin()
    }

    override fun doLogin(loginRequest: LoginRequest)
            : Single<LoginResponse> {
        return authService.login(loginRequest)
    }

    override fun checkEmailValidation(email: String)
            : Single<RegisterResponse> {
        return authService.checkEmailValidation(email)
    }

    override fun emailVerify(emailVerifyRequest: EmailVerifyRequest): Single<LoginResponse> {
        return authService.emailVerify(emailVerifyRequest)
    }

    override fun doRegister(registerRequest: RegisterRequest)
            : Single<RegisterResponse> {
        return authService.register(registerRequest)
    }

    override fun sendFcmTokenToServer(fcmTokenRequest: FcmTokenRequest): Single<BaseResponse> {
        return authService.fcmTokenToServer(fcmTokenRequest)
    }

    override fun deleteFcmTokenFromServer(id: String): Single<BaseResponse> {
        return authService.deleteFcmTokenFromServer(id)
    }

    /** mainService*/
    override fun getAccountDatas(myId: String)
            : Single<ArrayList<UserDataResponse>> {
        return accountService.getAllUserDatas(myId)
    }

    override fun updateAccountData(accountEditRequest: AccountEditRequest)
            : Single<BaseResponse> {
        return accountService.updateAccount(
            accountEditRequest.id,
            accountEditRequest.userId,
            accountEditRequest.userName,
            accountEditRequest.userMsg,
            arrayOf(accountEditRequest.userImg, accountEditRequest.backImg)
        )
    }

    override fun makeChatRoom(
        myId : String,
        chatRoomId: String?,
        people: ArrayList<String>
    ): Single<ChatBaseResponse> {
        return chatService.makeChatRoom(
            MakeChatRoomRequest(myId,chatRoomId,people)
        )
    }

    override fun getChatHistory(chatRoomId: String?)
            : Single<ArrayList<ChatHistoryResponse>> {
       return chatService.getChatHistory(chatRoomId)
    }

    override fun sendMessage(chatRequest: ChatRequest): Single<ChatBaseResponse> {
        return chatService.sendMessage(
            chatRequest.chatRoomId,
            chatRequest.id,
            chatRequest.chatMessage,
            chatRequest.chatImages
        )
    }

    override fun getChatRoomLists(myId: String?)
            : Single<ArrayList<ChatListResponse>> {
        return chatService.getChatRoomLists(myId)
    }
}