package com.example.coconut.model.response.hashtag

import com.google.gson.annotations.SerializedName

data class JobDataResponse(
    @SerializedName("jobTitle") var jobTitle: String,
    @SerializedName("jobLink") var jobLink: String,
    @SerializedName("companyImage") var companyImage: String,
    @SerializedName("companyName") var companyName: String,
    @SerializedName("career") var career: String,
    @SerializedName("location") var location: String,
    @SerializedName("position") var position: String
) {
    override fun toString(): String {
        return "JobDataResponse(jobTitle='$jobTitle', jobLink='$jobLink', companyImage='$companyImage', companyName='$companyName', career='$career', location='$location', position='$position')"
    }
}

