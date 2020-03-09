package com.example.coconut

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.coconut.di.moduleList
import com.example.coconut.service.SocketService
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import org.koin.android.ext.android.startKoin
import java.security.AccessControlContext

class MyApplication : Application() {

    private val TAG = "MyApplication"
    override fun onCreate() {
        super.onCreate()
//        Log.e(TAG,"onCreate")
        startKoin(applicationContext, moduleList)
    }

    override fun onTerminate() {
        super.onTerminate()
//        Log.e(TAG,"onTerminate")
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
//        Log.e(TAG,"onTrimMemory")
    }

    override fun onLowMemory() {
        super.onLowMemory()
//        Log.e(TAG,"onLowMemory")
    }
}