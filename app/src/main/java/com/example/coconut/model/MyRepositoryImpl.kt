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
    override fun doLogin(loginPostData: LoginPostData)
            : Single<LoginResponse> {
        return authService.login(loginPostData)
    }

    override fun checkEmailValidation(email: String)
            : Single<RegisterResponse> {
        return authService.checkEmailValidation(email)
    }

    override fun emailVerify(emailVerifyPostData: EmailVerifyPostData): Single<LoginResponse> {
        return authService.emailVerify(emailVerifyPostData)
    }

    override fun doRegister(registerPostData: RegisterPostData)
            : Single<RegisterResponse> {
        return authService.register(registerPostData)
    }

    override fun sendFcmTokenToServer(fcmTokenPostData: FcmTokenPostData): Single<BaseResponse> {
        return authService.fcmTokenToServer(fcmTokenPostData)
    }

    override fun deleteFcmTokenFromServer(id: String): Single<BaseResponse> {
        return authService.deleteFcmTokenFromServer(id)
    }

    /** mainService*/
    override fun getAccountDatas(myId: String)
            : Single<ArrayList<UserDataResponse>> {
        return accountService.getAllUserDatas(myId)
    }

    override fun updateAccountData(accountEditPostData: AccountEditPostData)
            : Single<BaseResponse> {
        return accountService.updateAccount(
            accountEditPostData.id,
            accountEditPostData.user_id,
            accountEditPostData.user_name,
            accountEditPostData.user_msg,
            arrayOf(accountEditPostData.user_img, accountEditPostData.back_img)
        )
    }

    override fun makeChatRoom(
        myId : String,
        chatRoomId: String?,
        people: ArrayList<String>
    ): Single<ChatBaseResponse> {
        return chatService.makeChatRoom(
            MakeChatRoomPostData(myId,chatRoomId,people)
        )
    }

    override fun getChatHistory(chatRoomId: String?)
            : Single<ArrayList<ChatHistoryResponse>> {
       return chatService.getChatHistory(chatRoomId)
    }

    override fun sendMessage(chatPostData: ChatPostData): Single<ChatBaseResponse> {
        return chatService.sendMessage(
            chatPostData.chat_room_id,
            chatPostData.id,
            chatPostData.chat_message,
            chatPostData.chat_images
        )
    }

    override fun getChatRoomLists(myId: String?)
            : Single<ArrayList<ChatListResponse>> {
        return chatService.getChatRoomLists(myId)
    }
}