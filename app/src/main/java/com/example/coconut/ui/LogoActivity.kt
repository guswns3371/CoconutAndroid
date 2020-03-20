package com.example.coconut.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.example.coconut.R
import com.example.coconut.ui.auth.login.LoginActivity
import com.example.coconut.ui.auth.login.LoginViewModel
import com.example.coconut.util.MyPreference
import com.example.coconut.util.log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class LogoActivity : AppCompatActivity() {

    companion object{
        private const val TAG = "LogoActivity"
        private const val SPLASH_TIME_OUT : Long = 800
    }

    private val pref : MyPreference by inject()
    private val viewModel : LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logo)

        fcmToken()

        Handler().postDelayed({
            Log.e(TAG,"userID : ${pref.userID}")
            Log.e(TAG,"fcmToken : ${pref.fcmToken}")

            when(pref.userID == null){
                true->{ startActivity(Intent(applicationContext, LoginActivity::class.java)) }
                false->{
                    pref.fcmToken?.let {
                        viewModel.sendFcmTokenToServer(pref.userID!!,it)
                    }
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                }
            }

            finish()

        },SPLASH_TIME_OUT)
    }

    private fun fcmToken(){
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token

                // Log and toast
                val msg = getString(R.string.msg_token_fmt, token)
                Log.e(TAG, msg)

                // MyPreference에 저장
                token?.let {
                    pref.fcmToken = it
                }

            })
    }

}
