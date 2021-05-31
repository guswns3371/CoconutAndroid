package com.example.coconut.ui.main.hashtag

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.coconut.base.BaseKotlinViewModel
import com.example.coconut.model.MyRepository
import com.example.coconut.model.response.account.UserDataResponse
import com.example.coconut.model.response.hashtag.CovidDataResponse
import com.example.coconut.model.response.hashtag.MusicDataResponse
import com.example.coconut.model.response.hashtag.NewsDataResponse
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

    fun getCovidData() {
        addDisposable(
            myRepository.getCovidData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e(TAG, "response : \n${it}")
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

}