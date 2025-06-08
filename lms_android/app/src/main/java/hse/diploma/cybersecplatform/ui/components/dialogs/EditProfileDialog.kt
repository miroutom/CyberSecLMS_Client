package hse.diploma.cybersecplatform.ui.components.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.data.model.analytics.UserStatistics
import hse.diploma.cybersecplatform.data.model.user.CourseProgress
import hse.diploma.cybersecplatform.data.model.user.UserData
import hse.diploma.cybersecplatform.ui.screens.profile.ProfileUiState
import hse.diploma.cybersecplatform.ui.theme.CyberSecPlatformTheme
import hse.diploma.cybersecplatform.ui.theme.Montserrat
import hse.diploma.cybersecplatform.ui.theme.Typography
import hse.diploma.cybersecplatform.utils.logD

@Composable
fun EditProfileDialog(
    uiState: ProfileUiState,
    onDismiss: () -> Unit,
    onSave: (username: String, fullName: String, email: String) -> Unit,
    errorMessage: String? = null,
) {
    logD("EditProfileDialog", "userData: ${uiState.userData}")
    var username by remember { mutableStateOf(uiState.userData.username) }
    var fullName by remember { mutableStateOf(uiState.userData.fullName) }
    var email by remember { mutableStateOf(uiState.userData.email) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.update_profile_setting),
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                fontFamily = Montserrat,
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text(stringResource(R.string.auth_label_username)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text(stringResource(R.string.auth_label_full_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(stringResource(R.string.auth_label_email)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                if (!errorMessage.isNullOrBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        errorMessage,
                        color = colorResource(R.color.error_dialog_text),
                        style = Typography.bodyMedium,
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(username, fullName, email) },
                enabled = username.isNotEmpty() || fullName.isNotEmpty() || email.isNotEmpty(),
                colors =
                    ButtonDefaults.filledTonalButtonColors(
                        containerColor = colorResource(R.color.button_enabled),
                        contentColor = colorResource(R.color.background),
                    ),
            ) {
                Text(
                    text = stringResource(R.string.save_button),
                    fontStyle = FontStyle.Italic,
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Normal,
                )
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors =
                    ButtonDefaults.filledTonalButtonColors(
                        containerColor = colorResource(R.color.button_enabled),
                        contentColor = colorResource(R.color.background),
                    ),
            ) {
                Text(
                    text = stringResource(R.string.cancel_button),
                    fontStyle = FontStyle.Italic,
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Normal,
                )
            }
        },
        containerColor = colorResource(R.color.dialog_color),
        tonalElevation = 8.dp,
    )
}

@Composable
@Preview(showBackground = true, apiLevel = 30)
private fun EditProfileDialogPreview() {
    CyberSecPlatformTheme {
        val courses =
            listOf(
                CourseProgress(
                    courseId = 1,
                    title = "SQL Injection",
                    completedTasks = 5,
                    tasksCount = 10,
                ),
                CourseProgress(
                    courseId = 2,
                    title = "XSS",
                    completedTasks = 3,
                    tasksCount = 8,
                ),
            )

        val statsCoursesProgress =
            courses.map { course ->
                UserStatistics.CourseProgress(
                    averageScore = 85.0,
                    completionPercentage = course.progress,
                    courseId = course.courseId,
                    courseName = course.title ?: "Курс ${course.courseId}",
                    lastActivity = "2023-05-20T14:30:00Z",
                )
            }

        EditProfileDialog(
            uiState =
                ProfileUiState(
                    userData =
                        UserData(
                            id = 1,
                            username = "lika",
                            fullName = "Lika S",
                            email = "lika@example.com",
                            profileImage = "https://example.com/profile.jpg",
                            isAdmin = false,
                            isTeacher = true,
                            isActive = true,
                            lastLogin = "2023-05-20T14:30:00Z",
                            courses = courses,
                        ),
                    stats =
                        UserStatistics(
                            averageScore = 85.0,
                            completedCourses = 0,
                            completedTasks = courses.sumOf { it.completedTasks },
                            coursesProgress = statsCoursesProgress,
                            joinedDate = "2023-01-01",
                            lastActive = "2023-05-20T14:30:00Z",
                            totalCourses = courses.size,
                            totalPoints = 250,
                            totalTasks = courses.sumOf { it.tasksCount },
                            userId = 1,
                        ),
                ),
            onDismiss = {},
            onSave = { _, _, _ -> },
        )
    }
}
