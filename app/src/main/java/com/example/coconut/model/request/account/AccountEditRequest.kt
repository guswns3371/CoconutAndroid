package com.example.coconut.model.request.account

import okhttp3.MultipartBody
import okhttp3.RequestBody

data class AccountEditRequest(
    var id : RequestBody?,
    var userId : RequestBody?,
    var userName : RequestBody?,
    var userMsg : RequestBody?,
    var userImg : MultipartBody.Part?,
    var backImg : MultipartBody.Part?
){
    override fun toString(): String {
        return "AccountEditRequest(user_id=$userId, user_img=$userImg, back_img=$backImg)"
    }
}