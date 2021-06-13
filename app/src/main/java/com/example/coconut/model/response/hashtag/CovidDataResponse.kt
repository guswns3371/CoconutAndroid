package com.example.coconut.model.response.hashtag

import com.google.gson.annotations.SerializedName


data class CovidDataResponse(
    @SerializedName("country") var country: String,
    @SerializedName("diffFromPrevDay") var diffFromPrevDay: String,
    @SerializedName("total") var total: String,
    @SerializedName("death") var death: String,
    @SerializedName("inspection") var inspection: String
) {

}

/**
private String country; // 시도명
private String diffFromPrevDay; // 전일대비확진환자증감
private String total; // 확진환자수
private String death; // 사망자수
private String incidence; // 발병률
private String inspection; // 일일 검사환자 수
 */