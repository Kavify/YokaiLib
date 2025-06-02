package ru.feryafox.yokailib.root.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.feryafox.yokailib.root.ui.screens.SettingsScreen
import ru.feryafox.yokailib.root.ui.screens.MainListScreen

// TODO добавить возможность расширять навигацию
@Composable
fun AppNavGraph(startDestination: String, navController: NavHostController) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable(Routes.MAIN.path) {
            MainListScreen(navController = navController)
        }

        composable(Routes.SETTING.path) {
            SettingsScreen(navController = navController)
        }
    }
}
