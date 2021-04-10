package com.example.coconut.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.example.coconut.Constant
import com.example.coconut.SocketData
import com.example.coconut.SocketSend
import com.example.coconut.util.MyPreference
import com.example.coconut.util.showToast
import com.example.coconut.util.toast
import com.gmail.bishoybasily.stomp.lib.Event
import com.gmail.bishoybasily.stomp.lib.StompClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.socket.client.Socket
import io.socket.emitter.Emitter
import okhttp3.OkHttpClient
import org.json.JSONException
import org.json.JSONObject
import org.koin.android.ext.android.inject
import ua.naiksoftware.stomp.Stomp
//import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.StompHeader
import java.util.concurrent.TimeUnit
import ua.naiksoftware.stomp.dto.LifecycleEvent.Type.*


class SocketService : Service() {

    private val TAG = "SocketService"
    private val intervalMillis = 4000L
    private var sender: String? = null
    private val pref: MyPreference by inject()
    private var isConnected: Boolean = false

    lateinit var handler: Handler
    lateinit var context: Context

    //    lateinit var socket: Socket
    private var socket: Socket? = null
    lateinit var stompClient: StompClient
    lateinit var stompConnection: Disposable
    lateinit var topic: Disposable
    private var compositeDisposable: CompositeDisposable? = null

    /**
     * https://bitsoul.tistory.com/149
     * 서비스는 클라이언트-서버 와 같이 동작 ( 서비스 = 서버, 서비스를 사용하는 context = 클라이언트)
     * 하나의 서비스 : 다수의 액티비티 연결 가능
     * onBind() 는 IBinder를 반환 : 서비스와 클라이언트사이의 인터페이스 역할하는 IBinder
     * IBinder로 액티비티(프래그먼트)에서 서비스속 변수를 사용할수있게된다
     * 클라이언트-서비스 연결을 끊기 위해서 unbindService()를 호출한다다
     **/

    inner class MyBinder : Binder() {
        fun getService() = this@SocketService
    }

    override fun onCreate() {
        super.onCreate()
        Log.e(TAG, "onCreate")
        handler = Handler()
        context = applicationContext
        sender = pref.userIdx

        val client = OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build()
        try {
//            socket = IO.socket(Constant.SOCKET_SERVER) // Spring Boot WebSocket으로 연결
            stompClient = StompClient(client, intervalMillis)
                .apply { this@apply.url = Constant.STOMP_URL }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

//        socketConnect()
        stompConnect()
    }

    fun getSocket(): Socket? {
        return socket
    }

    @JvmName("getStompClient1")
    fun getStompClient(): StompClient {
        return stompClient
    }

    private fun runOnUiThread(runnable: Runnable) {
        handler.post(runnable)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand")
        return START_REDELIVER_INTENT
    }

    /**
     * 설정에서 배터리 사용량 최적화하면 서비스가 제대로 작동하지 않는다
     * */
    override fun onDestroy() {
        offline()
        socketDisconnect()
        super.onDestroy()
        Log.e(TAG, "onDestroy")
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.e(TAG, "onBind")
        return MyBinder()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.e(TAG, "onUnbind")
        return super.onUnbind(intent)
    }


    @SuppressLint("CheckResult")
    private fun stompConnect() {
        stompConnection = stompClient.connect()
            .subscribe { event ->
                when (event.type) {
                    Event.Type.OPENED -> {
                        toast("socket opened")
                        topic = stompClient.join("/topic/greetings")
                            .doOnError { error ->
                                Log.e(TAG, "stompConnect: $error")
                            }
                            .subscribe { message ->
                                Log.e(TAG, "stompConnect: $message")
                            }

                        stompClient.send(
                            "/topic/hello-msg-mapping",
                            "My first STOMP message!"
                        ).subscribe { it ->
                            if (it) {
                                toast("메시지 보내기 성공")
                            }
                        }
                    }
                    Event.Type.CLOSED -> {
                        toast("socket closed")
                    }
                    Event.Type.ERROR -> {
                        toast("socket error")
                    }
                }
            }

    }

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

    private fun online() {
        Log.e(TAG, "online")
        try {
            JSONObject().apply {
                put(SocketData.USER_ID, pref.userIdx)
                socket?.emit(SocketSend.ONLINE_USER, this)
            }
        } catch (e: JSONException) {
            Log.e(TAG, "${e.message}")
        }
    }

    private fun offline() {
        Log.e(TAG, "offline")
        try {
            JSONObject().apply {
                put(SocketData.USER_ID, pref.userIdx)
                socket?.emit(SocketSend.OFFLINE_USER, this)
            }
        } catch (e: JSONException) {
            Log.e(TAG, "${e.message}")
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
}
