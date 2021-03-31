package com.example.coconut.model.request.account

import okhttp3.MultipartBody
import okhttp3.RequestBody

data class AccountEditRequest(
    var id : RequestBody?,
    var userId : RequestBody?,
    var name : RequestBody?,
    var message : RequestBody?,
    var profileImage : MultipartBody.Part?,
    var backImage : MultipartBody.Part?
){
    override fun toString(): String {
        return "AccountEditRequest(user_id=$userId, user_img=$profileImage, back_img=$backImage)"
    }
}