package com.example.coconut.di

import com.example.coconut.Constant
import com.example.coconut.model.UserDataModel
import com.example.coconut.model.UserDataModelImpl
import com.example.coconut.model.service.CocoaService
import com.example.coconut.ui.auth.login.LoginViewModel
import com.example.coconut.ui.auth.passfind.PassFindViewModel
import com.example.coconut.ui.auth.register.RegisterViewModel
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

var retrofitPart = module {

    single {
        okhttp()
    }
    single {
        retrofit(Constant.BASE_URL)
    }
    single {
        get<Retrofit>().create(CocoaService::class.java)
    }
}

var modelPart = module {
    factory<UserDataModel> {
        UserDataModelImpl(get())
    }
}

var viewModelPart = module {
    viewModel {
        LoginViewModel(get())
    }

    viewModel {
        RegisterViewModel(get())
    }

    viewModel {
        PassFindViewModel(get())
    }
}
var moduleList = listOf(retrofitPart, viewModelPart, modelPart)

private fun okhttp() = OkHttpClient.Builder().build()

private fun retrofit(baseURL : String) = Retrofit.Builder()
    .callFactory(OkHttpClient.Builder().build())
    .baseUrl(baseURL)
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    .addConverterFactory(GsonConverterFactory.create())
    .build()