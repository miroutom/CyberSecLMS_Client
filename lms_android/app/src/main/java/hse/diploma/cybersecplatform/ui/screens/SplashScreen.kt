package hse.diploma.cybersecplatform.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.ui.theme.CyberSecPlatformTheme
import hse.diploma.cybersecplatform.ui.theme.Typography
import hse.diploma.cybersecplatform.ui.theme.linearHorizontalGradient
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    isAuthorized: Boolean,
    onSplashCompleted: (Boolean) -> Unit,
) {
    LaunchedEffect(Unit) {
        delay(1500)
        onSplashCompleted(isAuthorized)
    }

    Box(
        modifier = Modifier.fillMaxSize().background(color = colorResource(R.color.xss_card_color)),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = R.mipmap.app_icon),
                contentDescription = null,
                modifier = Modifier.size(96.dp),
                tint = Color.Unspecified,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.app_name),
                style =
                    Typography.titleLarge.copy(
                        brush = linearHorizontalGradient,
                    ),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashScreenPreview() {
    CyberSecPlatformTheme {
        SplashScreen(isAuthorized = false, {})
    }
}
