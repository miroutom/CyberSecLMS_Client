package hse.diploma.cybersecplatform

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import hse.diploma.cybersecplatform.ui.theme.CyberSecPlatformTheme

@Composable
fun WithTheme(content: @Composable () -> Unit) {
    CyberSecPlatformTheme {
        Surface {
            content()
        }
    }
}
