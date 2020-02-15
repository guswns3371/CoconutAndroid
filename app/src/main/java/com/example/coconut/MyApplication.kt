package com.example.coconut

import android.app.Application
import com.example.coconut.di.moduleList
import org.koin.android.ext.android.startKoin

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin(applicationContext, moduleList)
    }
}