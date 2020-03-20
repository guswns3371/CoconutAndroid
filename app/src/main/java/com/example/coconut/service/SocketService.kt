package com.example.coconut.service

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
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject
import org.koin.android.ext.android.inject
import java.lang.RuntimeException

class SocketService : Service() {

    private val TAG = "SocketService"
    lateinit var handler : Handler

    lateinit var socket : Socket
    lateinit var context : Context
    private var sender : String? = null
    private val pref : MyPreference by inject()
    private var isConnected : Boolean = false

    //  https://bitsoul.tistory.com/149
    /**
     * 서비스는 클라이언트-서버 와 같이 동작 ( 서비스 = 서버, 서비스를 사용하는 context = 클라이언트)
     * 하나의 서비스 : 다수의 액티비티 연결 가능
     * onBind() 는 IBinder를 반환 : 서비스와 클라이언트사이의 인터페이스 역할하는 IBinder
     * IBinder로 액티비티(프래그먼트)에서 서비스속 변수를 사용할수있게된다
     * 클라이언트-서비스 연결을 끊기 위해서 unbindService()를 호출한다다     * */
    inner class MyBinder : Binder() {
        fun getService() =  this@SocketService
    }

    override fun onCreate() {
        super.onCreate()
        Log.e(TAG,"onCreate")
        handler = Handler()
        context = applicationContext
        sender = pref.userID

        try {
            socket = IO.socket(Constant.NODE_URL)
        }catch (e : Exception){
            throw RuntimeException(e)
        }

        socketConnect()
    }

    fun mySocket() : Socket {
        return socket
    }

    private fun runOnUiThread(runnable: Runnable){
        handler.post(runnable)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG,"onStartCommand")
        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        Log.e(TAG,"onDestroy")
        offline()
        socketDisconnect()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.e(TAG,"onBind")
        return MyBinder()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.e(TAG,"onUnbind")
        return super.onUnbind(intent)
    }


    private fun socketConnect(){
        Log.e(TAG,"socketConnect")
        socket.connect()
        socket.on(Socket.EVENT_CONNECT,onConnect)
        socket.on(Socket.EVENT_DISCONNECT,onDisconnect)
        socket.on(Socket.EVENT_CONNECT_ERROR,onConnectionError)
        socket.on(Socket.EVENT_CONNECT_TIMEOUT,onConnectionError)
    }

    private fun socketDisconnect(){
        Log.e(TAG,"socketDisconnect")
        socket.off(Socket.EVENT_CONNECT,onConnect)
        socket.off(Socket.EVENT_DISCONNECT,onDisconnect)
        socket.off(Socket.EVENT_CONNECT_ERROR,onConnectionError)
        socket.off(Socket.EVENT_CONNECT_TIMEOUT,onConnectionError)
    }

    private fun online(){
        Log.e(TAG,"online")
        try {
            JSONObject().apply {
                put(SocketData.USER_ID,pref.userID)
                socket.emit(SocketSend.ONLINE_USER,this)
            }
        }catch (e: JSONException){
            Log.e(TAG,"${e.message}")
        }
    }

    private fun offline(){
        Log.e(TAG,"offline")
        try {
            JSONObject().apply {
                put(SocketData.USER_ID,pref.userID)
                socket.emit(SocketSend.OFFLINE_USER,this)
            }
        }catch (e: JSONException){
            Log.e(TAG,"${e.message}")
        }
    }

    private val onConnect = Emitter.Listener {
        runOnUiThread (
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
                isConnected=false
                Log.e(TAG,"Disconnected")
                offline()
                showToast("서버와의 연결이 끊어졌습니다")
            }
        )
    }

    private val onConnectionError = Emitter.Listener {
        runOnUiThread(
            Runnable {
                isConnected=false
                Log.e(TAG,"onConnectionError")
                //showToast("서버와의 연결이 불안정합니다")
            }
        )
    }
}
