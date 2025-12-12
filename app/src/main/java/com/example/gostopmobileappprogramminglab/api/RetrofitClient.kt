package com.example.gostopmobileappprogramminglab.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Random User API
    private val randomUserRetrofit = Retrofit.Builder()
        .baseUrl("https://randomuser.me/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val randomUserApi: ApiService = randomUserRetrofit.create(ApiService::class.java)

    // Quotable API
    private val quotableRetrofit = Retrofit.Builder()
        .baseUrl("https://api.quotable.io/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val quoteApi: ApiService = quotableRetrofit.create(ApiService::class.java)
}
