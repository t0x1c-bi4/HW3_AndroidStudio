package com.example.hw3_androidstudio.viewmodel

import androidx.compose.runtime.mutableStateOf
import com.example.hw3_androidstudio.data.repository.PeopleRepository
import com.example.hw3_androidstudio.data.model.Person
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PeopleViewModel @Inject constructor(
    private val repository: PeopleRepository
) : ViewModel() {

    var state by mutableStateOf<PeopleUiState>(PeopleUiState.Loading)
        private set

    var favourites by mutableStateOf<List<Person>>(emptyList())
        private set

    fun loadFavourites() {
        viewModelScope.launch {
            favourites = repository.getFavourites()
        }
    }

    var query by mutableStateOf("")
        private set

    private var page = 1
    private var currentQuery: String? = null

    init {
        load()
        loadFavourites()
    }

    fun load() {
        page = 1
        currentQuery = null

        viewModelScope.launch {
            state = PeopleUiState.Loading

            try {
                val (list, canLoadMore) = repository.getPeople(page)

                state = if (list.isEmpty())
                    PeopleUiState.Empty
                else
                    PeopleUiState.Success(list, canLoadMore)

            } catch (e: Exception) {
                state = PeopleUiState.Error("Ошибка загрузки")
            }
        }
    }

    fun search(query: String) {
        page = 1
        currentQuery = query

        viewModelScope.launch {
            state = PeopleUiState.Loading

            try {
                val (list, canLoadMore) = repository.search(query, page)

                state = if (list.isEmpty())
                    PeopleUiState.Empty
                else
                    PeopleUiState.Success(list, canLoadMore)

            } catch (e: Exception) {
                state = PeopleUiState.Error("Ошибка поиска")
            }
        }
    }

    fun loadMore() {
        val current = state as? PeopleUiState.Success ?: return
        if (!current.canLoadMore || current.loadingMore) return

        viewModelScope.launch {

            state = current.copy(loadingMore = true)
            page++

            try {
                val (newItems, canLoadMore) =
                    if (currentQuery == null)
                        repository.getPeople(page)
                    else
                        repository.search(currentQuery!!, page)

                state = PeopleUiState.Success(
                    list = current.list + newItems,
                    canLoadMore = canLoadMore,
                    loadingMore = false
                )

            } catch (e: Exception) {
                state = current.copy(loadingMore = false)
            }
        }
    }

    fun toggleFav(person: Person) {
        viewModelScope.launch {

            val isFav = favourites.any { it.id == person.id }

            if (isFav) {
                repository.removeFavourite(person)
            } else {
                repository.addFavourite(person)
            }

            loadFavourites()
        }
    }

    fun getFavouritesList(): List<Person> = favourites

    fun onQueryChange(newQuery: String) {
        query = newQuery
        search(newQuery)
    }
}