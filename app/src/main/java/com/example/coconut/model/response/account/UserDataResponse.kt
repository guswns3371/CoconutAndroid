package com.example.coconut.model.response.account

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserDataResponse(
    @SerializedName("id") var id : String,
    @SerializedName("profile_picture") var profile_picture : String ?,
    @SerializedName("background_picture") var background_picture : String ?,
    @SerializedName("name") var name : String,
    @SerializedName("email") var email : String,
    @SerializedName("user_id") var user_id : String,
    @SerializedName("state_message") var state_message : String ?,
    @SerializedName("err") var err : String ?,
    var status : Boolean
) : Parcelable {

    override fun toString(): String {
        return "UserDataResponse(id='$id', profile_picture=$profile_picture, background_picture=$background_picture, name='$name', email='$email', user_id='$user_id', state_message=$state_message, err=$err, status=$status)"
    }
}