package com.example.coconut.ui.auth.login

import android.text.TextUtils
import android.util.Log
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.coconut.base.BaseKotlinViewModel
import com.example.coconut.model.MyRepository
import com.example.coconut.model.request.EmailVerifyPostData
import com.example.coconut.model.request.FcmTokenPostData
import com.example.coconut.model.request.LoginPostData
import com.example.coconut.model.response.auth.LoginResponse
import com.example.coconut.util.Event
import com.example.coconut.util.MyPreference
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

// koin으로 model 과 viewmodel을 연결해준다 => private val modelUser : UserDataModel
class LoginViewModel(private val modelUser : MyRepository,
                     private val pref: MyPreference) :BaseKotlinViewModel(){
    private val TAG = "LoginViewModel"

    //MutableLiveData 란 변경할 수 있는 LiveData 형입니다.
    //일반적인 LiveData 형은 변경할 수 없고 오로지 데이터의 변경값만을 소비하는데 반해
    //MutableLiveData 는 데이터를 UI Thread 와 Background Thread 에서 선택적으로 바꿀 수 있습니다.
    private val _loginResponseLiveData = MutableLiveData<Event<LoginResponse>>()

    //  _loginResponseLiveData 선언된 MutableLiveData를 loginResponseLiveData 통해 발행합니다.
    // 이렇듯 ViewModel 에서만 loginResponseLiveData 변경할 수 있기때문에 보안에 더 좋습니다.
    // LiveData를 더 잘쓰려면 데이터바인딩과 함께 사용해야 좋은 효과
    val loginResponseLiveData : LiveData<Event<LoginResponse>>
        get() = _loginResponseLiveData

    /**Login*/
    val email = ObservableField<String>()
    val password = ObservableField<String>()
    val isValid = ObservableField<Boolean>()

    /**Verify Email*/
    val secretToken = ObservableField<String>()

    override fun onCreate() {
        val callback = object :Observable.OnPropertyChangedCallback(){
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                loginValidation()
            }
        }
        email.addOnPropertyChangedCallback(callback)
        password.addOnPropertyChangedCallback(callback)
    }

    /**원래는 activity에 클릭리스너속에 들어가야할 메소드지만
     * email 과 password 값을 매개변수 없이 바로 넘기기위해 data binding을 사용하여 viewmodel속에 놓은것*/
    fun loginCheck(){
        addDisposable(modelUser.doLogin(
            LoginPostData(email.get().toString(), password.get().toString()))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it?.run {
                    Log.e(TAG,"loginCheck :${toString()}")
                    //sharedpreference에 user id 값을 저장한다
                    if (isConfirmed){
                        pref.userID = id

                        /**서버에 fcm과 id를 보내어 저장한다
                        앱이 처음 설치되었고, token이 compromised, 가입한 적이 없는 유저일 경우
                        => emailVerify()를 통해 서버에 전달 (LoginViewModel)

                        앱이 처음 설치되었고, token이 compromised, 가입한 적이 있는 유저일 경우
                        => loginCheck()를 통해 서버에 전달 (LoginViewModel)

                        앱이 다시 실행되고, token이 compromised, pref.userID 값이 저장되어있을경우
                        => sendFcmTokenToServer()를 통해 서버에 전달 (MyFirebaseMessagingService)

                        앱이 다시 실행되고, pref.token 값이 null 일경우
                        => fcmToken()를 통해 서버에 전달 (LogoActivity)
                         */
                        sendFcmTokenToServer(id,pref.fcmToken)
                    }


                    // MutableLiveData에ㅓ setValue, postValue 실행 하는 경우
//                    _loginResponseLiveData.postValue(this)
                    _loginResponseLiveData.value = Event(this)
                    // Background Thread에서 실행
                    // setValue(this) 와 다름
                }
            },{
                Log.d(TAG, "response error, message : ${it.message}")
                showSnackbar("${it.message}")
            }))
    }

    /**Verify Email*/
    fun emailVerify(){
        addDisposable(modelUser.emailVerify(
            EmailVerifyPostData(email.get().toString(),secretToken.get().toString()))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it?.run {
                    Log.e(TAG,"emailVerify :${toString()}")
                    //sharedpreference에 user id 값을 저장한다
                    if (isConfirmed){
                        pref.userID = id

                         /**서버에 fcm과 id를 보내어 저장한다
                         앱이 처음 설치되었고, token이 compromised, 가입한 적이 없는 유저일 경우
                          => emailVerify()를 통해 서버에 전달 (LoginViewModel)

                         앱이 처음 설치되었고, token이 compromised, 가입한 적이 있는 유저일 경우
                          => loginCheck()를 통해 서버에 전달 (LoginViewModel)

                         앱이 다시 실행되고, token이 compromised, pref.userID 값이 저장되어있을경우
                          => sendFcmTokenToServer()를 통해 서버에 전달 (MyFirebaseMessagingService)

                          앱이 다시 실행되고, pref.token 값이 null 일경우
                          => fcmToken()를 통해 서버에 전달 (LogoActivity)
                         */
                        sendFcmTokenToServer(id,pref.fcmToken)
                    }

                    // MutableLiveData에ㅓ setValue, postValue 실행 하는 경우
//                    _loginResponseLiveData.postValue(this)
                    _loginResponseLiveData.value = Event(this)
                    // Background Thread에서 실행
                    // setValue(this) 와 다름
                }
            },{
                Log.d(TAG, "response error, message : ${it.message}")
                showSnackbar("${it.message}")
            }))
    }

    /** MyFirebaseMessagingService sendRegistrationToServer() */
    fun sendFcmTokenToServer(id : String, token : String?){
        addDisposable(modelUser.sendFcmTokenToServer(
                FcmTokenPostData(id,token)
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it?.run {
                    Log.e(TAG, "sendFcmTokenToServer response : ${toString()}")
                }
            },{
                Log.e(TAG, "sendFcmTokenToServer response error, message : ${it.message}")
            }))
    }

    fun deleteFcmTokenFromServer(id : String){
        addDisposable(modelUser.deleteFcmTokenFromServer(
                id
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it?.run {
                    Log.e(TAG, "sendFcmTokenToServer response : ${toString()}")
                }
            },{
                Log.e(TAG, "sendFcmTokenToServer response error, message : ${it.message}")
            }))
    }
    private fun loginValidation() {
        val emailValidation = !TextUtils.isEmpty(email.get())
        val passwordValidation = !TextUtils.isEmpty(password.get())
        isValid.set((emailValidation && passwordValidation))
    }
}