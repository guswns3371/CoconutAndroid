package com.example.coconut.model

import com.example.coconut.model.request.account.AccountEditRequest
import com.example.coconut.model.request.auth.EmailVerifyRequest
import com.example.coconut.model.request.auth.LoginRequest
import com.example.coconut.model.request.auth.OAuth2LoginRequest
import com.example.coconut.model.request.auth.RegisterRequest
import com.example.coconut.model.request.chat.ChatRequest
import com.example.coconut.model.request.chat.FcmTokenRequest
import com.example.coconut.model.response.*
import com.example.coconut.model.response.account.UserDataResponse
import com.example.coconut.model.response.auth.LoginResponse
import com.example.coconut.model.response.auth.OAuth2LoginResponse
import com.example.coconut.model.response.auth.RegisterResponse
import com.example.coconut.model.response.chat.ChatBaseResponse
import com.example.coconut.model.response.chat.ChatHistoryResponse
import com.example.coconut.model.response.chat.ChatListResponse
import io.reactivex.Single

interface MyRepository {

    /** @AuthAPI */
    fun googleLogin() : Single<LoginResponse>

    fun naverLogin() : Single<LoginResponse>

    fun doLogin(loginRequest: LoginRequest) : Single<LoginResponse>

    fun checkEmailValidation(email : String) : Single<RegisterResponse>

    fun emailVerify(emailVerifyRequest: EmailVerifyRequest) : Single<LoginResponse>

    fun doRegister(registerRequest: RegisterRequest) : Single<RegisterResponse>

    fun sendFcmTokenToServer(fcmTokenRequest: FcmTokenRequest) : Single<BaseResponse>

    fun deleteFcmTokenFromServer(id : String) : Single<BaseResponse>

    fun sendUserInfoToServer(oAuth2LoginRequest: OAuth2LoginRequest) : Single<OAuth2LoginResponse>

    /** main */

    /** @AccountAPI */
    fun getAccountData(myId: String): Single<ArrayList<UserDataResponse>>

    fun updateAccountData(accountEditRequest: AccountEditRequest) : Single<BaseResponse>

    /** @ChatAPI */
    fun makeChatRoom(myId : String,chatRoomId : String?,people : ArrayList<String>) : Single<ChatBaseResponse>

    fun getChatHistory(chatRoomId : String?) : Single<ArrayList<ChatHistoryResponse>>

    fun sendMessage(chatRequest: ChatRequest): Single<ChatBaseResponse>

    fun getChatRoomLists(myId: String?) : Single<ArrayList<ChatListResponse>>
}