package com.example.coconut.ui.main.chat.inner

import android.Manifest
import android.app.Dialog
import android.app.NotificationManager
import android.content.*
import android.os.IBinder
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coconut.*
import com.example.coconut.adapter.InnerChatRecyclerAdapter
import com.example.coconut.adapter.InnerDrawerAdapter
import com.example.coconut.base.BaseKotlinActivity
import com.example.coconut.base.BroadcastReceiverManager
import com.example.coconut.base.SocketServiceManager
import com.example.coconut.databinding.ActivityInnerChatBinding
import com.example.coconut.model.request.chat.ChatRoomDataRequest
import com.example.coconut.model.request.chat.ChatRoomSaveRequest
import com.example.coconut.model.request.chat.ChatUploadImageRequest
import com.example.coconut.model.response.account.UserDataResponse
import com.example.coconut.model.response.chat.ChatHistoryResponse
import com.example.coconut.model.socket.ChatRoomSocketData
import com.example.coconut.model.socket.ChatMessageSocketData
import com.example.coconut.service.SocketService
import com.example.coconut.util.*
import kotlinx.android.synthetic.main.activity_inner_chat.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import io.socket.client.Socket
import com.gmail.bishoybasily.stomp.lib.StompClient
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_account_info.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.MultipartBody
import java.lang.Exception

class InnerChatActivity : BaseKotlinActivity<ActivityInnerChatBinding, InnerChatViewModel>(),
    SocketServiceManager, BroadcastReceiverManager {

    private var chatMode: Int = -1
    private val TAG = "InnerChatActivity"
    override var toolbar: Toolbar? = null

    override val layoutResourceId: Int = R.layout.activity_inner_chat
    override val viewModel: InnerChatViewModel by viewModel()
    private val pref: MyPreference by inject()
    private val recyclerAdapter: InnerChatRecyclerAdapter by inject()
    private val innerDrawerAdapter: InnerDrawerAdapter by inject()

    private var imm: InputMethodManager? = null
    private var loadingDialog: Dialog? = null
    private var uploadingDialog: Dialog? = null
    private var isOkToSend = false
    private var isEndOfHistory: Boolean = true

    private lateinit var myID: String
    private var roomID: String? = null
    private var chatRoomMembers: ArrayList<String> = arrayListOf()
    private var readMembers: ArrayList<String> = arrayListOf()
    private var chatHistoryList: ArrayList<ChatHistoryResponse> = arrayListOf()
    private var memberInfoList: ArrayList<UserDataResponse> = arrayListOf()
    private var chatImages: ArrayList<String?> = arrayListOf()


    override var isBind: Boolean = false
    override var socket: Socket? = null
    override var stompClient: StompClient? = null
    override val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.e(TAG, "onServiceDisconnected")
            socket = null
            stompClient = null
            isBind = false
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.e(TAG, "onServiceConnected")
            val binder = service as SocketService.MyBinder
            socket = binder.getService().getSocket()
            stompClient = binder.getService().getStompClient()
            isBind = true
        }
    }
    override val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            when (p1?.action) {
                BroadCastIntentID.SEND_ON_CONNECT -> {
                    if (isBind) {
                        onStompSubscribe()
                        onConnect()
                    }
                }
                BroadCastIntentID.SEND_ON_DISCONNECT -> {
                    onDisconnect()
                    clearDisposable()
                }
                BroadCastIntentID.SEND_ON_ERROR -> {
                    clearDisposable()
                }
                else -> {
                }
            }
        }
    }

    override fun initStartView() {

        viewDataBinding.lifecycleOwner = this

        toolbar = findViewById(R.id.chatToolbar)
        intent.getStringExtra(IntentID.CHAT_ROOM_TITLE)?.let {
            setToolbarTitle(it)
        }
        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?

        chat_recycler_view.apply {
            layoutManager = LinearLayoutManager(this@InnerChatActivity).apply {
                stackFromEnd = true
            }
            adapter = recyclerAdapter
            setHasFixedSize(true)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    isEndOfHistory =
                        !recyclerView.canScrollVertically(1)
                                || recyclerView.verticalScrollbarPosition == chat_recycler_view.adapter!!.itemCount - 1

                    if (!isEndOfHistory) {
                        scroll_bottom_btn.show()
                    } else {
                        scroll_bottom_btn.gone()
                    }
                }
            })
        }
    }

    override fun initDataBinding() {

        /**
         * 서버에서 roomID, fixedPeopleList, 툴바제목을 받아온다
         * */
        // makeChatRoom & getChatRoomData
        viewModel.chatRoomDataResponseLiveData.observe(this, { res ->
            // charRoomId
            res.chatRoomId?.let {
                Log.e(TAG, "채팅방 id : $it")
                roomID = it

                //charRoomId를 받아야 socket 을 열수있다
                onStompSubscribe()

                // notification 을 cancel 시킨다
                // notification id 가 chatRoomId 이므로 chatRoomId 받은 후에 실행
                cancelNotification()
            }

            // 툴바제목
            res.chatRoomName?.let {
                Log.e(TAG, "채팅방 이름 : $it")
                setToolbarTitle(it)
            }

            // chatRoomMembers
            res.chatRoomMembers?.let {
                it.run {
                    chatRoomMembers = this@run
                    recyclerAdapter.setFixedPeopleList(chatRoomMembers)
                }

                Log.e(TAG, "채팅방 사람들 : ${chatRoomMembers.showElements()}")
            }

            // drawer 속 list view
            res.chatRoomMembersInfo?.let {
                memberInfoList = it
                innerDrawerAdapter.addItemList(memberInfoList)
                navi_list_view.adapter = innerDrawerAdapter
            }

            // 채팅방 입장
            enterChatRoom()

            // 채팅 기록을 불러온다
            viewModel.getChatHistory(roomID)

            // 로딩 다이얼로그 시작
            loadingDialog = Dialog(this@InnerChatActivity).apply {
                setContentView(R.layout.custom_loading_dialog)
                setCancelable(true)
                show()
            }

            // 채팅버튼 활성화
            enableSendButton()
        })

        // getChatHistory
        viewModel.chatHistoryResponseLiveData.observe(this, Observer {
            Log.e(TAG, "getChatHistory observing $roomID 번방")

            //로딩 다이얼로그 dismiss
            loadingDialog!!.dismiss()

            it?.let { chatList ->

                chatHistoryList = arrayListOf()

                //채팅기록을 뿌려준다
                chatList.forEach { chat -> chatHistoryList.add(chat) }
                recyclerAdapter.addChatItem(chatHistoryList)

                //맨 밑으로 스크롤
                scrollToBottom()
            }
        })

        viewModel.chatUpdateReadMembersLiveData.observe(this, {
            Log.e(TAG, "updateReadMembers observing $roomID 번방")

            /** 다른 사람들이 채팅방에 입장할 때 읽음 표시갱신을 위해 chatHistoryList 를 새로 초기화한다
             * 안하면 같은 내용의 채팅이 여러번 반복되어 나타난다*/
            chatHistoryList = arrayListOf()

            //채팅기록을 뿌려준다
            it?.forEach { chat -> chatHistoryList.add(chat) }
            recyclerAdapter.addChatItem(chatHistoryList)

        })

        viewModel.chatUploadImagesLiveData.observe(this, { imageList ->
            Log.e(TAG, "chatUploadImagesLiveData: $roomID")

            uploadingDialog!!.dismiss()

            roomID?.let {
                sendMessage(
                    ChatMessageSocketData(
                        it, myID, null,
                        chatRoomMembers, readMembers, imageList, MessageType.IMAGE
                    )
                )
            }
        })
    }

    override fun initAfterBinding() {
        //메시지 보내기
        chat_send.setOnClickListener {
            roomID?.let {
                sendMessage(
                    ChatMessageSocketData(
                        it, myID, chat_edit_text.text.toString(),
                        chatRoomMembers, readMembers, null, MessageType.TEXT
                    )
                )
            }
        }
        // 키보드에서 완료버튼 누를때에도 메시지 보내기
        chat_edit_text.onDone {
            roomID?.let {
                sendMessage(
                    ChatMessageSocketData(
                        it, myID, chat_edit_text.text.toString(),
                        chatRoomMembers, readMembers, null, MessageType.TEXT
                    )
                )
            }

        }

        chat_add_btn.setOnClickListener {
            chatImages = arrayListOf()
            selectImage(IntentID.CHAT_IMAGE)
        }

        scroll_bottom_btn.setOnClickListener {
            scroll_bottom_btn.gone()
            scrollToBottom()
            isEndOfHistory = true
        }

        registerReceiver()
        bindService(this)

        // roomID와 fixedPeopleList 를 설정한다
        chatRoomMembers = whereChatFrom()

        // 채팅방을 만든다
        if (roomID == null) {
            // 프로필에서 채팅하기 버튼을 클릭한 경우
            viewModel.makeChatRoom(ChatRoomSaveRequest(myID, chatRoomMembers))
        } else {
            // 채팅방 목록에서 클릭한 경우
            viewModel.getChatRoomData(ChatRoomDataRequest(myID, roomID, chatRoomMembers))
        }

        // 채팅방 id를 얻기 전까지 채팅버튼 비활성화
        disableSendButton()
    }

    override fun onStart() {
        super.onStart()
        onStompSubscribe()
    }

    override fun onResume() {
        super.onResume()
        // 채팅방 입장
        enterChatRoom()
    }

    override fun onPause() {
        super.onPause()
        //채팅방 나감
        exitChatRoom()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver()
        unbindService(this)
    }


    // https://velog.io/@dnjscksdn98/Chatting-Server-Stomp%EB%A1%9C-%EC%B1%84%ED%8C%85-%EA%B8%B0%EB%8A%A5-%ED%99%95%EC%9E%A5
    private fun onStompSubscribe() {
        if (stompClient == null) Log.e(TAG, "onStompClient is null")
        stompClient?.apply {

            // add하기 전에 clear한다
            clearDisposable()

            // 채팅방 입장,퇴장할 때
            addDisposable(this.join("/sub/chat/room/$roomID")
                .doOnError { error -> Log.e(TAG, "onStompSubscribe error: $error") }
                .subscribe { message ->
                    readMembers = ArrayList(message.toCleanString().toArrayList())
                    Log.e(TAG, "[$roomID 번방] 현재사람들 : $readMembers")
                    Thread.sleep(30)
                    /** 다른 유저가 들어오면 채팅방에있는 모든 사람들의 읽음 표시를 갱신한다*/
                    viewModel.updateReadMembers(roomID)
                })


            // 채팅 메시지 보낼 때
            addDisposable(this.join("/sub/chat/message/$roomID")
                .doOnError { error -> Log.e(TAG, "onStompSubscribe error: $error") }
                .subscribe { message ->
                    Log.e(TAG, "/sub/chat/message/ : $message")

                    runOnUiThread {
                        val chatHistory = Json {
                            isLenient = true
                        }.decodeFromString<ChatHistoryResponse>(message)

                        chatHistoryList.add(chatHistory)
                        recyclerAdapter.addChatItem(chatHistoryList)

                        if (isEndOfHistory) {
                            scrollToBottom()
                        } else {
                            if (chatHistory.userInfo.id != myID)
                                showToast("${chatHistory.userInfo.name} : ${chatHistory.history}")
                        }
                    }
                })
        }
    }

    private fun onConnect() {
        enterChatRoom()
        enableSendButton()
    }

    private fun onDisconnect() {
        disableSendButton()
        exitChatRoom()
    }

    private fun enterChatRoom() {
        roomID?.let {

            Log.e(TAG, "enter $it 번방")
            stompClient?.let { stomp ->
                addDisposable(stomp.send(
                    "/pub/chat/enter",
                    ChatRoomSocketData(it, "${pref.userIdx}").toJSONString()!!
                )
                    .doOnError { error -> Log.e(TAG, "enter error : $error") }
                    .subscribe { })
            }

        }
    }

    private fun exitChatRoom() {
        roomID?.let {

            Log.e(TAG, "exit $it 번방")
            stompClient?.let { stomp ->
                addDisposable(stomp.send(
                    "/pub/chat/exit",
                    ChatRoomSocketData(it, "${pref.userIdx}").toJSONString()!!
                )
                    .doOnError { error -> Log.e(TAG, "exit error : $error") }
                    .subscribe { })
            }

        }
    }

    private fun sendMessage(chatMessageSocketData: ChatMessageSocketData) {
        if (!isOkToSend) {
            showToast("잠시만 기다려주세요")
            return
        }

        if (chat_edit_text.text.toString().isNotBlank()) {
            chatMessageSocketData.toJSONString()?.let {
                addDisposable(stompClient!!.send("/pub/chat/message", it)
                    .doOnError { error -> Log.e(TAG, "message error : $error") }
                    .subscribe { })
            }
            chat_edit_text.text.clear()
            scrollToBottom()
        }

        if (chatMessageSocketData.chatImages != null) {
            chatMessageSocketData.toJSONString()?.let {
                addDisposable(stompClient!!.send("/pub/chat/message", it)
                    .doOnError { error -> Log.e(TAG, "message error : $error") }
                    .subscribe { })
            }
            scrollToBottom()
        }
    }

    private fun whereChatFrom(): ArrayList<String> {
        myID = pref.userIdx!!
        chatMode = intent.getIntExtra(IntentID.CHAT_MODE, -1)

        // 중복 add되는걸 막기위함
        var fixedList: ArrayList<String> = arrayListOf()

        when (chatMode) {
            IntentID.CHAT_WITH_ME -> {
                // 나와의 채팅

                myID.let {
                    Log.e(TAG, "CHAT_WITH_ME")
                    fixedList.add(it)
                }
            }

            IntentID.CHAT_WITH_ONE_PARTNER -> {
                // AccountInfo에서 채팅을 클릭한 것
                // 1 : 1 채팅

                intent.getStringExtra(IntentID.ID)?.let {
                    Log.e(TAG, "CHAT_WITH_ONE_PARTNER")
                    fixedList.add(myID)
                    fixedList.add(it)
                }

            }

            IntentID.CHAT_WITH_PEOPLE_FROM_CHAT_FRAG -> {
                // chatfragment에서 들어옴
                // 채팅방에서 나가기 할경우도 또는 새로 초대할경우

                intent.getStringExtra(IntentID.CHAT_ROOM_ID)?.let {
                    Log.e(TAG, "CHAT_WITH_PEOPLE_FROM_CHAT_FRAG")
                    roomID = it
                }

                intent.getStringExtra(IntentID.CHAT_ROOM_PEOPLE_LIST)?.let {
                    it.toCleanString().toArrayList().run {
                        fixedList = ArrayList(this)
                    }
                }

            }

            IntentID.CHAT_WITH_PEOPLE_FROM_INVITING -> {
                // 채팅방 막 만듦
                // 여러 유저의 id가 넘어온다
                // 채팅방에서 나가기 할경우도 또는 새로 초대할경우

                intent.getStringArrayListExtra(IntentID.PEOPLE_IDS)?.let {
                    Log.e(TAG, "CHAT_WITH_MANY_FIRST")
                    fixedList = it
                    fixedList.add(myID)
                }
            }

            IntentID.CHAT_FROM_NOTIFICATION -> {
                // notification으로 들어올때

                intent.getStringExtra(IntentID.CHAT_ROOM_ID)?.let {
                    Log.e(TAG, "CHAT_FROM_NOTIFICATION 채팅방 id = $it")
                    roomID = it
                }

                intent.getStringExtra(IntentID.CHAT_ROOM_PEOPLE_LIST)?.let {
                    it.toCleanString().toArrayList().run {
                        fixedList = this
                    }
                }
            }

            -1 -> {
                Log.e(TAG, "CHAT_MODE ERROR")
                throw Exception()
            }
        }

        // 오름차순 배열
        fixedList.sortBy { it }

        Log.e(TAG, "[$roomID 번방 사람들] ${fixedList.showElements()}")

        return fixedList
    }

    private fun registerReceiver() {
        IntentFilter(BroadCastIntentID.SEND_ON_CONNECT).let {
            it.addAction(BroadCastIntentID.SEND_ON_DISCONNECT)
            it.addAction(BroadCastIntentID.SEND_ON_ERROR)
            registerBroadcastReceiver(this, it)
        }
    }

    private fun unregisterReceiver() {
        unregisterBroadcastReceiver(this)
    }

    private fun scrollToBottom() {
        chat_recycler_view.postDelayed({
            chat_recycler_view.scrollToPosition(chat_recycler_view.adapter!!.itemCount - 1)
        }, 200)
    }

    private fun cancelNotification() {
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).run {
            roomID?.let { cancel(it.toInt()) }
        }
    }

    private fun disableSendButton() {
        isOkToSend = false
    }

    private fun enableSendButton() {
        isOkToSend = true
    }

    private fun hideKeyboard() {
        imm?.hideSoftInputFromWindow(chat_edit_text.windowToken, 0)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.inner_chat_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_info -> {
                hideKeyboard()
                drawer_layout.openDrawer(GravityCompat.END)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        // drawer 가 열렸을경우 닫고 닫혔을 경우 뒤로가기
        when (drawer_layout.isDrawerOpen(GravityCompat.END)) {
            true -> {
                drawer_layout.closeDrawer(GravityCompat.END)
            }
            false -> {
                exitChatRoom()
                super.onBackPressed()
            }
        }
    }

    private fun selectImage(to: Int) {
        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                Log.e("cameraPermission", "Permission Granted")

                // 카메라 , 앨범 퍼미션 허가시
                Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    type = "image/*"
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    startActivityForResult(Intent.createChooser(this, "Select Picture"), to)
                }
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Log.e("cameraPermission", "Permission Denied : ${deniedPermissions.toString()}")
            }
        }

        TedPermission.with(applicationContext)
            .setPermissionListener(permissionListener)
            .setDeniedMessage(getString(R.string.permission_denied))
            .setPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
            .check()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK)
            return

        when (requestCode) {
            IntentID.CHAT_IMAGE -> {

                data!!.clipData?.let { clipData ->
                    for (i in 0 until clipData.itemCount) {
                        try {
                            val imageUri = clipData.getItemAt(i).uri
                            chatImages.add(getPath(imageUri))
                        } catch (e: Exception) {
                            showToast("앨범에서 사진 가져오기 에러")
                            Log.e(TAG, "앨범에서 사진 가져오기 에러 : ${e.message}")
                        }
                    }
                }

                data.data?.let { selectedImageUri ->
                    try {
                        chatImages.add(getPath(selectedImageUri))
                        Log.e(TAG, "chatImage : $chatImages")

                    } catch (e: Exception) {
                        showToast("앨범에서 사진 가져오기 에러")
                        Log.e(TAG, "앨범에서 사진 가져오기 에러 : ${e.message}")
                    }
                }

                if (chatImages.size > 0) {
                    val multipartList = arrayListOf<MultipartBody.Part?>()

                    chatImages.forEachIndexed { index, imageUri ->
                        multipartList.add(prepareFilePart("images", imageUri))
                    }

                    // 이미지 보내기
                    viewModel.uploadChatImages(
                        ChatUploadImageRequest(
                            createPartFromString(myID),
                            createPartFromString(roomID),
                            multipartList
                        )
                    )

                    uploadingDialog = Dialog(this@InnerChatActivity).apply {
                        setContentView(R.layout.custom_uploading_dialog)
                        setCancelable(true)
                        show()
                    }
                }
            }
        }
    }

    /**
    private fun onSocket() {
    if (socket == null) Log.e(TAG, "socket is null")
    socket?.apply {
    Log.e(TAG, "onSocket $roomID 번방")

    on(SocketReceive.CHATROOM_ENTER + roomID, onChatRoomEnter)
    on(SocketReceive.CHATROOM_EXIT + roomID, onChatRoomExit)
    on(SocketReceive.CHAT_MESSAGE, onChatMessageReceive)

    // 채팅방에서 네트워크 오류 발생시 나갔다가 들어오도록
    on(Socket.EVENT_CONNECT, onConnect)
    on(Socket.EVENT_DISCONNECT, onDisconnect)
    }
    }

    private fun offSocket() {
    if (socket == null) Log.e(TAG, "socket is null")
    socket?.apply {
    Log.e(TAG, "offSocket $roomID 번방")

    off(SocketReceive.CHATROOM_ENTER + roomID, onChatRoomEnter)
    off(SocketReceive.CHATROOM_EXIT + roomID, onChatRoomExit)
    off(SocketReceive.CHAT_MESSAGE, onChatMessageReceive)

    // 채팅방에서 네트워크 오류 발생시 나갔다가 들어오도록
    off(Socket.EVENT_CONNECT, onConnect)
    off(Socket.EVENT_DISCONNECT, onDisconnect)
    }
    }

    private val onChatRoomEnter = Emitter.Listener {
    runOnUiThread {
    (it[0] as String).apply {
    readMembers = arrayListOf()

    this.split(",").toTypedArray().run {
    this.toCollection(ArrayList()).forEach {
    if (it.toCleanString() != "")
    readMembers.add(it.toCleanString())
    }
    }

    Log.e(TAG, "[enter $roomID 번방] 현재사람들 ${readMembers.showElements()}")
    }

    /** 다른 유저가 들어오면 채팅방에있는 모든 사람들의 읽음 표시를 갱신한다*/
    viewModel.getChatHistory(roomID)
    }
    }

    private val onChatRoomExit = Emitter.Listener {
    runOnUiThread {
    (it[0] as String).apply {
    readMembers = arrayListOf()

    this.split(",").toTypedArray().run {
    this.toCollection(ArrayList()).forEach {
    if (it.toCleanString() != "")
    readMembers.add(it.toCleanString())
    }
    }

    Log.e(TAG, "[exit $roomID 번방] 현재사람들 ${readMembers.showElements()}")
    }
    }
    }

    private val onConnect = Emitter.Listener {
    runOnUiThread {
    enterChatRoom()
    enableSendButton()
    }
    }

    private val onDisconnect = Emitter.Listener {
    runOnUiThread {
    disableSendButton()
    exitChatRoom()
    }
    }

    private val onChatMessageReceive = Emitter.Listener {
    runOnUiThread {
    (it[0] as JSONObject).apply {
    Log.e(TAG, "[chat] received message : ${this["content"]}")

    val message = this.toObject(ChatHistoryResponse::class.java)
    chatHistoryList.add(message)
    recyclerAdapter.addChatItem(chatHistoryList)

    scrollToBottom()
    }
    }
    }

     **/

}
