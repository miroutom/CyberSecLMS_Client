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
import hse.diploma.cybersecplatform.data.model.UserData
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

@Preview
@Composable
private fun EditProfileDialogPreview() {
    CyberSecPlatformTheme {
        EditProfileDialog(
            uiState =
                ProfileUiState(
                    UserData(
                        username = "lika",
                        fullName = "lika s",
                        email = "example.com",
                        profileImage = "image",
                    ),
                ),
            onDismiss = {},
            onSave = { _, _, _ -> },
        )
    }
}
