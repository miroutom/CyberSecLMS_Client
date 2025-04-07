package hse.diploma.cybersecplatform.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import hse.diploma.cybersecplatform.ui.screens.favorites.FavoritesScreen
import hse.diploma.cybersecplatform.ui.screens.home.HomeScreen
import hse.diploma.cybersecplatform.ui.screens.profile.ProfileScreen
import hse.diploma.cybersecplatform.ui.screens.statistics.StatisticsScreen

@Composable
fun MainNavigationGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Onboarding.route,
        modifier = modifier,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) + fadeIn() },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() },
    ) {
        composable(Screen.HomeScreen.route) { HomeScreen(navController) }
        composable(Screen.Favorites.route) { FavoritesScreen(navController) }
        composable(Screen.Statistics.route) { StatisticsScreen(navController) }
        composable(Screen.Profile.route) { ProfileScreen(navController) }
    }
}
