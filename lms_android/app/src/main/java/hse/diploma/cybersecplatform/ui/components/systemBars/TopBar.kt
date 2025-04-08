package hse.diploma.cybersecplatform.ui.components.systemBars

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val canNavigateBack = navController.previousBackStackEntry != null
    val currentRoute = navBackStackEntry?.destination?.route
    val currentScreen =
        when (currentRoute) {
            Screen.HomeScreen.route -> Screen.HomeScreen
            Screen.Favorites.route -> Screen.Favorites
            Screen.Statistics.route -> Screen.Statistics
            Screen.Profile.route -> Screen.Profile
            else -> null
        }

    CenterAlignedTopAppBar(
        title = {
            Text(
                text =
                    currentScreen?.titleId?.let { stringResource(it) } ?: "",
                fontFamily = Montserrat,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
            )
        },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_back),
                        contentDescription = "Back",
                    )
                }
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
