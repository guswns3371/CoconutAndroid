package com.example.coconut.ui.auth.passfind

import android.util.Log
import androidx.databinding.ObservableField
import com.example.coconut.base.BaseKotlinViewModel
import com.example.coconut.model.UserDataModel

class PassFindViewModel(private val modelUser : UserDataModel): BaseKotlinViewModel() {
    private val TAG = "PassFindViewModel"

    val emailClicked = ObservableField<Boolean>()
    val smsClicked = ObservableField<Boolean>()

    val email = ObservableField<String>()
    val emailForSMS = ObservableField<String>()
    val phoneNumber = ObservableField<String>()

    override fun onCreate() {
        emailClicked.set(false)
        smsClicked.set(false)
    }

    fun findByEmail(){
        emailClicked.set(true)
        smsClicked.set(false)
        email.set(null)
        phoneNumber.set(null)
    }

    fun findBySMS(){
        emailClicked.set(false)
        smsClicked.set(true)
        emailForSMS.set(null)
    }

    fun findPasswordBtn(){
        when(emailClicked.get()){
            true->{ //이메일로 비번 찾기
                Log.e(TAG,"${email.get()}")
            }
            false->{ //SMS로 비번 찾기
                Log.e(TAG,"${emailForSMS.get()} / ${phoneNumber.get()}")
            }
        }
    }
}