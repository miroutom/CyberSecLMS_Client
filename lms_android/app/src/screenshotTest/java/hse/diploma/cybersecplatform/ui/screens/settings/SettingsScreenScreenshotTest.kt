package hse.diploma.cybersecplatform.ui.screens.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import hse.diploma.cybersecplatform.WithTheme

@Composable
@PreviewLightDark
(showBackground = true, device = "spec:parent=pixel_5")
fun PreviewSettingsScreen() =
    WithTheme {
        SettingsScreen()
    }
