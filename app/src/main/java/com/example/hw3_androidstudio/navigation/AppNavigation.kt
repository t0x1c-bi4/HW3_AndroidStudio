package com.example.hw3_androidstudio.navigation

import com.example.hw3_androidstudio.viewmodel.PeopleViewModel
import com.example.hw3_androidstudio.screens.*
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost

@Composable
fun AppNavigation() {

    val nav = rememberNavController()
    val vm: PeopleViewModel = hiltViewModel()

    NavHost(nav, startDestination = "list") {

        composable("list") {
            ListScreen(
                state = vm.state,
                favourites = vm.favourites,
                query = vm.query,
                onQueryChange = vm::onQueryChange,
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

            DetailScreen(
                id = id,
                onBack = { nav.popBackStack() }
            )
        }

        composable("favourites") {

            val list = vm.getFavouritesList()

            FavouritesScreen(
                people = list,
                onToggle = vm::toggleFav,
                onOpen = { nav.navigate("detail/$it") },
                onBack = { nav.popBackStack() }
            )
        }
    }
}