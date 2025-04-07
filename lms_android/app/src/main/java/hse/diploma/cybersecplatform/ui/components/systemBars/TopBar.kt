package hse.diploma.cybersecplatform.ui.components.systemBars

import androidx.compose.foundation.background
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.ui.components.ProfileIcon
import hse.diploma.cybersecplatform.ui.navigation.Screen
import hse.diploma.cybersecplatform.ui.theme.CyberSecPlatformTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val canNavigateBack = navController.previousBackStackEntry != null
    val currentRoute = navBackStackEntry?.destination?.route
    val currentScreen = when (currentRoute) {
        Screen.HomeScreen.route -> Screen.HomeScreen
        Screen.Favorites.route -> Screen.Favorites
        Screen.Statistics.route -> Screen.Statistics
        Screen.Profile.route -> Screen.Profile
        else -> null
    }

    CenterAlignedTopAppBar(
        title = { Text(currentScreen?.titleId?.let { stringResource(it) } ?: "" ) },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_back),
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            ProfileIcon(userProfileImageUrl = null)
        }
    )
}

@Preview(showBackground = true)
@Composable
fun TopBarPreview() {
    CyberSecPlatformTheme {
        TopBar(rememberNavController())
    }
}