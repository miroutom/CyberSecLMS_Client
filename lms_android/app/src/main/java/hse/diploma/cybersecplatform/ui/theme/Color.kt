package hse.diploma.cybersecplatform.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val lightBlue = Color(0xFF3764ED)
val mediumBlue = Color(0xFF2C4FBC)
val darkBlue = Color(0xFF1F3987)
val mint = Color(0xFF3AE8C5)

val linearHorizontalGradient =
    Brush.horizontalGradient(
        colors = listOf(lightBlue, mediumBlue, darkBlue),
    )

val xssCardGradient =
    Brush.verticalGradient(
        colors = listOf(Color(0xFFF2F2FF), Color(0xFF3764ED)),
    )

val sqlCardGradient =
    Brush.verticalGradient(
        colors = listOf(Color(0xFFFEF3E9), Color(0xFFF9A866)),
    )

val csrfCardGradient =
    Brush.verticalGradient(
        colors = listOf(Color(0xFFF2F2FF), Color(0xFF16B593)),
    )

val xssTitleGradient =
    Brush.horizontalGradient(
        0.0f to Color(0xFF3764ED),
        0.5f to Color(0xFF2C4FBC),
        1.0f to Color(0xFF1F3987),
        startX = 0.0f,
        endX = 100.0f,
    )

val sqlTitleGradient =
    Brush.horizontalGradient(
        0.0f to Color(0xFFF88F3A),
        0.5f to Color(0xFFDE7620),
        1.0f to Color(0xFFC6691D),
        startX = 0.0f,
        endX = 100.0f,
    )

val csrfTitleGradient =
    Brush.horizontalGradient(
        0.0f to Color(0xFF04916E),
        0.5f to Color(0xFF046E4C),
        1.0f to Color(0xFF004A2E),
        startX = 0.0f,
        endX = 100.0f,
    )
