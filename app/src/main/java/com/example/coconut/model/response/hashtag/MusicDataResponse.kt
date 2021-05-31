package com.example.coconut.model.response.hashtag

import com.google.gson.annotations.SerializedName

data class MusicDataResponse(
    @SerializedName("albumImage") var albumImage: String,
    @SerializedName("artist") var artist: String,
    @SerializedName("songTitle") var songTitle: String,
    @SerializedName("albumTitle") var albumTitle: String
) {
    override fun toString(): String {
        return "MusicDataResponse(albumImage='$albumImage', artist='$artist', songTitle='$songTitle', albumTitle='$albumTitle')"
    }
}
