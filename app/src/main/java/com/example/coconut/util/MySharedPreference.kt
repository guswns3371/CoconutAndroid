package com.example.coconut.util

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken


private const val PRE_FILENAME = "prefs_"

private const val USER_ID = "USER_ID"
private const val FCM_TOKEN = "FCM_TOKEN"

class MyPreference(private var app: Application) {

    private val prefs: SharedPreferences = app.getSharedPreferences(PRE_FILENAME, Context.MODE_PRIVATE)
    private val editor = prefs.edit()
    private val gson = GsonBuilder().create()
    inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object: TypeToken<T>() {}.type)


    var userID : String?
        get() = prefs.getString(USER_ID,null)
        set(value) = editor.putString(USER_ID,value).apply()

    fun resetUserId(){
        editor.remove(USER_ID).apply()
    }

    var fcmToken : String?
        get() = prefs.getString(FCM_TOKEN,null)
        set(value) = editor.putString(FCM_TOKEN,value).apply()

    fun resetFcmToken(){
        editor.remove(FCM_TOKEN).apply()
    }
}