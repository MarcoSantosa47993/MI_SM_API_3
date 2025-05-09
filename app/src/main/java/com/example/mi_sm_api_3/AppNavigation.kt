package com.example.mi_sm_api_3


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mi_sm_api_3.screens.HomeScreen


@Composable
fun AppNavigation(modifier: Modifier = Modifier) {

    var navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home" ) {

        composable(route = "home") {
            HomeScreen()
        }



    }




}