package com.example.coconut.oauth2

interface AuthLogoutListener {
    fun onStart(repo: AuthRepo, event: AuthEvent) {}
    fun onSuccess(repo: AuthRepo, event: AuthEvent) {}
    fun onFailure(repo: AuthRepo, event: AuthEvent, ex: AuthException) {}
}