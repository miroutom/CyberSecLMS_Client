package hse.diploma.cybersecplatform.ui.components.systemBars

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.di.vm.LocalViewModelFactory
import hse.diploma.cybersecplatform.extensions.addTimestamp
import hse.diploma.cybersecplatform.extensions.toAbsoluteUrl
import hse.diploma.cybersecplatform.navigation.Screen
import hse.diploma.cybersecplatform.ui.components.EditProfile
import hse.diploma.cybersecplatform.ui.screens.profile.ProfileViewModel
import hse.diploma.cybersecplatform.ui.state.shared.ProfileState
import hse.diploma.cybersecplatform.ui.theme.CyberSecPlatformTheme
import hse.diploma.cybersecplatform.ui.theme.Montserrat
import hse.diploma.cybersecplatform.utils.logD

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    profileViewModel: ProfileViewModel,
    navController: NavHostController,
) {
    val profileState by profileViewModel.profileState.collectAsState()

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
            Screen.Settings.route -> Screen.Settings
            else -> null
        }

    val successState = profileState as? ProfileState.Success

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
            logD("EditProfile", "userData: ${successState?.uiState?.userData}")
            EditProfile(
                profileViewModel = profileViewModel,
                userProfileImageUrl =
                    successState?.uiState?.userData?.profileImage?.toAbsoluteUrl()?.addTimestamp()
                        ?: "https://placehold.co/256x256.png?text=Avatar",
            )
        },
        colors =
            TopAppBarColors(
                containerColor = colorResource(R.color.background),
                titleContentColor = colorResource(R.color.main_text_color),
                scrolledContainerColor = Color.Transparent,
                navigationIconContentColor = colorResource(R.color.main_text_color),
                actionIconContentColor = colorResource(R.color.main_text_color),
            ),
    )
}

@PreviewLightDark
@Composable
fun TopBarPreview() {
    CyberSecPlatformTheme {
        TopBar(
            profileViewModel = viewModel(factory = LocalViewModelFactory.current),
            navController = rememberNavController(),
        )
    }
}
