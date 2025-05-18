package hse.diploma.cybersecplatform.ui.components.systemBars

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.di.vm.LocalViewModelFactory
import hse.diploma.cybersecplatform.ui.components.EditProfile
import hse.diploma.cybersecplatform.ui.components.dialogs.EditProfileDialog
import hse.diploma.cybersecplatform.ui.components.dialogs.PhotoPickerDialog
import hse.diploma.cybersecplatform.ui.navigation.Screen
import hse.diploma.cybersecplatform.ui.screens.profile.ProfileUiState
import hse.diploma.cybersecplatform.ui.screens.profile.ProfileViewModel
import hse.diploma.cybersecplatform.ui.state.ProfileState
import hse.diploma.cybersecplatform.ui.theme.CyberSecPlatformTheme
import hse.diploma.cybersecplatform.ui.theme.Montserrat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavHostController) {
    val profileViewModel: ProfileViewModel = viewModel(factory = LocalViewModelFactory.current)
    val context = LocalContext.current

    var showEditProfile by remember { mutableStateOf(false) }
    var showPhotoPicker by remember { mutableStateOf(false) }

    val profileState by profileViewModel.profileState.collectAsState()

    var editProfileUiState by remember { mutableStateOf<ProfileUiState?>(null) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

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
    val errorState = profileState as? ProfileState.Error
    val isLoading = profileState is ProfileState.Loading

    LaunchedEffect(showEditProfile, successState) {
        if (showEditProfile && successState != null) {
            editProfileUiState = successState.uiState
        }
    }

    LaunchedEffect(errorState) {
        errorMsg = errorState?.errorType?.toString()
    }

    LaunchedEffect(profileState) {
        if (showEditProfile && profileState is ProfileState.Success && !isLoading) {
            showEditProfile = false
        }
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
            EditProfile(
                userProfileImageUrl =
                    successState?.uiState?.avatarUrl
                        ?: "https://placehold.co/256x256.png?text=Avatar",
                onEditProfileClick = { showEditProfile = true },
                onProfilePhotoClick = { showPhotoPicker = true },
            )
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

    if (showEditProfile && editProfileUiState != null) {
        EditProfileDialog(
            uiState = editProfileUiState!!,
            onDismiss = {
                showEditProfile = false
                errorMsg = null
            },
            onSave = { username, fullname, email ->
                profileViewModel.updateProfile(username, fullname, email)
            },
            isSaving = isLoading,
            errorMessage = errorMsg,
        )
    }

    if (showPhotoPicker) {
        PhotoPickerDialog(
            onPhotoPicked = { uri ->
                profileViewModel.uploadPhoto(uri, context.contentResolver)
                showPhotoPicker = false
            },
            onDismiss = { showPhotoPicker = false },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TopBarPreview() {
    CyberSecPlatformTheme {
        TopBar(rememberNavController())
    }
}
