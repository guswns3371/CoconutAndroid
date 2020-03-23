package com.example.coconut.di

import android.util.Log
import com.example.coconut.Constant
import com.example.coconut.adapter.*
import com.example.coconut.model.MyRepository
import com.example.coconut.model.MyRepositoryImpl
import com.example.coconut.model.service.AuthService
import com.example.coconut.model.service.AccountService
import com.example.coconut.model.service.ChatService
import com.example.coconut.ui.main.account.AccountViewModel
import com.example.coconut.ui.auth.login.LoginViewModel
import com.example.coconut.ui.auth.passfind.PassFindViewModel
import com.example.coconut.ui.auth.register.RegisterViewModel
import com.example.coconut.ui.main.account.info.AccountInfoViewModel
import com.example.coconut.ui.main.chat.ChatViewModel
import com.example.coconut.ui.main.chat.inner.InnerChatViewModel
import com.example.coconut.ui.main.more.MoreViewModel
import com.example.coconut.ui.main.hashtag.HashTagViewModel
import com.example.coconut.util.MyPreference
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

var retrofitPart = module {
    //single : 앱이 살아있는 동안 전역적으로 사용가능한 객체
    //singleton 인스턴스 제공
    single { okhttp() }
    single { retrofit() }
    single { get<Retrofit>().create(AuthService::class.java) }
    single { get<Retrofit>().create(AccountService::class.java) }
    single { get<Retrofit>().create(ChatService::class.java) }
}
var socketPart = module {
//    single { socket()!! }
}
var adapterPart = module {
    //factory : inject 시정메 해당 객체를 샐성한다
    single { AccountRecyclerAdapter() }
    factory { InnerChatRecyclerAdapter(get()) }

    /** single로 할지 factory로 할지 나중에 결정*/
    factory { ChatListRecyclerAdpater(get()) }
    factory { AddChatRecyclerAdpater() }
    factory { InnerDrawerAdapter() }
}

var modelPart = module {
    factory<MyRepository> { MyRepositoryImpl(get(),get(),get()) }
    //get() 함수를 호출하면
    //컴포넌트 내에서 생성된 객체를 참조한다
}

var viewModelPart = module {
    viewModel { LoginViewModel(get(),get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { PassFindViewModel(get()) }

    /** fragment viewModels */
    viewModel { MoreViewModel(get()) }
    viewModel { AccountViewModel(get(),get()) }
    viewModel { AccountInfoViewModel(get()) }

    viewModel { ChatViewModel(get()) }
    viewModel { InnerChatViewModel(get()) }

    viewModel { HashTagViewModel() }
}

var sharedPreferencePart = module {
    single { MyPreference(androidApplication()) }
}

var moduleList =
    listOf(retrofitPart,
        socketPart,
        viewModelPart,
        adapterPart,
        modelPart,
        sharedPreferencePart)

private fun okhttp() = OkHttpClient.Builder().build()

private fun retrofit() = Retrofit.Builder()
    .callFactory(OkHttpClient.Builder().build())
    .baseUrl(Constant.NODE_URL)
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    .addConverterFactory(GsonConverterFactory.create())
    .build()


//private fun socket(): Socket? {
//    return try {
//        IO.socket(Constant.NODE_URL)
//    }catch (e : Exception){
//        Log.e("module","${e.message}")
//        null
//    }
//}
