package com.example.coconut.ui.main.hashtag

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.coconut.base.BaseKotlinViewModel

class HashTagViewModel : BaseKotlinViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is HashTag Fragment"
    }
    val text: LiveData<String> = _text
}