package com.example.mi_sm_api_3


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mi_sm_api_3.screens.HomeScreen
import com.example.mi_sm_api_3.screens.MatchInfoScreen

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable(route = "home") {
            HomeScreen(navController = navController)
        }
        composable(
            route = "matchInfo/{matchId}",
            arguments = listOf(navArgument("matchId") { type = NavType.LongType })
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getLong("matchId") ?: 0L
            MatchInfoScreen(navController,matchId = matchId)
        }
    }
}