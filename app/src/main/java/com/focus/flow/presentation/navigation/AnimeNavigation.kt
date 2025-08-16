package com.focus.flow.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.focus.flow.presentation.component.ExoPlayerManager
import com.focus.flow.presentation.screen.AnimeDetailScreen
import com.focus.flow.presentation.screen.AnimeListScreen

@Composable
fun AnimeNavigation(
    navController: NavHostController,
    exoPlayerManager: ExoPlayerManager
) {
    NavHost(
        navController = navController,
        startDestination = Screen.AnimeList.route
    ) {
        composable(route = Screen.AnimeList.route) {
            AnimeListScreen(
                onAnimeClick = { animeId ->
                    navController.navigate(Screen.AnimeDetail.createRoute(animeId))
                }
            )
        }
        
        composable(
            route = Screen.AnimeDetail.route,
            arguments = listOf(
                navArgument("animeId") {
                    type = NavType.IntType
                }
            )
        ) {
            AnimeDetailScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                exoPlayerManager = exoPlayerManager
            )
        }
    }
}

sealed class Screen(val route: String) {
    object AnimeList : Screen("anime_list")
    object AnimeDetail : Screen("anime_detail/{animeId}") {
        fun createRoute(animeId: Int) = "anime_detail/$animeId"
    }
}
