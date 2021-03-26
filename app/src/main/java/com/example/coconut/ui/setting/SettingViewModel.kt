package com.example.coconut.ui.setting

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.coconut.base.BaseKotlinViewModel
import com.example.coconut.oauth2.AuthEvent
import com.example.coconut.oauth2.AuthException
import com.example.coconut.oauth2.AuthLogoutListener
import com.example.coconut.oauth2.AuthRepo
import com.example.coconut.ui.auth.login.LoginViewModel
import com.example.coconut.util.Event
import com.example.coconut.util.MyPreference

class SettingViewModel(
    private val authRepo: AuthRepo
    ) : BaseKotlinViewModel() {

    private val TAG = "SettingViewModel"


    private val _logoutObservable = MutableLiveData<Event<Boolean>>()
    val logoutObservable : LiveData<Event<Boolean>>
        get() = _logoutObservable

    private val logoutListener : AuthLogoutListener = object : AuthLogoutListener {
        override fun onStart(repo: AuthRepo, event: AuthEvent) {
            Log.i(TAG, event.getDescription())
        }

        override fun onSuccess(repo: AuthRepo, event: AuthEvent) {
            Log.i(TAG, event.getDescription())
            _logoutObservable.value = Event(true)
        }

        override fun onFailure(repo: AuthRepo, event: AuthEvent, ex: AuthException) {
            Log.i(TAG, event.getDescription() + ": " + ex.message)
            _logoutObservable.value = Event(false)
        }
    }

    fun logout(){
        authRepo.logout(logoutListener)
    }
}