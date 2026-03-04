package com.example.hw3_androidstudio.data.repository

import com.example.hw3_androidstudio.data.api.SwapiApi
import com.example.hw3_androidstudio.data.model.Person
import com.example.hw3_androidstudio.data.model.PersonDto

class PeopleRepository (
    private val api: SwapiApi
) {

    suspend fun getPeople(page: Int): Pair<List<Person>, Boolean> {
        val res = api.getPeople(page)

        return Pair(
            res.results.map { it.toDomain() },
            res.next != null
        )
    }

    suspend fun search(query: String, page: Int): Pair<List<Person>, Boolean> {
        val res = api.searchPeople(query, page)

        return Pair(
            res.results.map { it.toDomain() },
            res.next != null
        )
    }

    suspend fun getById(id: Int): Person =
        api.getPerson(id).toDomain()
}

private fun PersonDto.toDomain(): Person {
    val id = url.trimEnd('/').substringAfterLast('/').toInt()

    return Person(
        id,
        name,
        height,
        mass,
        hair_color,
        skin_color,
        eye_color,
        birth_year,
        gender
    )
}