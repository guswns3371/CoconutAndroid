package com.example.coconut.ui.main.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.coconut.base.BaseKotlinViewModel
import com.example.coconut.model.MyRepository
import com.example.coconut.model.request.chat.ChatRoomExitRequest
import com.example.coconut.model.request.chat.ChatRoomNameChangeRequest
import com.example.coconut.model.response.chat.ChatRoomListResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ChatViewModel(private val myRepository: MyRepository) : BaseKotlinViewModel() {

    private val TAG = "ChatViewModel"
    private val _chatListResponseLiveData = MutableLiveData<ArrayList<ChatRoomListResponse>>()
    val chatRoomListResponseLiveData: LiveData<ArrayList<ChatRoomListResponse>> =
        _chatListResponseLiveData

    private val _chatChangeResponseLiveData = MutableLiveData<Boolean>()
    val chatChangeResponseLiveData: LiveData<Boolean> = _chatChangeResponseLiveData

    fun getChatRoomLists(myId: String?) {
        addDisposable(
            myRepository.getChatRoomLists(myId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when {
                        it == null -> {
                            _chatListResponseLiveData.postValue(arrayListOf())
                        }
                        it.size == 0 -> {
                            _chatListResponseLiveData.postValue(arrayListOf())
                        }
                        else -> {
                            _chatListResponseLiveData.postValue(it)
                        }
                    }
                }, {
                    Log.d(TAG, "getChatRoomLists response error, message : ${it.message}")
                })
        )
    }

    fun changeChatRoomName(chatRoomNameChangeRequest: ChatRoomNameChangeRequest) {
        addDisposable(
            myRepository.changeChatRoomName(chatRoomNameChangeRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it?.run {
                        _chatChangeResponseLiveData.postValue(this)
                    }
                }, {
                    Log.d(TAG, "changeChatRoomName response error, message : ${it.message}")
                })
        )
    }

    fun exitChatRoom(chatRoomExitRequest: ChatRoomExitRequest) {
        addDisposable(
            myRepository.exitChatRoom(chatRoomExitRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it?.run {
                        _chatChangeResponseLiveData.postValue(this)
                    }
                }, {
                    Log.d(TAG, "exitChatRoom response error, message : ${it.message}")
                })
        )
    }

}