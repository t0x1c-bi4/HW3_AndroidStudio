package com.example.hw3_androidstudio.screens

import com.example.hw3_androidstudio.data.model.Person
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.runtime.Composable
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material.icons.automirrored.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouritesScreen(
    people: List<Person>,
    onToggle: (Int) -> Unit,
    onOpen: (Int) -> Unit,
    onBack: () -> Unit
) {

    Column {

        TopAppBar(
            title = { Text("Избранное") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                }
            }
        )

        if (people.isEmpty()) {

            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Избранное пусто")
            }

        } else {

            LazyColumn {
                items(people) { person ->
                    PersonCard(
                        person = person,
                        isFav = true,
                        onToggle = { onToggle(person.id) },
                        onClick = { onOpen(person.id) }
                    )
                }
            }
        }
    }
}