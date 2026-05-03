package com.example.hw3_androidstudio.data.repository

import com.example.hw3_androidstudio.data.api.SwapiApi
import com.example.hw3_androidstudio.data.local.FavouriteDao
import com.example.hw3_androidstudio.data.local.FavouriteEntity
import com.example.hw3_androidstudio.data.model.Person
import com.example.hw3_androidstudio.data.model.PersonDto
import javax.inject.Inject

class PeopleRepository @Inject constructor(
    private val api: SwapiApi,
    private val dao: FavouriteDao
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

    suspend fun getFavourites(): List<Person> {
        return dao.getAll().map {
            Person(
                id = it.id,
                name = it.name,
                height = "",
                mass = "",
                hairColor = "",
                skinColor = "",
                eyeColor = "",
                birthYear = "",
                gender = ""
            )
        }
    }

    suspend fun addFavourite(person: Person) {
        dao.insert(FavouriteEntity(person.id, person.name))
    }

    suspend fun removeFavourite(person: Person) {
        dao.delete(FavouriteEntity(person.id, person.name))
    }
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