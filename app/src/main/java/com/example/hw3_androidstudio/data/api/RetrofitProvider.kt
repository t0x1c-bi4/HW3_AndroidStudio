package com.example.hw3_androidstudio.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitProvider {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://swapi.py4e.com/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: SwapiApi = retrofit.create(SwapiApi::class.java)
}