package com.example.coconut.model.api

import com.example.coconut.model.response.hashtag.*
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

    @GET("/api/crawl/notice")
    fun getSeoulTechList(): Single<ArrayList<NoticeDataResponse>>

    @GET("/api/crawl/job")
    fun getJobList(): Single<ArrayList<JobDataResponse>>
}