package com.example.gostopmobileappprogramminglab.api

import retrofit2.http.GET

interface ApiService {

    @GET("api/")
    suspend fun getRandomUser(): RandomUserResponse

    @GET("random")
    suspend fun getRandomQuote(): QuoteResponse

}