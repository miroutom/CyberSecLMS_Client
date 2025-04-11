package hse.diploma.cybersecplatform.ui.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import hse.diploma.cybersecplatform.extensions.animatedComposable
import hse.diploma.cybersecplatform.ui.screens.auth.AuthorizationScreen
import hse.diploma.cybersecplatform.ui.screens.auth.RegistrationScreen
import hse.diploma.cybersecplatform.ui.screens.onboarding.OnBoardingScreen
import hse.diploma.cybersecplatform.utils.logD

private const val TAG = "AuthNavigationGraph"

fun NavGraphBuilder.authNavigationGraph(
    navController: NavHostController,
    onAuthCompleted: () -> Unit,
    modifier: Modifier = Modifier,
) {

    navigation(
        startDestination = Screen.Onboarding.route,
        route = "auth_flow",
    ) {
        animatedComposable(Screen.Onboarding.route) {
            OnBoardingScreen(
                onNavigateToAuthorization = {
                    navController.navigate(Screen.Authorization.route)
                },
                onNavigateToRegistration = {
                    navController.navigate(Screen.Registration.route)
                },
                modifier = modifier,
            )
        }

        animatedComposable(Screen.Authorization.route) {
            AuthorizationScreen(
                onNavigateToRegistration = {
                    navController.navigate(Screen.Registration.route)
                },
                onAuthorized = {
                    logD(TAG, "onAuthorized()")
                    onAuthCompleted()
                },
                modifier = modifier,
            )
        }

        animatedComposable(Screen.Registration.route) {
            RegistrationScreen(
                onNavigateToAuthorization = {
                    navController.navigate(Screen.Authorization.route)
                },
                onRegistered = {
                    logD(TAG, "onRegistered()")
                    onAuthCompleted()
                },
                modifier = modifier,
            )
        }
    }
}
