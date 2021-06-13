package com.example.coconut.model.response.hashtag

import com.google.gson.annotations.SerializedName

data class NoticeDataResponse(
    @SerializedName("link") var link: String,
    @SerializedName("title") var title: String,
    @SerializedName("author") var author: String,
    @SerializedName("date") var date: String
) {
    override fun toString(): String {
        return "NoticeDataResponse(link='$link', title='$title', author='$author', date='$date')"
    }
}
