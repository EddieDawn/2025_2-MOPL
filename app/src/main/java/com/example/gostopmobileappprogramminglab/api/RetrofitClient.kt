package com.example.gostopmobileappprogramminglab.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // RandomUser API
    private val randomUserRetrofit = Retrofit.Builder()
        .baseUrl("https://randomuser.me/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val randomUserApi: ApiService =
        randomUserRetrofit.create(ApiService::class.java)


    // ZenQuotes API
    private val quoteRetrofit = Retrofit.Builder()
        .baseUrl("https://zenquotes.io/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val quoteApi: QuoteApiService =
        quoteRetrofit.create(QuoteApiService::class.java)
}



