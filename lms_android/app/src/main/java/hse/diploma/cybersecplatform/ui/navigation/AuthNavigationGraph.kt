package hse.diploma.cybersecplatform.ui.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import hse.diploma.cybersecplatform.data.api.AppPreferencesManager
import hse.diploma.cybersecplatform.extensions.animatedComposable
import hse.diploma.cybersecplatform.ui.components.dialogs.ErrorDialog
import hse.diploma.cybersecplatform.ui.screens.auth.AuthorizationScreen
import hse.diploma.cybersecplatform.ui.screens.auth.RegistrationScreen
import hse.diploma.cybersecplatform.ui.screens.onboarding.OnBoardingScreen
import hse.diploma.cybersecplatform.utils.logD

private const val TAG = "AuthNavigationGraph"

fun NavGraphBuilder.authNavigationGraph(
    navController: NavHostController,
    onAuthCompleted: () -> Unit,
    appPreferencesManager: AppPreferencesManager,
    modifier: Modifier = Modifier,
) {
    val startDestination =
        if (appPreferencesManager.isFirstLaunch()) {
            Screen.Onboarding.route
        } else {
            Screen.Authorization.route
        }

    navigation(
        startDestination = startDestination,
        route = "auth_flow",
    ) {
        animatedComposable(Screen.Onboarding.route) {
            OnBoardingScreen(
                onNavigateToAuthorization = {
                    appPreferencesManager.markAppLaunched()
                    navController.navigate(Screen.Authorization.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                },
                onNavigateToRegistration = {
                    appPreferencesManager.markAppLaunched()
                    navController.navigate(Screen.Registration.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                },
                modifier = modifier,
            )
        }

        animatedComposable(Screen.Authorization.route) {
            var errorMessage by remember { mutableStateOf<String?>(null) }

            AuthorizationScreen(
                onNavigateToRegistration = {
                    navController.navigate(Screen.Registration.route)
                },
                onAuthorized = {
                    logD(TAG, "onAuthorized")
                    onAuthCompleted()
                },
                onError = { error -> errorMessage = error },
                modifier = modifier,
            )

            errorMessage?.let { error ->
                ErrorDialog(
                    errorMessage = error,
                    onDismiss = { errorMessage = null },
                )
            }
        }

        animatedComposable(Screen.Registration.route) {
            var errorMessage by remember { mutableStateOf<String?>(null) }

            RegistrationScreen(
                onNavigateToAuthorization = {
                    navController.navigate(Screen.Authorization.route)
                },
                onRegistered = {
                    logD(TAG, "onRegistered")
                    onAuthCompleted()
                },
                onError = { error -> errorMessage = error },
                modifier = modifier,
            )

            errorMessage?.let { error ->
                ErrorDialog(
                    errorMessage = error,
                    onDismiss = { errorMessage = null },
                )
            }
        }
    }
}
