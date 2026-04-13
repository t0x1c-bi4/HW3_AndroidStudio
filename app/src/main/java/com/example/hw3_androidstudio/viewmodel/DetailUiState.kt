package com.example.hw3_androidstudio.viewmodel

import com.example.hw3_androidstudio.data.model.Person

sealed interface DetailUiState {
    object Loading : DetailUiState
    data class Success(val person: Person) : DetailUiState
    object Error : DetailUiState
}