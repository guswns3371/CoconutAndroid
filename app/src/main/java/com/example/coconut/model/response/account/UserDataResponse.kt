package com.example.coconut.model.response.account

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Keep
@Serializable
@Parcelize
data class UserDataResponse(
    @SerializedName("id") var id : String,
    @SerializedName("userId") var userId : String?,
    @SerializedName("name") var name : String,
    @SerializedName("email") var email : String,
    @SerializedName("stateMessage") var stateMessage : String ?,
    @SerializedName("profilePicture") var profilePicture : String ?,
    @SerializedName("backgroundPicture") var backgroundPicture : String ?,
    @SerializedName("err") var err : String ?,
    @SerializedName("status") var status : Boolean?
) : Parcelable {

    override fun toString(): String {
        return "UserDataResponse(id='$id', userId='$userId', name='$name', email='$email', stateMessage=$stateMessage, profilePicture=$profilePicture, backgroundPicture=$backgroundPicture, err=$err, status=$status)"
    }
}