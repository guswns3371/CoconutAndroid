package com.example.coconut.model.response.account

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserDataResponse(
    @SerializedName("id") var id : String,
    @SerializedName("profile_image") var profile_image : String ?,
    @SerializedName("back_image") var back_image : String ?,
    @SerializedName("name") var name : String,
    @SerializedName("email") var email : String,
    @SerializedName("user_id") var user_id : String,
    @SerializedName("message") var message : String ?,
    @SerializedName("err") var err : String ?,

    var status : Boolean
) : Parcelable {

    override fun toString(): String {
        return "UserDataResponse(id='$id', profile_image=$profile_image, back_image=$back_image, name='$name', email='$email', user_id='$user_id', message=$message, err=$err, status=$status)"
    }
}