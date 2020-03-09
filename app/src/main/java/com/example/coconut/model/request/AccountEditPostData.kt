package com.example.coconut.model.request

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.Part

data class AccountEditPostData(
//    @Part("id") var id : RequestBody?,
//    @Part("user_id") var user_id : RequestBody?,
//    @Part("user_img") var user_img : MultipartBody.Part?,
//    @Part("back_img") var back_img : MultipartBody.Part?
     var id : RequestBody?,
     var user_id : RequestBody?,
     var user_name : RequestBody?,
     var user_msg : RequestBody?,
     var user_img : MultipartBody.Part?,
     var back_img : MultipartBody.Part?
){
    override fun toString(): String {
        return "AccountEditPostData(user_id=$user_id, user_img=$user_img, back_img=$back_img)"
    }
}