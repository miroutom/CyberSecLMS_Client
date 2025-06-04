package hse.diploma.cybersecplatform.ui.screens.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import hse.diploma.cybersecplatform.di.vm.LocalAuthStateViewModel
import hse.diploma.cybersecplatform.di.vm.LocalViewModelFactory
import hse.diploma.cybersecplatform.navigation.Screen

@Composable
fun ProfileScreenWrapper(
    profileViewModel: ProfileViewModel = viewModel(factory = LocalViewModelFactory.current),
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val authStateViewModel = LocalAuthStateViewModel.current
    val profileState by profileViewModel.profileState.collectAsState()

    ProfileScreen(
        state = profileState,
        onLogoutClick = { authStateViewModel.logout() },
        onSettingsClick = { navHostController.navigate(Screen.Settings.route) },
        onReload = { profileViewModel.loadProfile() },
        modifier = modifier,
    )
}
