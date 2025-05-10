package hse.diploma.cybersecplatform.ui.components.systemBars

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.ui.components.ProfileIcon
import hse.diploma.cybersecplatform.ui.navigation.Screen
import hse.diploma.cybersecplatform.ui.theme.CyberSecPlatformTheme
import hse.diploma.cybersecplatform.ui.theme.Montserrat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavHostController) {
    val noBackStackRoutes =
        setOf(
            Screen.HomeScreen.route,
            Screen.MyCourses.route,
            Screen.Profile.route,
        )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val canNavigateBack = navController.previousBackStackEntry != null && currentRoute !in noBackStackRoutes

    val currentScreen =
        when (currentRoute) {
            Screen.HomeScreen.route -> Screen.HomeScreen
            Screen.MyCourses.route -> Screen.MyCourses
            Screen.Profile.route -> Screen.Profile
            Screen.TaskScreen.route -> Screen.TaskScreen
            else -> null
        }

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = currentScreen?.titleId?.let { stringResource(it) } ?: "",
                fontFamily = Montserrat,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
            )
        },
        navigationIcon = {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.alpha(if (canNavigateBack) 1f else 0f),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_back),
                    contentDescription = "Back",
                )
            }
        },
        actions = {
            ProfileIcon(userProfileImageUrl = null)
        },
        colors =
            TopAppBarColors(
                containerColor = Color.White,
                titleContentColor = Color.Black,
                scrolledContainerColor = Color.Transparent,
                navigationIconContentColor = Color.Black,
                actionIconContentColor = Color.Transparent,
            ),
    )
}

@Preview(showBackground = true)
@Composable
fun TopBarPreview() {
    CyberSecPlatformTheme {
        TopBar(rememberNavController())
    }
}
