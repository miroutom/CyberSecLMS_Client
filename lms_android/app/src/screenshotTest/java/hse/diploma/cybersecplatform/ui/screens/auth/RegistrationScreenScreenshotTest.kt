package hse.diploma.cybersecplatform.ui.screens.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import hse.diploma.cybersecplatform.MainApplication
import hse.diploma.cybersecplatform.WithTheme

@Composable
@PreviewLightDark
(showBackground = true, device = "spec:parent=pixel_5")
fun PreviewRegistrationScreen() =
    WithTheme {
        RegistrationScreen(
            onNavigateToAuthorization = {},
            onRegistered = {},
            onError = {},
            viewModel = MainApplication.appComponent.viewModelFactory().create(RegistrationViewModel::class.java),
        )
    }
