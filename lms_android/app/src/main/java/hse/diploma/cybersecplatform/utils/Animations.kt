package hse.diploma.cybersecplatform.utils

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

fun slideInWithFade(): EnterTransition {
    return slideInHorizontally(initialOffsetX = { it }) + fadeIn()
}

fun slideOutToLeft(): ExitTransition {
    return slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
}

fun slideInFromLeft(): EnterTransition {
    return slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
}

fun slideOutToRight(): ExitTransition {
    return slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
}
