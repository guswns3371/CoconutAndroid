package com.example.coconut.oauth2

interface UserInfoCallback {
    fun call(userInfo: UserInfo?, ex: AuthException?)
}