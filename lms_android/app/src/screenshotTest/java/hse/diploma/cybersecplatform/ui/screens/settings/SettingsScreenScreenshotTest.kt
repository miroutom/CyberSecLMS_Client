package hse.diploma.cybersecplatform.ui.screens.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import hse.diploma.cybersecplatform.WithTheme
import hse.diploma.cybersecplatform.domain.model.AppTheme
import hse.diploma.cybersecplatform.domain.model.Language
import hse.diploma.cybersecplatform.mock.mockUser
import hse.diploma.cybersecplatform.ui.state.screen_state.SettingsScreenState

@Composable
@Preview(showBackground = true, apiLevel = 30)
fun PreviewSettingsScreen() =
    WithTheme {
        val state =
            SettingsScreenState(
                theme = AppTheme.SYSTEM,
                language = Language.ENGLISH,
                user = mockUser,
            )

        SettingsScreen(
            state = state,
            onThemeSelected = {},
            onLanguageSelected = {},
            onPasswordChangeInitiated = { _, _, _ -> },
            onDeleteAccountInitiated = { _, _ -> },
            onDeleteOtpSubmitted = { _, _ -> },
            onDeleteOtpDismissed = {},
            onErrorDismissed = {},
            onLogout = {},
        )
    }
