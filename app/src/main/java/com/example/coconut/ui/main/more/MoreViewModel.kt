package com.example.coconut.ui.main.more

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.coconut.base.BaseKotlinViewModel
import com.example.coconut.model.MyRepository

class MoreViewModel(private val modelUser : MyRepository) : BaseKotlinViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is More Fragment"
    }
    val text: LiveData<String> = _text
}