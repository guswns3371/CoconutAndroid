package com.example.coconut.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.example.coconut.BroadCastIntentID
import com.example.coconut.Constant
import com.example.coconut.IntentID
import com.example.coconut.model.socket.EchoSocketData
import com.example.coconut.ui.main.account.AccountFragment
import com.example.coconut.ui.main.chat.ChatFragment
import com.example.coconut.ui.main.chat.inner.InnerChatActivity
import com.example.coconut.ui.main.hashtag.HashTagFragment
import com.example.coconut.util.MyPreference
import com.example.coconut.util.showToast
import com.example.coconut.util.toArrayList
import com.example.coconut.util.toCleanString
import com.gmail.bishoybasily.stomp.lib.Event
import com.gmail.bishoybasily.stomp.lib.StompClient
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit
import ua.naiksoftware.stomp.dto.LifecycleEvent.Type.*
import kotlin.properties.Delegates


class SocketService : Service() {

    private val TAG = "SocketService"
    private val intervalMillis = 300L
    private var sender: String? = null
    private val pref: MyPreference by inject()

    private lateinit var handler: Handler
    lateinit var context: Context

    //    lateinit var socket: Socket
    private var socket: Socket? = null
    private lateinit var stompClient: StompClient
    private var compositeDisposable: CompositeDisposable? = null
    private var serviceDisposable: Disposable? = null

    private var onlineUserList: ArrayList<String>? by Delegates.observable(null) { property, oldValue, newValue ->
        Log.e(TAG, "onlineUserList observable old: $oldValue")
        Log.e(TAG, "onlineUserList observable new: $newValue")
    }

    private lateinit var accountFragReceiver: BroadcastReceiver
    private lateinit var innerChatActivityReceiver: BroadcastReceiver
    private lateinit var chatFragReceiver: BroadcastReceiver
    private lateinit var hashFragReceiver: BroadcastReceiver

    /**
     * https://bitsoul.tistory.com/149
     * 서비스는 클라이언트-서버 와 같이 동작 ( 서비스 = 서버, 서비스를 사용하는 context = 클라이언트)
     * 하나의 서비스 : 다수의 액티비티 연결 가능
     * onBind() 는 IBinder를 반환 : 서비스와 클라이언트사이의 인터페이스 역할하는 IBinder
     * IBinder로 액티비티(프래그먼트)에서 서비스속 변수를 사용할수있게된다
     * 클라이언트-서비스 연결을 끊기 위해서 unbindService()를 호출한다
     **/

    inner class MyBinder : Binder() {
        fun getService() = this@SocketService
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.e(TAG, "onBind")
        return MyBinder()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.e(TAG, "onUnbind")
        return super.onUnbind(intent)
    }

    override fun onCreate() {
        super.onCreate()
        Log.e(TAG, "onCreate")

        registerSocketReceiver()

        handler = Handler()
        context = applicationContext
        sender = pref.userIdx

        val client = OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build()
        try {
            // Spring Boot WebSocket 과 연결
            stompClient = StompClient(client, intervalMillis)
                .apply {
                    this@apply.url = Constant.STOMP_URL
                }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

        stompConnect()

    }

    /**
     * 설정에서 배터리 사용량 최적화하면 서비스가 제대로 작동하지 않는다
     * */

    override fun onDestroy() {
        super.onDestroy()
        offline()
        stompDisconnect()
        unregisterSocketReceiver()
        Log.e(TAG, "onDestroy")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand")
        return START_REDELIVER_INTENT
    }

    fun getSocket(): Socket? {
        return socket
    }

    fun getStompClient(): StompClient {
        return stompClient
    }

    private fun addDisposable(disposable: Disposable) {
        if (compositeDisposable == null) {
            compositeDisposable = CompositeDisposable()
        }
        compositeDisposable?.add(disposable)
    }

    private fun disposeDisposable() {
        compositeDisposable?.dispose()
        compositeDisposable = null

    }

    private fun clearDisposable() {
        compositeDisposable?.clear()
    }

    private fun runOnUiThread(runnable: Runnable) {
        handler.post(runnable)
    }


    private fun sendAccountFragBroadcast(userList: ArrayList<String>?) {
        Intent().let {
            it.action = BroadCastIntentID.SEND_USER_LIST
            it.putExtra(IntentID.RECEIVE_USER_LIST, userList)
            sendBroadcast(it)
        }
    }

    private fun sendOnConnectBroadcast() {
        Intent().let {
            it.action = BroadCastIntentID.SEND_ON_CONNECT
            sendBroadcast(it)
        }
    }

    private fun sendOnDisconnectBroadcast() {
        Intent().let {
            it.action = BroadCastIntentID.SEND_ON_DISCONNECT
            sendBroadcast(it)
        }
    }

    private fun registerSocketReceiver() {
        accountFragReceiver = AccountFragment().getBroadcastReceiver()
        innerChatActivityReceiver = InnerChatActivity().getBroadcastReceiver()
        chatFragReceiver = ChatFragment().getBroadcastReceiver()
        hashFragReceiver = HashTagFragment().getBroadcastReceiver()

        IntentFilter(BroadCastIntentID.SEND_USER_LIST).let {
            it.addAction(BroadCastIntentID.SEND_ON_CONNECT)
            it.addAction(BroadCastIntentID.SEND_ON_DISCONNECT)
            it.addAction(BroadCastIntentID.SEND_ON_ERROR)
            registerReceiver(accountFragReceiver, it)
        }

        IntentFilter(BroadCastIntentID.SEND_ON_CONNECT).let {
            it.addAction(BroadCastIntentID.SEND_ON_DISCONNECT)
            it.addAction(BroadCastIntentID.SEND_ON_ERROR)
            registerReceiver(innerChatActivityReceiver, it)
            registerReceiver(chatFragReceiver, it)
            registerReceiver(hashFragReceiver, it)
        }
    }

    private fun unregisterSocketReceiver() {
        unregisterReceiver(accountFragReceiver)
        unregisterReceiver(innerChatActivityReceiver)
        unregisterReceiver(chatFragReceiver)
        unregisterReceiver(hashFragReceiver)
    }

    private fun stompConnect() {
        Log.e(TAG, "stompConnect")

        addDisposable(stompClient.connect()
            .subscribe { event ->
                when (event.type) {
                    Event.Type.OPENED -> {
                        Log.e(TAG, "onConnect")
                        onlineUserList = arrayListOf()
                        serviceDisposable = joinToTopicDisposable()
                        online()
                        sendOnConnectBroadcast()
                        showToast("서버와 연결되었습니다")
                    }
                    Event.Type.CLOSED -> {
                        Log.e(TAG, "Disconnected")
                        showToast("서버와의 연결이 끊어졌습니다")
                        resetLine()
                        offline()
                        serviceDisposable?.dispose()
                        sendOnDisconnectBroadcast()
                    }
                    Event.Type.ERROR -> {
                        Log.e(TAG, "onConnectionError")
                        showToast("서버와의 연결이 불안정합니다")
                        resetLine()
                        offline()
                        serviceDisposable?.dispose()
                        sendOnDisconnectBroadcast()
                    }
                }
            })
    }

    private fun stompDisconnect() {
        Log.e(TAG, "stompDisconnect")
        disposeDisposable()
    }

    private fun joinToTopicDisposable(): Disposable {
        return stompClient.join("/sub/users/service")
            .doOnError { error -> Log.e(TAG, "onlineUserList error: $error") }
            .subscribe { userListJsonString ->
                onlineUserList = userListJsonString.toCleanString().toArrayList()
                Log.e(TAG, "onlineUserList: $onlineUserList")

                // 브로드캐스트
                sendAccountFragBroadcast(onlineUserList)
            }
    }

    private fun resetLine() {
        Log.e(TAG, "resetLine")
        onlineUserList = null
        sendAccountFragBroadcast(onlineUserList)
    }

    private fun online() {
        Log.e(TAG, "online")

        addDisposable(stompClient.send(
            "/pub/users/online",
            "${pref.userIdx}"
        )
            .doOnError { error -> Log.e(TAG, "online error : $error") }
            .subscribe { })
    }

    private fun offline() {
        Log.e(TAG, "offline")

        addDisposable(stompClient.send(
            "/pub/users/offline",
            "${pref.userIdx}"
        )
            .doOnError { error -> Log.e(TAG, "offline error : $error") }
            .subscribe { })
    }

    fun logout() {
        Log.e(TAG, "logout")
        addDisposable(stompClient.send(
            "/pub/users/offline",
            "${pref.userIdx}"
        )
            .doOnError { error -> Log.e(TAG, "offline error : $error") }
            .doAfterNext { pref.resetUserId() }
            .subscribe { Log.e(TAG, "logout complete") })

    }

    private fun sampleStomp() {
        // 채널에 구독한다. 방이름이 greetings
        // @SendTo("/topic/greetings")
        addDisposable(stompClient.join("/topic/greetings")
            .doOnError { error ->
                Log.e(TAG, "stompConnect error: $error")
            }
            .subscribe { message ->
                Log.e(TAG, "stompConnect Stomp 로 보낸 메시지 되돌아옴: $message")
                val echoSocketData = Json.decodeFromString<EchoSocketData>(message)
                if (echoSocketData.userIndex.toCleanString() != pref.userIdx)
                    Log.e(TAG, "stompConnect: ${echoSocketData.echo}")
            })

        // @MessageMapping("/hello-msg-mapping")
        addDisposable(stompClient.send(
            "/topic/hello-msg-mapping",
            "${pref.userIdx}"
        )
            .doOnError { error ->
                Log.e(TAG, "/topic/hello-msg-mapping error : $error")
            }
            .subscribe { })
    }


    /**
    private fun socketConnect() {
    Log.e(TAG, "socketConnect")
    socket?.run {
    connect()
    on(Socket.EVENT_CONNECT, onConnect)
    on(Socket.EVENT_DISCONNECT, onDisconnect)
    on(Socket.EVENT_CONNECT_ERROR, onConnectionError)
    on(Socket.EVENT_CONNECT_TIMEOUT, onConnectionTimeout)
    }

    }

    private fun socketDisconnect() {
    Log.e(TAG, "socketDisconnect")
    socket?.run {
    off(Socket.EVENT_CONNECT, onConnect)
    off(Socket.EVENT_DISCONNECT, onDisconnect)
    off(Socket.EVENT_CONNECT_ERROR, onConnectionError)
    off(Socket.EVENT_CONNECT_TIMEOUT, onConnectionTimeout)
    }

    }

    private val onConnect = Emitter.Listener {
    runOnUiThread(
    Runnable {
    if (!isConnected) {
    Log.e(TAG, "onConnect")
    showToast("서버와 연결되었습니다")
    isConnected = true
    online()
    }
    }
    )
    }

    private val onDisconnect = Emitter.Listener {
    runOnUiThread(
    Runnable {
    isConnected = false
    Log.e(TAG, "Disconnected")
    offline()
    showToast("서버와의 연결이 끊어졌습니다")
    }
    )
    }

    private val onConnectionError = Emitter.Listener {
    runOnUiThread(
    Runnable {
    isConnected = false
    Log.e(TAG, "onConnectionError")
    showToast("서버와의 연결이 불안정합니다")
    }
    )
    }

    private val onConnectionTimeout = Emitter.Listener {
    runOnUiThread(
    Runnable {
    isConnected = false
    Log.e(TAG, "onConnectionTimeout")
    }
    )
    }
     **/

}
