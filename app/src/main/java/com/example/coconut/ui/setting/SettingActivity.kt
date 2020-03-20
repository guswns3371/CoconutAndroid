package com.example.coconut.ui.setting

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.example.coconut.*
import com.example.coconut.base.SocketServiceManager
import com.example.coconut.service.SocketService
import com.example.coconut.ui.auth.login.LoginActivity
import com.example.coconut.ui.auth.login.LoginViewModel
import com.example.coconut.util.MyPreference
import io.socket.client.Socket
import kotlinx.android.synthetic.main.activity_setting.*
import org.json.JSONException
import org.json.JSONObject
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingActivity : AppCompatActivity(), SocketServiceManager {

    private val pref : MyPreference by inject()
    private val loginViewModel : LoginViewModel by viewModel()
    private val TAG = "SettingActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        bindService(this)
        clickListeners()
    }

    override fun onDestroy() {
        unbindService(this)
        super.onDestroy()
    }
    private fun clickListeners(){
        logoutButton.setOnClickListener {
            /**자동로그인 해제를 위해 UserId를 삭제한다*/
            socket?.run {
                offline()
                loginViewModel.deleteFcmTokenFromServer(pref.userID!!)
                pref.resetUserId()
                callActivity(Constant.LOGIN_PAGE)
            }
        }
    }

    private fun callActivity(where : Int){
        when(where){
            Constant.LOGIN_PAGE->{
                val intent = Intent(applicationContext,LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or //이걸 해줘야 fragment도 같이 pop
                        Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

        }
    }

    override var isBind: Boolean = false
    override var socket: Socket? = null
    override val serviceConnection: ServiceConnection = object :ServiceConnection{
        override fun onServiceDisconnected(name: ComponentName?) {
            socket = null
            isBind = false
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.e("serviceConn","onServiceConnected")
            val binder = service as SocketService.MyBinder
            socket = binder.getService().mySocket()
            isBind = true
        }

    }

    private fun offline(){
        Log.e(TAG,"offline")
        try {
            JSONObject().apply {
                put(SocketData.USER_ID,pref.userID)
                socket?.emit(SocketSend.OFFLINE_USER,this)
            }
        }catch (e: JSONException){
            Log.e(TAG,"${e.message}")
        }
    }
}
