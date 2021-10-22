package com.example.coconut.ui.main.chat.inner

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.coconut.base.BaseKotlinViewModel
import com.example.coconut.model.MyRepository
import com.example.coconut.model.request.chat.ChatMessageRequest
import com.example.coconut.model.request.chat.ChatRoomDataRequest
import com.example.coconut.model.request.chat.ChatRoomSaveRequest
import com.example.coconut.model.request.chat.ChatUploadImageRequest
import com.example.coconut.model.response.chat.ChatRoomDataResponse
import com.example.coconut.model.response.chat.ChatHistoryResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class InnerChatViewModel(private val myRepository: MyRepository) : BaseKotlinViewModel() {

    private val TAG = "InnerChatViewModel"

    private val _chatRoomDataResponseLiveData = MutableLiveData<ChatRoomDataResponse>()
    val chatRoomDataResponseLiveData: LiveData<ChatRoomDataResponse> = _chatRoomDataResponseLiveData

    private val _chatUpdateRoomDataLiveData = MutableLiveData<ChatRoomDataResponse>()
    val chatUpdateRoomDataLiveData: LiveData<ChatRoomDataResponse> =
        _chatUpdateRoomDataLiveData

    private val _chatResponseLiveData = MutableLiveData<ArrayList<ChatHistoryResponse>>()
    val chatHistoryResponseLiveData: LiveData<ArrayList<ChatHistoryResponse>> =
        _chatResponseLiveData

    private val _chatUpdateHistoryLiveData = MutableLiveData<ArrayList<ChatHistoryResponse>>()
    val chatUpdateHistoryLiveData: LiveData<ArrayList<ChatHistoryResponse>> =
        _chatUpdateHistoryLiveData

    private val _chatUploadImagesLiveData = MutableLiveData<ArrayList<String>>()
    val chatUploadImagesLiveData: LiveData<ArrayList<String>> =
        _chatUploadImagesLiveData

    fun getChatHistory(chatRoomId: String?) {
        addDisposable(
            myRepository.getChatHistory(chatRoomId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it?.run {
                        _chatResponseLiveData.postValue(this)
                    }
                }, {
                    Log.d(TAG, "getChatHistory response error, message : ${it.message}")
                    _chatResponseLiveData.postValue(null)
                })
        )
    }

    fun updateHistory(chatRoomId: String?) {
        addDisposable(
            myRepository.getChatHistory(chatRoomId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it?.run {
                        _chatUpdateHistoryLiveData.postValue(this)
                    }
                }, {
                    Log.d(TAG, "updateReadMembers response error, message : ${it.message}")
                    _chatUpdateHistoryLiveData.postValue(null)
                })
        )
    }

    fun makeChatRoom(chatRoomSaveRequest: ChatRoomSaveRequest) {
        addDisposable(
            myRepository.makeChatRoom(chatRoomSaveRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it?.run {
                        Log.e(TAG, "makeChatRoom response : ${toString()}")
                        _chatRoomDataResponseLiveData.postValue(this)
                    }
                }, {
                    Log.d(TAG, "makeChatRoom response error, message : ${it.message}")
                })
        )
    }

    fun getChatRoomData(roomID: String?, userId: String?) {
        addDisposable(
            myRepository.getChatRoomData(roomID, userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it?.run {
                        Log.e(TAG, "getChatRoomInfo response : ${toString()}")
                        _chatRoomDataResponseLiveData.postValue(this)
                    }
                }, {
                    Log.d(TAG, "getChatRoomInfo response error, message : ${it.message}")
                })
        )
    }

    fun updateChatRoomData(roomID: String?, userId: String?) {
        addDisposable(
            myRepository.getChatRoomData(roomID, userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it?.run {
                        Log.e(TAG, "updateChatRoomData response : ${toString()}")
                        _chatUpdateRoomDataLiveData.postValue(this)
                    }
                }, {
                    Log.d(TAG, "updateChatRoomData response error, message : ${it.message}")
                })
        )
    }

    fun inviteUser(chatRoomDataRequest: ChatRoomDataRequest) {
        addDisposable(
            myRepository.inviteUser(chatRoomDataRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it?.run {
                        Log.e(TAG, "updateChatRoomData response : ${toString()}")
                        _chatUpdateRoomDataLiveData.postValue(this)
                    }
                }, {
                    Log.d(TAG, "updateChatRoomData response error, message : ${it.message}")
                })
        )
    }

    fun uploadChatImages(chatUploadImageRequest: ChatUploadImageRequest) {
        addDisposable(
            myRepository.uploadChatImages(chatUploadImageRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it.run {
                        Log.e(TAG, "response : \n${toString()}")
                        _chatUploadImagesLiveData.postValue(this)
                    }
                }, {
                    Log.e(TAG, "response error, message : ${it.message}")
                })
        )
    }

}