package com.example.coconut.model.api

import com.example.coconut.model.response.hashtag.CovidDataResponse
import com.example.coconut.model.response.hashtag.MusicDataResponse
import com.example.coconut.model.response.hashtag.NewsDataResponse
import io.reactivex.Single
import retrofit2.http.GET
import java.util.ArrayList

interface CrawlAPI {

    @GET("/api/crawl/covid")
    fun getCovidData(): Single<ArrayList<CovidDataResponse>>

    @GET("/api/crawl/news")
    fun getNewsData(): Single<ArrayList<NewsDataResponse>>

    @GET("/api/crawl/music")
    fun getMusicTopList(): Single<ArrayList<MusicDataResponse>>
}