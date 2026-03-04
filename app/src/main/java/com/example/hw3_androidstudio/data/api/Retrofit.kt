package com.example.hw3_androidstudio.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Retrofit {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://swapi.dev/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: SwapiApi = retrofit.create(SwapiApi::class.java)
}