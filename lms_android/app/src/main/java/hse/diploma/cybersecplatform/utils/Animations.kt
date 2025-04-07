package hse.diploma.cybersecplatform.utils

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavBackStackEntry

fun AnimatedContentTransitionScope<NavBackStackEntry>.slideInWithFade(): EnterTransition {
    return slideInHorizontally(initialOffsetX = { it }) + fadeIn()
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.slideOutToLeft(): ExitTransition {
    return slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.slideInFromLeft(): EnterTransition {
    return slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.slideOutToRight(): ExitTransition {
    return slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
}
