package hse.diploma.cybersecplatform.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.ui.components.menu.ProfileMenu
import hse.diploma.cybersecplatform.ui.screens.error.ErrorScreen
import hse.diploma.cybersecplatform.ui.screens.loading.LoadingScreen
import hse.diploma.cybersecplatform.ui.state.shared.ProfileState
import hse.diploma.cybersecplatform.ui.theme.CyberSecPlatformTheme
import hse.diploma.cybersecplatform.ui.theme.Montserrat
import hse.diploma.cybersecplatform.ui.theme.Typography

@Composable
fun ProfileScreen(
    state: ProfileState,
    onLogoutClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onReload: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state) {
        is ProfileState.Loading -> {
            LoadingScreen()
        }
        is ProfileState.Success -> {
            val uiState = state.uiState
            ProfileContent(
                profile = uiState,
                modifier = modifier,
                onLogoutClick = onLogoutClick,
                onSettingsClick = onSettingsClick,
            )
        }
        is ProfileState.Error -> {
            val errorType = state.errorType
            ErrorScreen(errorType, onReload = onReload)
        }
    }
}

@Composable
private fun ProfileContent(
    profile: ProfileUiState,
    modifier: Modifier = Modifier,
    onLogoutClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ElevatedCard(
            modifier =
                modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            colors =
                CardDefaults.elevatedCardColors(
                    containerColor = colorResource(R.color.dialog_color),
                ),
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.welcome_text, profile.userData.fullName),
                            fontFamily = Montserrat,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = colorResource(R.color.button_enabled),
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.email_label, profile.userData.email),
                        fontFamily = Montserrat,
                        fontSize = 12.sp,
                        color = colorResource(R.color.main_text_color),
                    )
                }
                Spacer(Modifier.width(8.dp))
                Image(
                    painter = painterResource(R.drawable.profile_image),
                    contentDescription = "Illustration",
                    modifier = Modifier.size(width = 136.dp, height = 105.dp),
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_activity),
                contentDescription = "Statistics",
                tint = colorResource(R.color.main_text_color),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.profile_statistics_section),
                style = Typography.bodyMedium,
            )
        }

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(colorResource(R.color.profile_content_background)),
        )

        ProfileMenu(
            onTheoryClick = {},
            onSettingsClick = onSettingsClick,
            onLogoutClick = onLogoutClick,
        )
    }
}

@Composable
@PreviewLightDark
@Preview(name = "ProfileScreen", showSystemUi = true, showBackground = true)
private fun ProfileScreenPreview() {
    CyberSecPlatformTheme {
        ProfileScreen(
            state = ProfileState.Loading,
            onLogoutClick = {},
            onSettingsClick = {},
            onReload = {},
        )
    }
}
