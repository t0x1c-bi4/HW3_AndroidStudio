package com.example.hw3_androidstudio.data.local

import com.example.hw3_androidstudio.data.api.SwapiApi
import com.example.hw3_androidstudio.data.model.PeopleResponse
import com.example.hw3_androidstudio.data.model.PersonDto

class FakeApi : SwapiApi {

    var shouldThrowError = false
    var people = listOf<PersonDto>()

    override suspend fun getPeople(page: Int): PeopleResponse {
        if (shouldThrowError) throw Exception("Error")

        return PeopleResponse(
            results = people,
            next = null
        )
    }

    override suspend fun getPerson(id: Int): PersonDto {
        return people.first()
    }

    override suspend fun searchPeople(query: String, page: Int): PeopleResponse {
        return PeopleResponse(
            results = people.filter { it.name.contains(query, true) },
            next = null
        )
    }
}