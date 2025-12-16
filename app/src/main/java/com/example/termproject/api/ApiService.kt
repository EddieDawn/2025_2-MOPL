package com.example.termproject.api

import retrofit2.http.GET

interface ApiService {
    @GET("api/")
    suspend fun getRandomUser(): RandomUserResponse
}


