package com.example.coconut.ui.main.chat.inner

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.coconut.base.BaseKotlinViewModel
import com.example.coconut.model.MyRepository
import com.example.coconut.model.request.chat.ChatMessageRequest
import com.example.coconut.model.response.chat.ChatRoomSaveResponse
import com.example.coconut.model.response.chat.ChatHistoryResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class InnerChatViewModel(private val myRepository: MyRepository) : BaseKotlinViewModel(){

    private val TAG = "InnerChatViewModel"

    private val _chatRoomSaveResponseLiveData = MutableLiveData<ChatRoomSaveResponse>()
    val chatRoomSaveResponseLiveData : LiveData<ChatRoomSaveResponse> = _chatRoomSaveResponseLiveData

    private val _chatResponseLiveData = MutableLiveData<ArrayList<ChatHistoryResponse>>()
    val chatHistoryResponseLiveData : LiveData<ArrayList<ChatHistoryResponse>> = _chatResponseLiveData

    private val _chatUpdateReadMembersLiveData = MutableLiveData<ArrayList<ChatHistoryResponse>>()
    val chatUpdateReadMembersLiveData : LiveData<ArrayList<ChatHistoryResponse>> = _chatUpdateReadMembersLiveData

    fun getChatHistory(chatRoomId : String?){
        addDisposable(myRepository.getChatHistory(chatRoomId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it?.run {
                    _chatResponseLiveData.postValue(this)
                }
            },{
                Log.d(TAG, "getChatHistory response error, message : ${it.message}")
                _chatResponseLiveData.postValue(null)
            }))
    }

    fun updateReadMembers(chatRoomId : String?) {
        addDisposable(myRepository.getChatHistory(chatRoomId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it?.run {
                    _chatUpdateReadMembersLiveData.postValue(this)
                }
            },{
                Log.d(TAG, "updateReadMembers response error, message : ${it.message}")
                _chatUpdateReadMembersLiveData.postValue(null)
            }))
    }

    fun makeChatRoom(myId : String, people : ArrayList<String>){
        addDisposable(myRepository.makeChatRoom(myId,people)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it?.run {
                    Log.e(TAG,"makeChatRoom response : ${toString()}")
                    _chatRoomSaveResponseLiveData.postValue(this)
                }
            },{
                Log.d(TAG, "makeChatRoom response error, message : ${it.message}")
            }))
    }

    fun getChatRoomInfo(myId : String, chatRoomId: String?, people : ArrayList<String>){
        addDisposable(myRepository.getChatRoomInfo(myId,chatRoomId,people)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it?.run {
                    Log.e(TAG,"getChatRoomInfo response : ${toString()}")
                    _chatRoomSaveResponseLiveData.postValue(this)
                }
            },{
                Log.d(TAG, "getChatRoomInfo response error, message : ${it.message}")
            }))
    }

    fun sendMessage(chatMessageRequest: ChatMessageRequest){
        addDisposable(myRepository.sendMessage(chatMessageRequest)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it?.run {
                    Log.e(TAG,"sendMessage response : ${toString()}")
                    _chatRoomSaveResponseLiveData.postValue(this)
                }
            },{
                Log.d(TAG, "sendMessage response error, message : ${it.message}")
            }))
    }

}