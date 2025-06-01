package hse.diploma.cybersecplatform.ui.screens.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.navigation.compose.rememberNavController
import hse.diploma.cybersecplatform.MainApplication
import hse.diploma.cybersecplatform.WithTheme

@Composable
@PreviewLightDark
(showBackground = true, device = "spec:parent=pixel_5")
fun PreviewProfileScreen() =
    WithTheme {
        ProfileScreen(
            profileViewModel = MainApplication.appComponent.viewModelFactory().create(ProfileViewModel::class.java),
            navHostController = rememberNavController(),
        )
    }
