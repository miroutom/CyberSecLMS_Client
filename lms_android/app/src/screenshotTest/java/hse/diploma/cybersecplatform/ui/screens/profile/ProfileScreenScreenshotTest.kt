package hse.diploma.cybersecplatform.ui.screens.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.navigation.compose.rememberNavController
import hse.diploma.cybersecplatform.MainApplication
import hse.diploma.cybersecplatform.WithTheme
import hse.diploma.cybersecplatform.data.model.UserData
import hse.diploma.cybersecplatform.mock.mockUser
import hse.diploma.cybersecplatform.ui.state.shared.ProfileState

@Composable
@PreviewLightDark
@Preview(showBackground = true, device = "spec:parent=pixel_5")
fun PreviewProfileScreen() = WithTheme {
    val state = ProfileState.Success(
        ProfileUiState(mockUser)
    )

    ProfileScreen(
        state = state,
        onLogoutClick = {},
        onSettingsClick = {},
        onReload = {}
    )
}
