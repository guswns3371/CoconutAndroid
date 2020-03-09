package com.example.coconut.ui.main.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.coconut.base.BaseKotlinViewModel
import com.example.coconut.model.MyRepository
import com.example.coconut.model.response.chat.ChatListResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ChatViewModel(private val myRepository: MyRepository) : BaseKotlinViewModel() {

    private val TAG = "ChatViewModel"
    private val _chatListResponseLiveData = MutableLiveData<ArrayList<ChatListResponse>>()
    val chatListResponseLiveData : LiveData<ArrayList<ChatListResponse>> =_chatListResponseLiveData

    fun getChatRoomLists(myId : String?){
        addDisposable(myRepository.getChatRoomLists(myId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it?.run {
                    forEach {chatList ->
                        val info = chatList.chat_room_info!!
                        Log.e(TAG, "getChatRoomLists response : [${chatList.chat_room_id} 번방] ${chatList.room_name} ${info.last_content} ${info.last_time}\n")
                    }
                    _chatListResponseLiveData.postValue(this)
                }
            },{
                Log.d(TAG, "getChatRoomLists response error, message : ${it.message}")
            }))
    }
}