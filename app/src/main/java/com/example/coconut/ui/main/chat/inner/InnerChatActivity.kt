package com.example.coconut.ui.main.chat.inner

import android.app.Dialog
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coconut.*
import com.example.coconut.adapter.InnerChatRecyclerAdapter
import com.example.coconut.base.BaseKotlinActivity
import com.example.coconut.base.SocketServiceManager
import com.example.coconut.databinding.ActivityInnerChatBinding
import com.example.coconut.model.response.chat.ChatHistoryResponse
import com.example.coconut.model.socket.ChatSocketData
import com.example.coconut.service.SocketService
import com.example.coconut.util.*
import kotlinx.android.synthetic.main.activity_inner_chat.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception

class InnerChatActivity : BaseKotlinActivity<ActivityInnerChatBinding,InnerChatViewModel>(),SocketServiceManager{

    private var chatMode : Int = -1
    private val TAG = "InnerChatActivity"
    override var toolbar: Toolbar? = null

    override val layoutResourceId: Int = R.layout.activity_inner_chat
    override val viewModel: InnerChatViewModel by viewModel()
    private val pref : MyPreference by inject()
    private val recyclerAdapter : InnerChatRecyclerAdapter by inject()
    private var progressDialog : Dialog? = null

    override var isBind: Boolean = false
    override var socket: Socket? = null
    override val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.e("serviceConn","onServiceDisconnected")
            socket = null
            isBind = false
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.e("serviceConn","onServiceConnected")
            val binder = service as SocketService.MyBinder
            socket = binder.getService().mySocket()
            isBind = true
            socketForChat()
        }
    }

    private lateinit var myIdPref: String
    private var partnerIdIntent : String? = null
    private var peopleIdsIntent : ArrayList<String>? = null

    private var charRoomId : String? = null
    private var fixedPeopleList : ArrayList<String> = arrayListOf()
    private var currentOnlinePeopleList : ArrayList<String> = arrayListOf()
    private var isHistoryLoaded : Boolean = false

    private var chatHistoryList : ArrayList<ChatHistoryResponse> = arrayListOf()

    override fun initStartView() {

        viewDataBinding.lifecycleOwner = this

        toolbar = findViewById(R.id.chatToolbar)
        intent.getStringExtra(IntentID.CHAT_ROOM_TITLE)?.let {
            setToolbarTitle(it)
        }
        chat_recycler_view.apply {
            layoutManager = LinearLayoutManager(this@InnerChatActivity).apply {
                stackFromEnd = true
            }
            adapter = recyclerAdapter
            setHasFixedSize(true)

        }

        myIdPref = pref.UserId!!
        chatMode = intent.getIntExtra(IntentID.CHAT_MODE,-1)

        // charRoomId와 fixedPeopleList 를 설정한다
        whereChatFrom()

        // chat room을 만든다 , 이미 존재하면 만들지 않고 (서버에서)
        // 채팅방을 들어왔다가 메시지를 전송하지 않아도 서버에서 채팅방이 만들어진다
        viewModel.makeChatRoom(myIdPref,charRoomId,fixedPeopleList)

        // 채팅방 id를 얻기 전까지 채팅버튼 비활성화
        chat_send.isClickable = false
        bindService(this)
    }

    override fun initDataBinding() {
        // makeChatRoom, sendMessage
        viewModel.cBaseResponseLiveData.observe(this, Observer {
            if (it.success){

                Log.e(TAG,"채팅방 id : ${it.ChatRoomIdResponse}")
                charRoomId = it.ChatRoomIdResponse

                Log.e(TAG,"채팅방 이름 : ${it.ChatRoomNameResponse}")
                setToolbarTitle(it.ChatRoomNameResponse)

                Log.e(TAG,"채팅방 사람들 : ${it.ChatRoomPeopleResponse}")

                (it.ChatRoomPeopleResponse).split(",").toTypedArray().run {
                    fixedPeopleList = this.toCollection(ArrayList())
                }

                Log.e(TAG,"업데이트된 peopleListForSend : ${fixedPeopleList.showElements()}")

                // 여기서 드디어 채팅 기록을 불러온다
                if (!isHistoryLoaded){
                    // 최초 한번만 로드한다
                    // 채팅방 입장
                    enterChatRoom()

                    // 실시간 로드는 소켓통신으로 구현
                    viewModel.getChatHistory(charRoomId)
                    isHistoryLoaded = true

                    //이제 채팅버튼 활성화
                    chat_send.isClickable = true
                    progressDialog = Dialog(this@InnerChatActivity).apply {
                        setContentView(R.layout.custom_loading_dialog)
                        setCancelable(true)
                        show()
                    }
                }
            }
        })

        // getChatHistory
        viewModel.chatHistoryResponseLiveData.observe(this, Observer {
            Log.e(TAG,"getChatHistory observing")

            progressDialog!!.dismiss()

            it.forEach { chat->
                chatHistoryList.add(chat)
            }
            recyclerAdapter.addChatItem(chatHistoryList)
            recyclerAdapter.updateChatReadCount(fixedPeopleList)
        })
    }

    override fun initAfterBinding() {
        //메시지 보내기
        chat_send.setOnClickListener {
            sendMessage()
        }

        // 키보드에서 완료버튼 누를때에도 메시지 보내기
        chat_edit_text.onDone {
            sendMessage()
        }
    }

    override fun onDestroy() {
        //채팅방 나감
        exitChatRoom()
        offSocket()
        unbindService(this)
        super.onDestroy()
    }

    private fun whereChatFrom(){
        //peopleListForSend 설정함
        when(chatMode){
            IntentID.CHAT_WITH_ME->{
                // 나와의 채팅
                myIdPref.let {
                    Log.e(TAG,"CHAT_WITH_ME")
                    partnerIdIntent = it
                    fixedPeopleList.add(it)
                }
            }
            IntentID.CHAT_WITH_ONE_PARTNER->{
                // 1:1 채팅
                intent.getStringExtra(IntentID.ID)?.let {
                    Log.e(TAG,"CHAT_WITH_ONE_PARTNER")
                    partnerIdIntent = it
                    // AccountInfo에서 채팅을 클릭한 것
                    // 1 : 1 채팅
                    // ["myid","partnerid"]
                    fixedPeopleList.add(myIdPref)
                    fixedPeopleList.add(it)
                }


            }
            IntentID.CHAT_WITH_PEOPLE_FROM_CHAT_FRAG->{
                // chatfragment에서 들어옴
                // 채팅방에서 나가기 할경우도 또는 새로 초대할경우
                intent.getStringExtra(IntentID.CHAT_ROOM_ID)?.let {
                    Log.e(TAG,"CHAT_WITH_PEOPLE_FROM_CHAT_FRAG")
                    charRoomId = it
                }


                intent.getStringExtra(IntentID.CHAT_ROOM_PEOPLE_LIST)?.let {
                    it.split(",").toTypedArray().run {
                        forEach { id->
                            // 이렇게 해줘야 "2 가 2로
                            // 3] 이 3으로 , [3 이 3으로 바뀐다
                            fixedPeopleList.add(id.toCleanString())
                        }
                    }
                }

            }
            IntentID.CHAT_WITH_PEOPLE_FROM_INVITING->{
                // 채팅방 막 만듦
                // 여러 유저의 id가 넘어온다
                // ["myid","p1","p2" ... ]
                // 채팅방에서 나가기 할경우도 또는 새로 초대할경우
                intent.getStringArrayListExtra(IntentID.PEOPLE_IDS)?.let {
                    Log.e(TAG,"CHAT_WITH_MANY_FIRST")
                    fixedPeopleList = it
                    fixedPeopleList.add(myIdPref)
                }
            }
            -1->{
                Log.e(TAG,"CHAT_MODE ERROR")
                throw Exception()
            }
        }

        // 오르차순 배열
        fixedPeopleList.sortBy { it }
        Log.e(TAG, "[size : ${fixedPeopleList.size}] ${fixedPeopleList.showElements()}")
    }

    private fun socketForChat(){
        if (socket == null) Log.e(TAG,"socket is null")
        socket?.apply {
            on(SocketReceive.CHATROOM_ENTER,onChatroomEnter)
            on(SocketReceive.CHATROOM_EXIT,onChatroomExit)
            on(SocketReceive.CHAT_MESSAGE,onChatMessageReceive)

            // 채팅방에서 네트워크 오류 발생시 나갔다가 들어오도록
            on(Socket.EVENT_CONNECT,onConnect)
            on(Socket.EVENT_DISCONNECT,onDisconnect)
        }
    }

    private fun offSocket(){
        if (socket == null) Log.e(TAG,"socket is null")
        socket?.apply {
            off(SocketReceive.CHATROOM_ENTER,onChatroomEnter)
            off(SocketReceive.CHATROOM_EXIT,onChatroomExit)
            off(SocketReceive.CHAT_MESSAGE,onChatMessageReceive)

            // 채팅방에서 네트워크 오류 발생시 나갔다가 들어오도록
            off(Socket.EVENT_CONNECT,onConnect)
            off(Socket.EVENT_DISCONNECT,onDisconnect)
        }
    }
    private val onChatroomEnter = Emitter.Listener {
        runOnUiThread {
            (it[0] as String).apply {
                this.split(",").toTypedArray().run {
                    currentOnlinePeopleList = this.toCollection(ArrayList())
                }
                Log.e(TAG,"[enter] current People ${currentOnlinePeopleList.showElements()}")
            }
        }
    }

    private val onChatroomExit = Emitter.Listener {
        runOnUiThread {
            (it[0] as String).apply {
                this.split(",").toTypedArray().run {
                    currentOnlinePeopleList = this.toCollection(ArrayList())
                }
                Log.e(TAG,"[exit] current People ${currentOnlinePeopleList.showElements()}")
            }
        }
    }

    private val onConnect = Emitter.Listener {
        runOnUiThread {
            //exitChatRoom()
            enterChatRoom()
            chat_send.isClickable = true
        }
    }

    private val onDisconnect = Emitter.Listener {
        runOnUiThread{
            chat_send.isClickable = false
            exitChatRoom()
        }
    }
    private val onChatMessageReceive = Emitter.Listener {
        runOnUiThread {
            (it[0] as JSONObject).apply {
                Log.e(TAG,"[chat] received message : $this")
              // Log.e(TAG,Gson().fromJson("$this",ChatSocketData::class.java).toString())

                val message = this.toObject(ChatHistoryResponse::class.java)
                chatHistoryList.add(message)
                recyclerAdapter.addChatItem(chatHistoryList)

                scrollToBottom()
            }
        }
    }

    private fun enterChatRoom(){
        Log.e(TAG,"enter")
        try {
            JSONObject().apply {
                put(SocketData.USER_ID,pref.UserId)
                put(SocketData.CHAT_ROOM_ID,charRoomId)
                socket?.emit(SocketSend.CHATROOM_ENTER,this)
            }
        }catch (e: JSONException){
            Log.e(TAG,"${e.message}")
        }
    }

    private fun exitChatRoom(){
        Log.e(TAG,"exit")
        try {
            JSONObject().apply {
                put(SocketData.USER_ID,pref.UserId)
                put(SocketData.CHAT_ROOM_ID,charRoomId)
                socket?.emit(SocketSend.CHATROOM_EXIT,this)
            }
        }catch (e: JSONException){
            Log.e(TAG,"${e.message}")
        }
    }

    private fun sendMessage(){
        if (!chat_edit_text.text.toString().isBlank()){
            ChatSocketData(charRoomId,myIdPref,chat_edit_text.text.toString(),fixedPeopleList,currentOnlinePeopleList,null)
                .toJSONObject()?.let {
                    socket?.emit(SocketSend.CHAT_MESSAGE,it)
                    chat_edit_text.text.clear()
                }
            scrollToBottom()
        }
    }
    private fun scrollToBottom(){
        chat_recycler_view.postDelayed({
            chat_recycler_view.scrollToPosition(chat_recycler_view.adapter!!.itemCount -1)
        },200)
    }
}
