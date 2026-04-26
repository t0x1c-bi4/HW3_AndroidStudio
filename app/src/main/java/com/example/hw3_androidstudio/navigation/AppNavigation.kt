package com.example.hw3_androidstudio.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hw3_androidstudio.screens.DetailScreen
import com.example.hw3_androidstudio.screens.ListScreen
import com.example.hw3_androidstudio.viewmodel.PeopleViewModel

@Composable
fun AppNavigation() {

    val nav = rememberNavController()
    val vm: PeopleViewModel = hiltViewModel()

    val state by vm.uiState.collectAsState()
    val favourites by vm.favourites.collectAsState()
    val query by vm.query.collectAsState()
    val filter by vm.filter.collectAsState()

    NavHost(navController = nav, startDestination = "list") {

        composable("list") {
            ListScreen(
                state = state,
                favourites = favourites,
                onRetry = vm::retry,
                onToggle = vm::toggleFav,
                onOpen = { nav.navigate("detail/$it") },
                query = query,
                onQueryChange = vm::onQueryChange,
                selectedFilter = filter,
                onFilterChange = vm::onFilterChange,
                onLoadMore = vm::loadMore
            )
        }

        composable("detail/{id}") { back ->
            val id = back.arguments!!.getString("id")!!.toInt()

            DetailScreen(
                id = id,
                onBack = { nav.popBackStack() }
            )
        }
    }
}