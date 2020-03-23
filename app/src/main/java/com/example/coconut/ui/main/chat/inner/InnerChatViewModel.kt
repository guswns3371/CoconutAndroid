package com.example.coconut.ui.main.chat.inner

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.coconut.base.BaseKotlinViewModel
import com.example.coconut.model.MyRepository
import com.example.coconut.model.request.ChatPostData
import com.example.coconut.model.response.chat.ChatBaseResponse
import com.example.coconut.model.response.chat.ChatHistoryResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class InnerChatViewModel(private val myRepository: MyRepository) : BaseKotlinViewModel(){

    private val TAG = "InnerChatViewModel"

    private val _cBaseResponseLiveData = MutableLiveData<ChatBaseResponse>()
    val cBaseResponseLiveData : LiveData<ChatBaseResponse> = _cBaseResponseLiveData

    private val _chatResponseLiveData = MutableLiveData<ArrayList<ChatHistoryResponse>>()
    val chatHistoryResponseLiveData : LiveData<ArrayList<ChatHistoryResponse>> = _chatResponseLiveData

    fun getChatHistory(chatRoomId : String?){
        addDisposable(myRepository.getChatHistory(chatRoomId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it?.run {
                    forEach {history ->
                        //Log.e(TAG,"getChatHistory response : [${history.user_info.name}] ${history.chat_content}\n")
                    }
                    _chatResponseLiveData.postValue(this)
                }
            },{
                Log.d(TAG, "getChatHistory response error, message : ${it.message}")
            }))
    }

    fun makeChatRoom(myId : String,chatRoomId : String?,people : ArrayList<String>){
        addDisposable(myRepository.makeChatRoom(myId,chatRoomId,people)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it?.run {
                    Log.e(TAG,"makeChatRoom response : ${toString()}")
                    _cBaseResponseLiveData.postValue(this)
                }
            },{
                Log.d(TAG, "makeChatRoom response error, message : ${it.message}")
            }))
    }

    fun sendMessage(chatPostData: ChatPostData){
        addDisposable(myRepository.sendMessage(chatPostData)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it?.run {
                    Log.e(TAG,"sendMessage response : ${toString()}")
                    _cBaseResponseLiveData.postValue(this)
                }
            },{
                Log.d(TAG, "sendMessage response error, message : ${it.message}")
            }))
    }

}