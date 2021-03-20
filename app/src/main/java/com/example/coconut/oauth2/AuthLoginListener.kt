package com.example.coconut.oauth2

import android.content.Intent

interface AuthLoginListener {
    fun onStart(repo : AuthRepo, event: AuthEvent) {}
    fun onEvent(repo: AuthRepo, event: AuthEvent) {}
    fun onUserAgentRequest(repo: AuthRepo, intent: Intent) {}
    fun onSuccess(repo: AuthRepo, event: AuthEvent) {}
    fun onFailure(repo: AuthRepo, event: AuthEvent, ex: AuthException) {}
}