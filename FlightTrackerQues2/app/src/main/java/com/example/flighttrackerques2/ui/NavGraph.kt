package com.example.flighttrackerques2.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph(navController: NavHostController, isDarkTheme: Boolean, onToggleTheme: () -> Unit) {
    NavHost(navController, startDestination = "search") {
        composable("search") {
            FlightSearchScreen(navController, isDarkTheme, onToggleTheme)
        }
        composable("trackedFlights") {
            TrackedFlightsScreen(navController, isDarkTheme, onToggleTheme)
        }
    }
}
