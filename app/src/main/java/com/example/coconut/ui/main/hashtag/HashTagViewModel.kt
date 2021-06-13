package com.example.coconut.ui.main.hashtag

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.coconut.base.BaseKotlinViewModel
import com.example.coconut.model.MyRepository
import com.example.coconut.model.response.account.UserDataResponse
import com.example.coconut.model.response.hashtag.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class HashTagViewModel(
    private val myRepository: MyRepository
) : BaseKotlinViewModel() {

    private val TAG = "HashTagViewModel"

    private val _covidDataResponseLiveData = MutableLiveData<ArrayList<CovidDataResponse>>()
    val covidDataResponseLiveData: LiveData<ArrayList<CovidDataResponse>> = _covidDataResponseLiveData

    private val _newsDataResponseLiveData = MutableLiveData<ArrayList<NewsDataResponse>>()
    val newsDataResponseLiveData: LiveData<ArrayList<NewsDataResponse>> = _newsDataResponseLiveData

    private val _musicDataResponseLiveData = MutableLiveData<ArrayList<MusicDataResponse>>()
    val musicDataResponseLiveData: LiveData<ArrayList<MusicDataResponse>> = _musicDataResponseLiveData

    private val _noticeDataResponseLiveData = MutableLiveData<ArrayList<NoticeDataResponse>>()
    val noticeDataResponseLiveData: LiveData<ArrayList<NoticeDataResponse>> = _noticeDataResponseLiveData

    private val _jobDataResponseLiveData = MutableLiveData<ArrayList<JobDataResponse>>()
    val jobDataResponseLiveData: LiveData<ArrayList<JobDataResponse>> = _jobDataResponseLiveData

    fun getCovidData() {
        addDisposable(
            myRepository.getCovidData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e(TAG, "response : \n${it}")
                    it.add(0, CovidDataResponse("지역", "증가", "확진자수", "사망자", "발생률"))
                    _covidDataResponseLiveData.postValue(it)
                }, {
                    Log.e(TAG, "response error, message : ${it.message}")
                    _covidDataResponseLiveData.postValue(arrayListOf())
                })
        )
    }

    fun getNewsData() {
        addDisposable(
            myRepository.getNewsData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e(TAG, "response : \n${it}")
                    _newsDataResponseLiveData.postValue(it)
                }, {
                    Log.e(TAG, "response error, message : ${it.message}")
                    _newsDataResponseLiveData.postValue(arrayListOf())
                })
        )
    }

    fun getMusicTopList() {
        addDisposable(
            myRepository.getMusicTopList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e(TAG, "response : \n${it}")
                    _musicDataResponseLiveData.postValue(it)
                }, {
                    Log.e(TAG, "response error, message : ${it.message}")
                    _musicDataResponseLiveData.postValue(arrayListOf())
                })
        )
    }

    fun getSeoulTechList() {
        addDisposable(
            myRepository.getSeoulTechList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e(TAG, "response : \n${it}")
                    _noticeDataResponseLiveData.postValue(it)
                }, {
                    Log.e(TAG, "response error, message : ${it.message}")
                    _noticeDataResponseLiveData.postValue(arrayListOf())
                })
        )
    }

    fun getJobList() {
        addDisposable(
            myRepository.getJobList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e(TAG, "response : \n${it}")
                    _jobDataResponseLiveData.postValue(it)
                }, {
                    Log.e(TAG, "response error, message : ${it.message}")
                    _jobDataResponseLiveData.postValue(arrayListOf())
                })
        )
    }
}