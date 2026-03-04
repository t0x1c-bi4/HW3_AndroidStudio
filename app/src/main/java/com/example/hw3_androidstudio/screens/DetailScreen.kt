package com.example.hw3_androidstudio.screens

import com.example.hw3_androidstudio.data.model.Person
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable

@Composable
fun DetailScreen(
    person: Person,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(
            person.name,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(16.dp))

        Text("Рост: ${person.height}")
        Text("Вес: ${person.mass}")
        Text("Цвет волос: ${person.hairColor}")
        Text("Цвет кожи: ${person.skinColor}")
        Text("Глаза: ${person.eyeColor}")
        Text("Год рождения: ${person.birthYear}")
        Text("Пол: ${person.gender}")

        Spacer(Modifier.height(24.dp))

        Button(onClick = onBack) {
            Text("Назад")
        }
    }
}