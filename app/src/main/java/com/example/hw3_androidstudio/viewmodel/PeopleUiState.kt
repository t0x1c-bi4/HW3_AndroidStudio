package com.example.hw3_androidstudio.viewmodel

import com.example.hw3_androidstudio.data.model.Person
sealed interface PeopleUiState {
    object Loading : PeopleUiState
    object Empty : PeopleUiState
    data class Error(val msg: String) : PeopleUiState
    data class Success(
        val list: List<Person>,
        val canLoadMore: Boolean,
        val loadingMore: Boolean = false
    ) : PeopleUiState
}