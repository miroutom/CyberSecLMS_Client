package hse.diploma.cybersecplatform.ui.screens.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import hse.diploma.cybersecplatform.MainApplication
import hse.diploma.cybersecplatform.WithTheme
import hse.diploma.cybersecplatform.ui.screens.otp.OtpViewModel

@Composable
@PreviewLightDark
(showBackground = true, device = "spec:parent=pixel_5")
fun PreviewAuthorizationScreen() =
    WithTheme {
        AuthorizationScreen(
            onNavigateToRegistration = {},
            onAuthorized = {},
            onError = {},
            viewModel = MainApplication.appComponent.viewModelFactory().create(AuthorizationViewModel::class.java),
            otpViewModel = MainApplication.appComponent.viewModelFactory().create(OtpViewModel::class.java),
        )
    }
