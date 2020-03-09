package com.example.coconut.model

import com.example.coconut.model.request.*
import com.example.coconut.model.response.*
import com.example.coconut.model.response.account.UserDataResponse
import com.example.coconut.model.response.auth.LoginResponse
import com.example.coconut.model.response.auth.RegisterResponse
import com.example.coconut.model.response.chat.ChatBaseResponse
import com.example.coconut.model.response.chat.ChatHistoryResponse
import com.example.coconut.model.response.chat.ChatListResponse
import io.reactivex.Single

interface MyRepository {

    /** auth */
    fun doLogin(loginPostData: LoginPostData) : Single<LoginResponse>

    fun checkEmailValidation(email : String) : Single<RegisterResponse>

    fun emailVerify(emailVerifyPostData: EmailVerifyPostData) : Single<LoginResponse>

    fun doRegister(registerPostData: RegisterPostData) : Single<RegisterResponse>

    /** main*/

    /** account*/
    fun getAccountDatas(myId: String): Single<ArrayList<UserDataResponse>>

    fun updateAccountData(accountEditPostData: AccountEditPostData) : Single<BaseResponse>

    /** chat*/

    fun makeChatRoom(myId : String,chatRoomId : String?,people : ArrayList<String>) : Single<ChatBaseResponse>

    fun getChatHistory(chatRoomId : String?) : Single<ArrayList<ChatHistoryResponse>>

    fun sendMessage(chatPostData: ChatPostData): Single<ChatBaseResponse>

    fun getChatRoomLists(myId: String?) : Single<ArrayList<ChatListResponse>>
}