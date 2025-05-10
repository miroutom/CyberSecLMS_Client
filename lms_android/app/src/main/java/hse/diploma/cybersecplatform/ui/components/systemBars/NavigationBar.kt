package hse.diploma.cybersecplatform.ui.components.systemBars

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.ui.navigation.Screen
import hse.diploma.cybersecplatform.ui.theme.CyberSecPlatformTheme

@Composable
fun CustomNavigationBar(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val items = listOf(Screen.HomeScreen, Screen.MyCourses, Screen.Profile)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier =
            modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = RectangleShape,
                    spotColor = Color.Black.copy(alpha = 0.7f),
                ),
        containerColor = Color.White,
    ) {
        items.forEach { screen ->
            val selected = currentRoute == screen.route
            val backgroundColor = if (selected) Color(0xFF060051) else Color(0xFFE1E1E3)

            NavigationBarItem(
                icon = {
                    val interactionSource = remember { MutableInteractionSource() }

                    Box(
                        modifier =
                            Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(backgroundColor)
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication =
                                        ripple(
                                            bounded = true,
                                            color = Color.White,
                                        ),
                                    onClick = {
                                        if (currentRoute != screen.route) {
                                            navController.navigate(screen.route) {
                                                popUpTo(screen.route) { inclusive = false }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        } else {
                                            navController.popBackStack(screen.route, inclusive = false)
                                        }
                                    },
                                ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter =
                                when (screen) {
                                    Screen.HomeScreen -> painterResource(R.drawable.ic_home)
                                    Screen.MyCourses -> painterResource(R.drawable.ic_favorite)
                                    else -> painterResource(R.drawable.ic_account)
                                },
                            contentDescription = screen.titleId?.let { stringResource(it) },
                            tint = if (selected) Color.White else Color(0xFF060051),
                        )
                    }
                },
                selected = selected,
                onClick = {},
                colors =
                    NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                        selectedIconColor = Color.Unspecified,
                        unselectedIconColor = Color.Unspecified,
                    ),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomNavigationBarPreview() {
    CyberSecPlatformTheme {
        CustomNavigationBar(rememberNavController())
    }
}
