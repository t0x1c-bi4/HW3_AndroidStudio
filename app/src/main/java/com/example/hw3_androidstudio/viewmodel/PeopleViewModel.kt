package com.example.hw3_androidstudio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hw3_androidstudio.data.model.Person
import com.example.hw3_androidstudio.data.repository.PeopleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PeopleLoadState {
    data object Loading : PeopleLoadState()

    data class Success(
        val people: List<Person>,
        val canLoadMore: Boolean,
        val loadingMore: Boolean = false
    ) : PeopleLoadState()

    data object Empty : PeopleLoadState()

    data class Error(val message: String) : PeopleLoadState()
}

private data class PageResult(
    val page: Int,
    val people: List<Person>,
    val canLoadMore: Boolean
)

@HiltViewModel
class PeopleViewModel @Inject constructor(
    private val repository: PeopleRepository
) : ViewModel() {

    private val queryFlow = MutableStateFlow("")
    private val filterFlow = MutableStateFlow(PeopleFilter.ALL)
    private val refreshFlow = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    private val loadMoreFlow = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    val query: StateFlow<String> = queryFlow.asStateFlow()
    val filter: StateFlow<PeopleFilter> = filterFlow.asStateFlow()

    val favourites: StateFlow<List<Person>> = repository
        .observeFavourites()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val favouriteIdsFlow: Flow<Set<Int>> =
        repository.observeFavouriteIds()

    private val debouncedQueryFlow = queryFlow
        .debounce(300)
        .distinctUntilChanged()

    private val remotePeopleFlow: Flow<PeopleLoadState> =
        combine(
            refreshFlow.onStart { emit(Unit) },
            debouncedQueryFlow
        ) { _, query ->
            query
        }.flatMapLatest { query ->

            loadMoreFlow
                .onStart { emit(Unit) }
                .scan(0) { page, _ -> page + 1 }
                .dropFirst()
                .mapLatest { page ->
                    val result =
                        if (query.isBlank()) {
                            repository.getPeople(page)
                        } else {
                            repository.search(query, page)
                        }

                    PageResult(
                        page = page,
                        people = result.first,
                        canLoadMore = result.second
                    )
                }
                .scan< PageResult, PeopleLoadState >(PeopleLoadState.Loading) { state, result ->
                    when {
                        result.page == 1 -> {
                            if (result.people.isEmpty()) {
                                PeopleLoadState.Empty
                            } else {
                                PeopleLoadState.Success(
                                    people = result.people,
                                    canLoadMore = result.canLoadMore,
                                    loadingMore = false
                                )
                            }
                        }

                        state is PeopleLoadState.Success -> {
                            val combined = state.people + result.people

                            if (combined.isEmpty()) {
                                PeopleLoadState.Empty
                            } else {
                                PeopleLoadState.Success(
                                    people = combined,
                                    canLoadMore = result.canLoadMore,
                                    loadingMore = false
                                )
                            }
                        }

                        else -> {
                            if (result.people.isEmpty()) {
                                PeopleLoadState.Empty
                            } else {
                                PeopleLoadState.Success(
                                    people = result.people,
                                    canLoadMore = result.canLoadMore,
                                    loadingMore = false
                                )
                            }
                        }
                    }
                }
                .onStart { emit(PeopleLoadState.Loading) }
                .catch { e ->
                    emit(PeopleLoadState.Error(e.message ?: "Ошибка загрузки"))
                }
        }

    val uiState: StateFlow<PeopleUiState> = combine(
        remotePeopleFlow,
        filterFlow,
        favouriteIdsFlow
    ) { loadState, filter, favouriteIds ->

        when (loadState) {
            is PeopleLoadState.Loading -> {
                PeopleUiState.Loading
            }

            is PeopleLoadState.Empty -> {
                PeopleUiState.Empty
            }

            is PeopleLoadState.Error -> {
                PeopleUiState.Error(loadState.message)
            }

            is PeopleLoadState.Success -> {
                val filteredList = loadState.people.filter { person ->
                    when (filter) {
                        PeopleFilter.ALL -> true
                        PeopleFilter.FAVOURITES -> person.id in favouriteIds
                    }
                }

                if (filteredList.isEmpty()) {
                    PeopleUiState.Empty
                } else {
                    PeopleUiState.Success(
                        list = filteredList,
                        canLoadMore = loadState.canLoadMore && filter == PeopleFilter.ALL,
                        loadingMore = loadState.loadingMore
                    )
                }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PeopleUiState.Loading
    )

    val state: PeopleUiState
        get() = uiState.value

    init {
        refresh()
    }

    fun load() {
        refresh()
    }

    fun retry() {
        refresh()
    }

    fun refresh() {
        refreshFlow.tryEmit(Unit)
    }

    fun onQueryChange(newQuery: String) {
        queryFlow.value = newQuery
    }

    fun onFilterChange(newFilter: PeopleFilter) {
        filterFlow.value = newFilter
    }

    fun toggleFav(person: Person) {
        viewModelScope.launch {
            val isFav = repository.isFavourite(person.id)

            if (isFav) {
                repository.removeFavourite(person)
            } else {
                repository.addFavourite(person)
            }
        }
    }

    fun loadMore() {
        val current = uiState.value as? PeopleUiState.Success ?: return
        if (!current.canLoadMore || current.loadingMore) return

        loadMoreFlow.tryEmit(Unit)
    }
}

private fun <T> Flow<T>.dropFirst(): Flow<T> = flow {
    var first = true
    collect { value ->
        if (first) {
            first = false
        } else {
            emit(value)
        }
    }
}