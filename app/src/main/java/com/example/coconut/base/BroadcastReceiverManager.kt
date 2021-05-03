package com.example.coconut.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter

interface BroadcastReceiverManager {

    val mBroadcastReceiver : BroadcastReceiver

    fun registerBroadcastReceiver(context: Context?, intentFilter: IntentFilter){
        context?.registerReceiver(mBroadcastReceiver,intentFilter)
    }

    fun unregisterBroadcastReceiver(context: Context?) {
        context?.unregisterReceiver(mBroadcastReceiver)
    }

    fun getBroadcastReceiver() = mBroadcastReceiver
}