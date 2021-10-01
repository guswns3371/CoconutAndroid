package com.example.coconut.ui.auth.register

import android.text.TextUtils
import android.util.Log
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.coconut.base.BaseKotlinViewModel
import com.example.coconut.model.MyRepository
import com.example.coconut.model.request.auth.RegisterRequest
import com.example.coconut.model.response.auth.RegisterResponse
import com.example.coconut.model.data.RegisterValidData
import com.example.coconut.model.request.auth.EmailCheckRequest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class RegisterViewModel(private val model : MyRepository) : BaseKotlinViewModel() {

    private val TAG = "RegisterViewModel"

    private val _registerResponseLiveData = MutableLiveData<RegisterResponse>()
    val registerResponseLiveData : LiveData<RegisterResponse>
        get() = _registerResponseLiveData

    private val _registerValidLiveData = MutableLiveData<RegisterValidData>()
    val registerValidDataLiveData : LiveData<RegisterValidData>
        get() = _registerValidLiveData

    //data binding한 edittext 들
    val email = ObservableField<String>()
    val name = ObservableField<String>()
    val id = ObservableField<String>()
    val password = ObservableField<String>()
    val passwordConfirm = ObservableField<String>()

    //register button의 enabled 요소 결정요소
    val isValid = ObservableField<Boolean>()
    val isEmailValid = ObservableField<Boolean>()
    private var isValidEmail = false

    private val emailPattern : String = "[a-zA-Z0-9._-]+@[a-z]+\\.+([a-z])+"


    override fun onCreate() {
        val callback = object : Observable.OnPropertyChangedCallback(){
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                registerValidation()
            }
        }
        val emailCallback = object : Observable.OnPropertyChangedCallback(){
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                isValidEmail = false
                registerValidation()
            }
        }
        email.addOnPropertyChangedCallback(emailCallback)
        id.addOnPropertyChangedCallback(callback)
        name.addOnPropertyChangedCallback(callback)
        password.addOnPropertyChangedCallback(callback)
        passwordConfirm.addOnPropertyChangedCallback(callback)
    }

    fun emailCheckButton(){
        addDisposable(model.checkEmailValidation(EmailCheckRequest(email.get().toString()))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it?.run {
                    Log.e(TAG,"registerResponse : ${toString()}")

                    isValidEmail = this.isEmailOk
                    registerValidation()
                    _registerResponseLiveData.postValue(this)
                }
            },{
                isValidEmail = false
                Log.e(TAG, "response error, message : ${it.message}")
                showSnackbar("${it.message}")
            })
        )
    }
    fun registerButton(){
        addDisposable(model.doRegister(
            RegisterRequest(
                email.get().toString().trim(),
                id.get().toString().trim(),
                name.get().toString().trim(),
                password.get().toString().trim())
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it?.run {
                    Log.e(TAG,"registerResponse : ${toString()}")
                    _registerResponseLiveData.postValue(this)
                }
            },{
                Log.e(TAG, "response error, message : ${it.message}")
                showSnackbar("${it.message}")
            })
        )
    }

    private fun registerValidation(){

        val emailValid = !TextUtils.isEmpty(email.get()) &&  email.get().toString().matches(emailPattern.toRegex())
        val nameValid = !TextUtils.isEmpty(name.get()) && name.get().toString().length>=2 && name.get().toString().length <=8 && !name.get().toString().contains(" ")
        val idValid = !TextUtils.isEmpty(id.get()) && id.get().toString().length>=2 && id.get().toString().length <=20 && !id.get().toString().contains(" ")
        val passwordValid  = !TextUtils.isEmpty(password.get())
        val passwordConfirmValid  = !TextUtils.isEmpty(passwordConfirm.get()) && (password.get().toString() == passwordConfirm.get().toString())

        _registerValidLiveData.postValue(
            RegisterValidData(
                idValid,
                nameValid,
                passwordValid,
                passwordConfirmValid
            )
        )

        isEmailValid.set(emailValid)
        isValid.set(emailValid && nameValid && idValid && passwordValid && passwordConfirmValid && isValidEmail)
    }
}