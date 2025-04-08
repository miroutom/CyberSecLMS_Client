package hse.diploma.cybersecplatform.ui.components.systemBars

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
    val items = listOf(Screen.HomeScreen, Screen.Favorites, Screen.Statistics, Screen.Profile)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier =
            modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = RectangleShape,
                    clip = false,
                    spotColor = Color.Black.copy(alpha = 0.7f),
                ),
        containerColor = Color.White,
    ) {
        items.forEach { screen ->
            val selected = currentRoute == screen.route
            val backgroundColor = if (selected) Color(0xFF060051) else Color(0xFFE1E1E3)

            NavigationBarItem(
                icon = {
                    Box(
                        modifier =
                            Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(backgroundColor),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter =
                                when (screen) {
                                    Screen.HomeScreen -> painterResource(R.drawable.ic_home)
                                    Screen.Favorites -> painterResource(R.drawable.ic_favorite)
                                    Screen.Statistics -> painterResource(R.drawable.ic_activity)
                                    else -> painterResource(R.drawable.ic_account)
                                },
                            contentDescription = screen.titleId?.let { stringResource(it) },
                        )
                    }
                },
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(Screen.HomeScreen.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors =
                    NavigationBarItemColors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.Black,
                        selectedIndicatorColor = Color.Transparent,
                        unselectedIconColor = Color(0xFF060051),
                        unselectedTextColor = Color.Black,
                        disabledIconColor = Color.Gray,
                        disabledTextColor = Color.Gray,
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
