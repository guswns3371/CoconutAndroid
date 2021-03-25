package com.example.coconut.ui.main.account

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.coconut.base.BaseKotlinViewModel
import com.example.coconut.model.MyRepository
import com.example.coconut.model.response.account.UserDataResponse
import com.example.coconut.util.MyPreference
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class AccountViewModel(private val myRepository: MyRepository,
                       private val pref: MyPreference) : BaseKotlinViewModel() {

    private val TAG = "AccountViewModel"

    private val _userDataResponseLiveData = MutableLiveData<ArrayList<UserDataResponse>>()
    val userDataResponseLiveData: LiveData<ArrayList<UserDataResponse>> = _userDataResponseLiveData

    fun getAllAccounts(){
        pref.userIdx?.let { id->
            addDisposable(
                myRepository.getAccountDatas(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it.run {
                        if (it.size>0){
                            //Log.e(TAG, "response : \n${toString()}")
                            _userDataResponseLiveData.postValue(this)
                        }
                    }
                },{
                    Log.e(TAG, "response error, message : ${it.message}")
                })
            )
        }

    }



}