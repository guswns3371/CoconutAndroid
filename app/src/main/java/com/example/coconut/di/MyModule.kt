package com.example.coconut.di

import com.example.coconut.Constant
import com.example.coconut.MyApplication
import com.example.coconut.adapter.*
import com.example.coconut.model.MyRepository
import com.example.coconut.model.MyRepositoryImpl
import com.example.coconut.model.api.AccountAPI
import com.example.coconut.model.api.AuthAPI
import com.example.coconut.model.api.ChatAPI
import com.example.coconut.oauth2.AuthRepo
import com.example.coconut.ui.auth.login.LoginViewModel
import com.example.coconut.ui.auth.passfind.PassFindViewModel
import com.example.coconut.ui.auth.register.RegisterViewModel
import com.example.coconut.ui.main.account.AccountViewModel
import com.example.coconut.ui.main.account.info.AccountInfoViewModel
import com.example.coconut.ui.main.chat.ChatViewModel
import com.example.coconut.ui.main.chat.inner.InnerChatViewModel
import com.example.coconut.ui.main.hashtag.HashTagViewModel
import com.example.coconut.ui.main.more.MoreViewModel
import com.example.coconut.util.MyPreference
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


var retrofitPart = module {
    //single : 앱이 살아있는 동안 전역적으로 사용가능한 객체
    //singleton 인스턴스 제공
    single { okHttpClient() }
    single { retrofit() }
    single { get<Retrofit>().create(AuthAPI::class.java) }
    single { get<Retrofit>().create(AccountAPI::class.java) }
    single { get<Retrofit>().create(ChatAPI::class.java) }
}
var socketPart = module {
//    single { socket()!! }
}
var adapterPart = module {
    //factory : inject 시점에 해당 객체를 샐성한다
    single { AccountRecyclerAdapter() }
    factory { InnerChatRecyclerAdapter(get()) }

    /** single로 할지 factory로 할지 나중에 결정*/
    factory { ChatListRecyclerAdpater(get()) }
    factory { AddChatRecyclerAdpater() }
    factory { InnerDrawerAdapter() }
}

var modelPart = module {
    factory<MyRepository> { MyRepositoryImpl(get(), get(), get()) }
    //get() 함수를 호출하면
    //컴포넌트 내에서 생성된 객체를 참조한다
}

var authPart = module {
    single { AuthRepo(androidApplication() as MyApplication, get() ) }
}

var viewModelPart = module {
    viewModel { LoginViewModel(get(), get(), get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { PassFindViewModel(get()) }

    /** fragment viewModels */
    viewModel { MoreViewModel(get()) }
    viewModel { AccountViewModel(get(), get()) }
    viewModel { AccountInfoViewModel(get()) }

    viewModel { ChatViewModel(get()) }
    viewModel { InnerChatViewModel(get()) }

    viewModel { HashTagViewModel() }
}

var sharedPreferencePart = module {
    single { MyPreference(androidApplication()) }
}

var moduleList =
    listOf(
        retrofitPart,
        socketPart,
        viewModelPart,
        adapterPart,
        modelPart,
        sharedPreferencePart,
        authPart
    )

var gson: Gson = GsonBuilder()
    .setLenient()
    .create()

private fun okHttpClient() = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
    .build()

private fun retrofit() = Retrofit.Builder()
    .client(okHttpClient())
    .baseUrl(Constant.SPRING_BOOT_URL)
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    .addConverterFactory(GsonConverterFactory.create(gson))
    .build()

//private fun socket(): Socket? {
//    return try {
//        IO.socket(Constant.NODE_URL)
//    }catch (e : Exception){
//        Log.e("module","${e.message}")
//        null
//    }
//}



