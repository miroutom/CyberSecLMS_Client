package hse.diploma.cybersecplatform.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import hse.diploma.cybersecplatform.extensions.animatedComposable
import hse.diploma.cybersecplatform.ui.screens.favorites.FavoritesScreen
import hse.diploma.cybersecplatform.ui.screens.home.HomeScreen
import hse.diploma.cybersecplatform.ui.screens.profile.ProfileScreen
import hse.diploma.cybersecplatform.ui.screens.statistics.StatisticsScreen

fun NavGraphBuilder.mainNavigationGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.HomeScreen.route,
        route = "main_flow",
    ) {
        animatedComposable(Screen.HomeScreen.route) {
            HomeScreen(navController)
        }

        animatedComposable(Screen.Favorites.route) {
            FavoritesScreen(navController)
        }

        animatedComposable(Screen.Statistics.route) {
            StatisticsScreen(navController)
        }

        animatedComposable(Screen.Profile.route) {
            ProfileScreen(navController)
        }
    }
}
