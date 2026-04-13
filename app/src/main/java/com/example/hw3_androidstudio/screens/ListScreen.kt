package com.example.hw3_androidstudio.screens

import com.example.hw3_androidstudio.viewmodel.PeopleUiState
import com.example.hw3_androidstudio.data.model.Person
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon

@Composable
fun ListScreen(
    state: PeopleUiState,
    favourites: List<Person>,
    onSearch: (String) -> Unit,
    onRetry: () -> Unit,
    onToggle: (Person) -> Unit,
    onOpen: (Int) -> Unit,
    onOpenFavourites: () -> Unit,
    query: String,
    onQueryChange: (String) -> Unit,
    onLoadMore: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                label = { Text("Поиск") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.width(8.dp))

            IconButton(onClick = onOpenFavourites) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Избранное"
                )
            }
        }

        when (state) {

            // Loading
            PeopleUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(8.dp))
                        Text("Загрузка")
                    }
                }
            }

            // Empty
            PeopleUiState.Empty -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Пусто")
                }
            }

            // Error
            is PeopleUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.msg)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = onRetry) {
                            Text("Повторить")
                        }
                    }
                }
            }

            // Success
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