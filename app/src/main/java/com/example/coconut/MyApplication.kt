package com.example.coconut

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import androidx.annotation.ColorRes
import com.example.coconut.di.moduleList
import com.google.common.io.BaseEncoding
import org.koin.android.ext.android.startKoin
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class MyApplication : Application(){

    override fun onCreate() {
        super.onCreate()
        startKoin(applicationContext, moduleList)
        createNotificationChannel()
    }

    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val channelId = getString(R.string.default_notification_channel_id)
            val channelName = getString(R.string.default_notification_channel_name)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    channelName, NotificationManager.IMPORTANCE_HIGH
                )
            )
        }
    }

    fun getSignature(): String? {
        val pm = packageManager
        val packageName = packageName
        return try {
            val packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            if (packageInfo?.signatures == null || packageInfo.signatures.isEmpty() || packageInfo.signatures[0] == null
            ) {
                null
            } else signatureDigest(packageInfo.signatures[0])
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    private fun signatureDigest(sig: Signature): String? {
        val signature = sig.toByteArray()
        return try {
            val md = MessageDigest.getInstance("SHA1")
            val digest = md.digest(signature)
            BaseEncoding.base16().lowerCase().encode(digest)
        } catch (e: NoSuchAlgorithmException) {
            null
        }
    }

    fun getColorValue(@ColorRes color: Int): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getColor(color)
        } else {
            resources.getColor(color)
        }
    }
}