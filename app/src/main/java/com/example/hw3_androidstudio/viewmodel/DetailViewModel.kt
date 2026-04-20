package com.example.hw3_androidstudio.viewmodel

import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hw3_androidstudio.data.repository.PeopleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: PeopleRepository
) : ViewModel() {

    var state by mutableStateOf<DetailUiState>(DetailUiState.Loading)
        private set

    fun load(id: Int) {
        viewModelScope.launch {

            state = DetailUiState.Loading

            try {
                val person = repository.getById(id)
                state = DetailUiState.Success(person)

            } catch (e: Exception) {
                state = DetailUiState.Error
            }
        }
    }
}