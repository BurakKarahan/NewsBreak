package com.burakkarahan.newsbreak.service

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    val  retrofit = Retrofit.Builder()
        .baseUrl(BaseUrl.baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build().create(RetrofitAPI::class.java)
}

object RetrofitClientBurak {

    val  retrofit = Retrofit.Builder()
        .baseUrl(BaseUrl.baseUrlBurak)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build().create(RetrofitAPI::class.java)
}