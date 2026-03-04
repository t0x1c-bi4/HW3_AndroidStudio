package com.example.hw3_androidstudio.data.api

import com.example.hw3_androidstudio.data.model.PeopleResponse
import com.example.hw3_androidstudio.data.model.PersonDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SwapiApi {

    @GET("people/")
    suspend fun getPeople(
        @Query("page") page: Int
    ): PeopleResponse

    @GET("people/")
    suspend fun searchPeople(
        @Query("search") query: String,
        @Query("page") page: Int
    ): PeopleResponse

    @GET("people/{id}/")
    suspend fun getPerson(
        @Path("id") id: Int
    ): PersonDto
}