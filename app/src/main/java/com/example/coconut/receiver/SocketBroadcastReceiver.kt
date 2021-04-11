package com.example.coconut.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.coconut.BroadCastIntentID
import com.example.coconut.util.MyPreference
import org.koin.android.ext.android.inject
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class SocketBroadcastReceiver : BroadcastReceiver(), KoinComponent {

    private val pref : MyPreference by inject() // 브로드캐스트 리시버에서 사용하기 위해서 KoinComponent를 implement해야한다.

    private val TAG = "SocketBroadcastReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action) {
            BroadCastIntentID.SEND_LOGOUT -> {
                Log.e(TAG, "onReceive: SEND_LOGOUT")
                pref.resetUserId()
            }
            else ->{

            }
        }
    }
}