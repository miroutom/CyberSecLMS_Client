package hse.diploma.cybersecplatform.ui.components.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.ui.screens.profile.ProfileUiState
import hse.diploma.cybersecplatform.ui.theme.CyberSecPlatformTheme
import hse.diploma.cybersecplatform.ui.theme.Montserrat
import hse.diploma.cybersecplatform.ui.theme.Typography

@Composable
fun EditProfileDialog(
    uiState: ProfileUiState,
    onDismiss: () -> Unit,
    onSave: (username: String, fullName: String, email: String) -> Unit,
    isSaving: Boolean = false,
    errorMessage: String? = null
) {
    var username by remember { mutableStateOf(uiState.username) }
    var fullName by remember { mutableStateOf(uiState.fullName) }
    var email by remember { mutableStateOf(uiState.email) }

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
                    enabled = !isSaving,
                    singleLine = true,
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text(stringResource(R.string.auth_label_full_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSaving,
                    singleLine = true,
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(stringResource(R.string.auth_label_email)) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSaving,
                    singleLine = true,
                )
                if (!errorMessage.isNullOrBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        errorMessage,
                        color = colorResource(R.color.error_dialog_text),
                        style = Typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(username, fullName, email) },
                enabled = !isSaving,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = colorResource(R.color.button_enabled),
                    contentColor = Color.White,
                ),
            ) {
                if (isSaving) CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                else Text(
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
                enabled = !isSaving,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = colorResource(R.color.button_enabled),
                    contentColor = Color.White,
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
            uiState = ProfileUiState(
                username = "username",
                fullName = "fullName",
                email = "email",
                avatarUrl = null
            ),
            onDismiss = {},
            onSave = { _, _, _ -> },
        )
    }
}
