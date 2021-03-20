package com.example.coconut.oauth2

import com.google.gson.annotations.SerializedName

data class UserInfoResult(
    @SerializedName("email") val mEmail: String,
    @SerializedName("email_verified") val mEmailVerified: Boolean,
    @SerializedName("family_name") val mFamilyName: String,
    @SerializedName("given_name") val mGivenName: String,
    @SerializedName("hd") val mHd: String,
    @SerializedName("locale") val mLocale: String,
    @SerializedName("name") val mName: String,
    @SerializedName("picture") val mPicture: String,
    @SerializedName("sub") val mSub: String
) {
    override fun toString(): String {
        return "UserInfoResult(mEmail='$mEmail', mEmailVerified=$mEmailVerified, mFamilyName='$mFamilyName', mGivenName='$mGivenName', mName='$mName', mPicture='$mPicture')"
    }
}