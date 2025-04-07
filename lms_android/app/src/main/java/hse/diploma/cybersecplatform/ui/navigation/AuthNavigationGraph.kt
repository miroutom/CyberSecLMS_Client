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
import hse.diploma.cybersecplatform.ui.screens.auth.AuthorizationScreen
import hse.diploma.cybersecplatform.ui.screens.auth.RegistrationScreen
import hse.diploma.cybersecplatform.ui.screens.onboarding.OnBoardingScreen

@Composable
fun AuthNavigationGraph(
    navController: NavHostController,
    onAuthCompleted: () -> Unit,
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
        composable(Screen.Onboarding.route) {
            OnBoardingScreen(
                onNavigateToAuthorization = {
                    navController.navigate(Screen.Authorization.route)
                },
                onNavigateToRegistration = {
                    navController.navigate(Screen.Registration.route)
                },
            )
        }
        composable(Screen.Authorization.route) {
            AuthorizationScreen(
                onNavigateToRegistration = {
                    navController.navigate(Screen.Registration.route)
                },
                onAuthorized = { onAuthCompleted() },
            )
        }
        composable(Screen.Registration.route) {
            RegistrationScreen(
                onNavigateToAuthorization = {
                    navController.navigate(Screen.Authorization.route)
                },
                onRegistered = { onAuthCompleted() },
            )
        }
    }
}
