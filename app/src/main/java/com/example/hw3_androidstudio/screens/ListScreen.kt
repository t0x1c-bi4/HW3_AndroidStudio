package com.example.hw3_androidstudio.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hw3_androidstudio.data.model.Person
import com.example.hw3_androidstudio.viewmodel.PeopleFilter
import com.example.hw3_androidstudio.viewmodel.PeopleUiState

@Composable
fun ListScreen(
    state: PeopleUiState,
    favourites: List<Person>,
    onRetry: () -> Unit,
    onToggle: (Person) -> Unit,
    onOpen: (Int) -> Unit,
    query: String,
    onQueryChange: (String) -> Unit,
    selectedFilter: PeopleFilter,
    onFilterChange: (PeopleFilter) -> Unit,
    onLoadMore: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {

        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            label = { Text("Поиск") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedFilter == PeopleFilter.ALL,
                onClick = { onFilterChange(PeopleFilter.ALL) },
                label = { Text("Все") }
            )

            FilterChip(
                selected = selectedFilter == PeopleFilter.FAVOURITES,
                onClick = { onFilterChange(PeopleFilter.FAVOURITES) },
                label = { Text("Избранные") }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        when (state) {
            PeopleUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Загрузка")
                    }
                }
            }

            PeopleUiState.Empty -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Пусто")
                }
            }

            is PeopleUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.msg,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onRetry) {
                            Text("Повторить")
                        }
                    }
                }
            }

            is PeopleUiState.Success -> {
                LazyColumn {
                    items(state.list) { person ->
                        PersonCard(
                            person = person,
                            isFav = favourites.any { it.id == person.id },
                            onToggle = { onToggle(person) },
                            onClick = { onOpen(person.id) }
                        )
                    }

                    if (state.canLoadMore) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (state.loadingMore) {
                                    CircularProgressIndicator()
                                } else {
                                    Button(onClick = onLoadMore) {
                                        Text("Загрузить ещё")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}