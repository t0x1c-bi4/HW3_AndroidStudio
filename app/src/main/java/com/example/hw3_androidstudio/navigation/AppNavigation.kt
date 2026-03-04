package com.example.hw3_androidstudio.navigation

import com.example.hw3_androidstudio.viewmodel.PeopleViewModel
import com.example.hw3_androidstudio.viewmodel.PeopleUiState
import com.example.hw3_androidstudio.screens.*
import androidx.compose.runtime.Composable
import androidx.navigation.compose.composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost

@Composable
fun AppNavigation() {

    val nav = rememberNavController()
    val vm: PeopleViewModel = viewModel()

    NavHost(nav, startDestination = "list") {

        composable("list") {
            ListScreen(
                state = vm.state,
                favourites = vm.favourites,
                onSearch = vm::search,
                onRetry = vm::load,
                onToggle = vm::toggleFav,
                onOpen = { nav.navigate("detail/$it") },
                onOpenFavourites = { nav.navigate("favourites") },
                onLoadMore = vm::loadMore
            )
        }

        composable("detail/{id}") { back ->
            val id = back.arguments!!.getString("id")!!.toInt()

            val person =
                (vm.state as? PeopleUiState.Success)
                    ?.list
                    ?.first { it.id == id }

            person?.let {
                DetailScreen(it) { nav.popBackStack() }
            }
        }

        composable("favourites") {

            val list = vm.getFavouritePeople()

            FavouritesScreen(
                people = list,
                onToggle = vm::toggleFav,
                onOpen = { nav.navigate("detail/$it") },
                onBack = { nav.popBackStack() }
            )
        }
    }
}