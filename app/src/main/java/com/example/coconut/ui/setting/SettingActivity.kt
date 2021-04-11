package com.example.coconut.ui.setting

import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import com.example.coconut.*
import com.example.coconut.base.BaseKotlinActivity
import com.example.coconut.base.SocketServiceManager
import com.example.coconut.databinding.ActivitySettingBinding
import com.example.coconut.receiver.SocketBroadcastReceiver
import com.example.coconut.service.SocketService
import com.example.coconut.ui.auth.login.LoginActivity
import com.example.coconut.ui.auth.login.LoginViewModel
import com.example.coconut.util.MyPreference
import io.socket.client.Socket
import org.json.JSONException
import org.json.JSONObject
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.gmail.bishoybasily.stomp.lib.StompClient

class SettingActivity : BaseKotlinActivity<ActivitySettingBinding,SettingViewModel>(), SocketServiceManager {

    private val pref : MyPreference by inject()
    private val loginViewModel : LoginViewModel by viewModel()
    private val TAG = "SettingActivity"
    lateinit var mReceiver: SocketBroadcastReceiver

    override val layoutResourceId: Int
        get() = R.layout.activity_setting

    override val viewModel: SettingViewModel by viewModel()

    override var toolbar: Toolbar? = null

    override var isBind: Boolean = false
    override var socket: Socket? = null
    lateinit var socketService: SocketService
    override var stompClient: StompClient? = null
    override val serviceConnection: ServiceConnection = object :ServiceConnection{
        override fun onServiceDisconnected(name: ComponentName?) {
            // onServiceDisconnected 는 서비스가 비정상종료 될 때에만 호출된다.
            Log.e(TAG, "onServiceDisconnected")
            socket = null
            isBind = false
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.e(TAG,"onServiceConnected")
            val binder = service as SocketService.MyBinder
            socket = binder.getService().getSocket()
            isBind = true
            socketService = binder.getService()

        }

    }



    override fun initStartView() {
        bindService(this)
        //viewModel 에서 button click을 하기 위함
        viewDataBinding.viewModel = viewModel // view와 viewmodel을 연결한다
        viewDataBinding.lifecycleOwner = this
        viewModel.onCreate()
    }

    override fun initDataBinding() {
        viewModel.logoutObservable.observe(this, Observer { event->
            event.getContentIfNotHandled()?.let {
                when (it) {
                    true -> {
                        Log.i(TAG, "initDataBinding: logout start")
                        // offline()
                        loginViewModel.deleteFcmTokenFromServer(pref.userIdx!!)
                        pref.resetAccessToken()
                        pref.resetRefreshToken()
                        socketService.logout()
                        callActivity(Constant.LOGIN_PAGE)
                    }
                    false -> {

                    }
                }
            }
        })
    }

    override fun initAfterBinding() {

    }


    override fun onDestroy() {
        Log.e(TAG, "onDestroy")
        super.onDestroy()
        unbindService(this)
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

    private fun offline(){
        Log.e(TAG,"offline")
        try {
            JSONObject().apply {
                put(SocketData.USER_ID,pref.userIdx)
                socket?.emit(SocketSend.OFFLINE_USER,this)
            }
        }catch (e: JSONException){
            Log.e(TAG,"${e.message}")
        }
    }

//    private fun registerReceiver() {
//        mReceiver = SocketBroadcastReceiver()
//        registerReceiver(mReceiver, IntentFilter(BroadCastIntentID.SEND_LOGOUT))
//    }
//
//    private fun unregisterReceiver() {
//        unregisterReceiver(mReceiver)
//    }
//
//    private fun sendLogoutBroadcast() {
//        val intent = Intent()
//        intent.action = BroadCastIntentID.SEND_LOGOUT
//        sendBroadcast(intent)
//    }

}
