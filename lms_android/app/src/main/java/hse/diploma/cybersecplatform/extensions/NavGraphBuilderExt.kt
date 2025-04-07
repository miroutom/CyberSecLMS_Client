package hse.diploma.cybersecplatform.extensions

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import hse.diploma.cybersecplatform.utils.slideInFromLeft
import hse.diploma.cybersecplatform.utils.slideInWithFade
import hse.diploma.cybersecplatform.utils.slideOutToLeft
import hse.diploma.cybersecplatform.utils.slideOutToRight

fun NavGraphBuilder.animatedComposable(
    route: String,
    content: @Composable (NavBackStackEntry) -> Unit,
) {
    composable(
        route = route,
        enterTransition = { slideInWithFade() },
        exitTransition = { slideOutToLeft() },
        popEnterTransition = { slideInFromLeft() },
        popExitTransition = { slideOutToRight() },
    ) {
        content(it)
    }
}
