package com.example.coconut.ui.setting

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import com.example.coconut.*
import com.example.coconut.base.BaseKotlinActivity
import com.example.coconut.base.SocketServiceManager
import com.example.coconut.databinding.ActivitySettingBinding
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

class SettingActivity : BaseKotlinActivity<ActivitySettingBinding,SettingViewModel>(), SocketServiceManager {

    private val pref : MyPreference by inject()
    private val loginViewModel : LoginViewModel by viewModel()
    private val TAG = "SettingActivity"

    override val layoutResourceId: Int
        get() = R.layout.activity_setting
    override val viewModel: SettingViewModel by viewModel()

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

    override fun initStartView() {
        bindService(this)
        //viewModel 에서 button click을 하기 위함
        viewDataBinding.viewModel = viewModel // view와 viewmodel을 연결한다
        viewDataBinding.lifecycleOwner = this
        viewModel.onCreate()
    }

    override fun initDataBinding() {
        viewModel.logoutObservable.observe(this, Observer {
            it.getContentIfNotHandled()?.let { b ->
                when(b) {
                    true ->{
                        socket?.run {
                            offline()
                            loginViewModel.deleteFcmTokenFromServer(pref.userIdx!!)
                            pref.resetAccessToken()
                            pref.resetRefreshToken()
                            pref.resetUserId() /**자동로그인 해제를 위해 UserId를 삭제한다*/
                            callActivity(Constant.LOGIN_PAGE)
                        }
                    }
                    false ->{

                    }
                }
            }
        })
    }

    override fun initAfterBinding() {

    }

    override var toolbar: Toolbar? = null

    override fun onDestroy() {
        unbindService(this)
        super.onDestroy()
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


}
