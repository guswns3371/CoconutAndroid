package com.example.coconut.model.response.hashtag

import com.google.gson.annotations.SerializedName

data class NewsDataResponse(
    @SerializedName("thumbNailImage") var thumbNailImage: String,
    @SerializedName("newsUrl") var newsUrl: String,
    @SerializedName("title") var title: String,
    @SerializedName("newsName") var newsName: String
) {
    override fun toString(): String {
        return "NewsDataResponse(thumbNailImage='$thumbNailImage', newsUrl='$newsUrl', title='$title', newsName='$newsName')"
    }
}
