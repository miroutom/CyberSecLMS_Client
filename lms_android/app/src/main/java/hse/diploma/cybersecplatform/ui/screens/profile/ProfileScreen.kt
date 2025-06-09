package hse.diploma.cybersecplatform.ui.screens.profile

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.data.model.analytics.UserStatistics
import hse.diploma.cybersecplatform.mock.mockStats
import hse.diploma.cybersecplatform.mock.mockUser
import hse.diploma.cybersecplatform.ui.components.menu.ProfileMenu
import hse.diploma.cybersecplatform.ui.screens.error.ErrorScreen
import hse.diploma.cybersecplatform.ui.screens.loading.LoadingScreen
import hse.diploma.cybersecplatform.ui.state.shared.ProfileState
import hse.diploma.cybersecplatform.ui.theme.CyberSecPlatformTheme
import hse.diploma.cybersecplatform.ui.theme.Montserrat
import hse.diploma.cybersecplatform.ui.theme.Typography
import hse.diploma.cybersecplatform.utils.formatIsoDateToReadable

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
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Spacer(modifier = Modifier.height(16.dp))

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
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
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
                fontWeight = FontWeight.Bold,
            )
        }

        StatisticsOverviewSection(stats = profile.stats)

        ProfileMenu(
            onTheoryClick = {},
            onSettingsClick = onSettingsClick,
            onLogoutClick = onLogoutClick,
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun StatisticsOverviewSection(stats: UserStatistics) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors =
            CardDefaults.elevatedCardColors(
                containerColor = colorResource(R.color.profile_content_background),
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.completed_courses),
                    value = "${stats.completedCourses}/${stats.totalCourses}",
                )

                StatCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.total_points),
                    value = "${stats.totalPoints}",
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.completed_tasks),
                    value = "${stats.completedTasks}/${stats.totalTasks}",
                )

                StatCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.average_score, stats.averageScore),
                    value = String.format("%.1f", stats.averageScore),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                InfoItem(
                    title = stringResource(R.string.joined_date),
                    value = stats.joinedDate,
                )

                InfoItem(
                    title = stringResource(R.string.last_activity_session),
                    value = formatIsoDateToReadable(stats.lastActive),
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors =
            CardDefaults.elevatedCardColors(
                containerColor = colorResource(R.color.xss_card_color),
            ),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                maxLines = 1,
                style = Typography.bodySmall,
                color = colorResource(R.color.main_text_color),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            Text(
                text = value,
                fontSize = 16.sp,
                style = Typography.bodyMedium,
                color = colorResource(R.color.main_text_color),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun InfoItem(
    title: String,
    value: String,
) {
    Column {
        Text(
            text = title,
            fontSize = 12.sp,
            maxLines = 1,
            style = Typography.bodySmall,
            color = colorResource(R.color.main_text_color),
            textAlign = TextAlign.Center,
        )

        Text(
            text = value,
            fontSize = 14.sp,
            style = Typography.bodyMedium,
            color = colorResource(R.color.main_text_color),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
@Preview(name = "ProfileScreen", showBackground = true, apiLevel = 30)
private fun ProfileScreenPreview() {
    CyberSecPlatformTheme {
        ProfileScreen(
            state = ProfileState.Success(ProfileUiState(mockUser, mockStats)),
            onLogoutClick = {},
            onSettingsClick = {},
            onReload = {},
        )
    }
}
