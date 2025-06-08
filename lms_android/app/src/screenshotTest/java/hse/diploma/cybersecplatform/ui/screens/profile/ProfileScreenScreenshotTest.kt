package hse.diploma.cybersecplatform.ui.screens.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import hse.diploma.cybersecplatform.WithTheme
import hse.diploma.cybersecplatform.mock.mockStats
import hse.diploma.cybersecplatform.mock.mockUser
import hse.diploma.cybersecplatform.ui.state.shared.ProfileState

@Composable
@Preview(showBackground = true, apiLevel = 30)
fun PreviewProfileScreen() =
    WithTheme {
        val state =
            ProfileState.Success(
                ProfileUiState(mockUser, mockStats),
            )

        ProfileScreen(
            state = state,
            onLogoutClick = {},
            onSettingsClick = {},
            onReload = {},
        )
    }
