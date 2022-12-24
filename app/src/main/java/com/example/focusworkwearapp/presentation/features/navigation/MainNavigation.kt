package com.example.focusworkwearapp.presentation.features.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.focusworkwearapp.presentation.features.ui.screens.*
import com.example.focusworkwearapp.presentation.features.ui.viewmodel.MainViewModel
import com.example.focusworkwearapp.presentation.service.StopwatchService


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainNavigation(
    stopwatchService: StopwatchService
) {
    val navHostController = rememberNavController()
    val viewModel: MainViewModel = viewModel()

    NavHost(
        navController = navHostController, startDestination = Navigators.Home.route
    ) {
        composable(Navigators.Home.route) {
            HomeScreen(navHostController)
        }
        composable(Navigators.Task.route) {
            TaskScreen(viewModel, navHostController)
        }
        composable(Navigators.Report.route) {
            RegressionScreen(navHostController)
        }
        composable(Navigators.Info.route) {
            InfoScreen()
        }
        composable(Navigators.Timer.route) {
            TimerScreen(viewModel = viewModel,stopwatchService)
        }
    }
}

sealed class Navigators(val route: String) {
    object Task : Navigators("task")
    object Home : Navigators("home")
    object Report : Navigators("report")
    object Info : Navigators("info")
    object Timer : Navigators("timer")
}