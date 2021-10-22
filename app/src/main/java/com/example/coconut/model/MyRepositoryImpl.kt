package com.example.coconut.model

import com.example.coconut.model.request.account.AccountEditRequest
import com.example.coconut.model.response.*
import com.example.coconut.model.response.account.UserDataResponse
import com.example.coconut.model.response.auth.LoginResponse
import com.example.coconut.model.response.auth.RegisterResponse
import com.example.coconut.model.response.chat.ChatRoomDataResponse
import com.example.coconut.model.response.chat.ChatHistoryResponse
import com.example.coconut.model.response.chat.ChatRoomListResponse
import com.example.coconut.model.api.AuthAPI
import com.example.coconut.model.api.AccountAPI
import com.example.coconut.model.api.ChatAPI
import com.example.coconut.model.api.CrawlAPI
import com.example.coconut.model.request.auth.*
import com.example.coconut.model.request.chat.*
import com.example.coconut.model.response.auth.OAuth2LoginResponse
import com.example.coconut.model.response.hashtag.*
import io.reactivex.Single

class MyRepositoryImpl(
    private val authAPI: AuthAPI,
    private val accountAPI: AccountAPI,
    private val chatAPI: ChatAPI,
    private val crawlAPI: CrawlAPI
) : MyRepository {


    /** authService*/
    override fun doLogin(loginRequest: LoginRequest)
            : Single<LoginResponse> {
        return authAPI.login(loginRequest)
    }

    override fun checkEmailValidation(email: EmailCheckRequest)
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

    override fun makeChatRoom(chatRoomSaveRequest: ChatRoomSaveRequest): Single<ChatRoomDataResponse> {
        return chatAPI.makeChatRoom(chatRoomSaveRequest)
    }

    override fun getChatRoomData(chatRoomId: String?, userId: String?): Single<ChatRoomDataResponse> {
        return chatAPI.getChatRoomData(chatRoomId, userId)
    }

    override fun getChatHistory(chatRoomId: String?)
            : Single<ArrayList<ChatHistoryResponse>> {
        return chatAPI.getChatHistory(chatRoomId)
    }

    override fun getChatRoomLists(userId: String?)
            : Single<ArrayList<ChatRoomListResponse>> {
        return chatAPI.getChatRoomLists(userId)
    }

    override fun uploadChatImages(chatUploadImageRequest: ChatUploadImageRequest): Single<ArrayList<String>> {
        return chatAPI.uploadChatImages(
            chatUploadImageRequest.userId,
            chatUploadImageRequest.chatRoomId,
            chatUploadImageRequest.images
        )
    }

    override fun changeChatRoomName(chatRoomNameChangeRequest: ChatRoomNameChangeRequest): Single<Boolean> {
        return chatAPI.changeChatRoomName(chatRoomNameChangeRequest)
    }

    override fun exitChatRoom(chatRoomExitRequest: ChatRoomExitRequest): Single<Boolean> {
        return chatAPI.exitChatRoom(chatRoomExitRequest)
    }

    override fun inviteUser(chatRoomDataRequest: ChatRoomDataRequest): Single<ChatRoomDataResponse> {
        return chatAPI.inviteUser(chatRoomDataRequest)
    }

    override fun getCovidData(): Single<ArrayList<CovidDataResponse>> {
        return crawlAPI.getCovidData()
    }

    override fun getNewsData(): Single<ArrayList<NewsDataResponse>> {
        return crawlAPI.getNewsData()
    }

    override fun getMusicTopList(): Single<ArrayList<MusicDataResponse>> {
        return crawlAPI.getMusicTopList()
    }

    override fun getSeoulTechList(): Single<ArrayList<NoticeDataResponse>> {
        return crawlAPI.getSeoulTechList()
    }

    override fun getJobList(): Single<ArrayList<JobDataResponse>> {
        return crawlAPI.getJobList()
    }
}