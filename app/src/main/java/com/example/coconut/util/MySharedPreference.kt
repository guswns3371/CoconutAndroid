package com.example.coconut.util

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken


private const val PRE_FILENAME = "prefs_"

private const val USER_IDX = "USER_IDX"
private const val FCM_TOKEN = "FCM_TOKEN"
private const val OAUTH_ACCESS_TOKEN = "OAUTH_ACCESS_TOKEN"
private const val OAUTH_REFRESH_TOKEN = "OAUTH_REFRESH_TOKEN"

class MyPreference(private var app: Application) {

    private val prefs: SharedPreferences = app.getSharedPreferences(PRE_FILENAME, Context.MODE_PRIVATE)
    private val editor = prefs.edit()
    private val gson = GsonBuilder().create()
    inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object: TypeToken<T>() {}.type)!!


    var userIdx : String?
        get() = prefs.getString(USER_IDX,null)
        set(value) = editor.putString(USER_IDX,value).apply()

    fun resetUserId(){
        editor.remove(USER_IDX).apply()
    }

    var fcmToken : String?
        get() = prefs.getString(FCM_TOKEN,null)
        set(value) = editor.putString(FCM_TOKEN,value).apply()

    fun resetFcmToken(){
        editor.remove(FCM_TOKEN).apply()
    }

    var accessToken : String?
        get() = prefs.getString(OAUTH_ACCESS_TOKEN,null)
        set(value) = editor.putString(OAUTH_ACCESS_TOKEN,value).apply()

    fun resetAccessToken() {
        editor.remove(OAUTH_ACCESS_TOKEN).apply()
    }

    var refreshToken : String?
        get() = prefs.getString(OAUTH_REFRESH_TOKEN,null)
        set(value) = editor.putString(OAUTH_REFRESH_TOKEN,value).apply()

    fun resetRefreshToken() {
        editor.remove(OAUTH_REFRESH_TOKEN).apply()
    }

}