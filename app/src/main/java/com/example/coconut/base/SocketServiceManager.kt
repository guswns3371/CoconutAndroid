package com.example.coconut.base

import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import com.example.coconut.service.SocketService
import io.socket.client.Socket

interface SocketServiceManager {

     var isBind : Boolean
     var socket : Socket?

     val serviceConnection: ServiceConnection

     // 뷰가 시작할때
     fun bindService(context: Context?){
        context?.bindService(Intent(context,SocketService::class.java),serviceConnection,
            AppCompatActivity.BIND_AUTO_CREATE)
     }

    //뷰가 끝날때
     fun unbindService(context: Context?){
        context?.unbindService(serviceConnection)
     }
}