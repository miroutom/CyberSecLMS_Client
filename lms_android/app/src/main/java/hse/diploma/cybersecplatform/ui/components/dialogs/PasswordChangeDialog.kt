package hse.diploma.cybersecplatform.ui.components.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.ui.components.textFields.PasswordConfirmationField
import hse.diploma.cybersecplatform.ui.components.textFields.PasswordField
import hse.diploma.cybersecplatform.ui.theme.Montserrat
import hse.diploma.cybersecplatform.utils.isPasswordValid

@Composable
fun PasswordChangeDialog(
    onDismiss: () -> Unit,
    onSubmit: (currentPassword: String, newPassword: String) -> Unit,
    isLoading: Boolean,
) {
    var currentPassword by remember { mutableStateOf(TextFieldValue("")) }
    var newPassword by remember { mutableStateOf(TextFieldValue("")) }
    var confirmPassword by remember { mutableStateOf(TextFieldValue("")) }
    var error by remember { mutableStateOf<String?>(null) }

    val currentPasswordRequired = stringResource(R.string.current_password_required)
    val newPasswordRequired = stringResource(R.string.new_password_required)
    val passwordsNotMatchError = stringResource(R.string.passwords_dont_match)
    val currentAndNewPasswordsAreEqual = stringResource(R.string.current_and_new_passwords_are_equal)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.update_password_setting),
                fontFamily = Montserrat,
                fontWeight = FontWeight.Medium,
            )
        },
        text = {
            Column {
                PasswordField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(8.dp))

                PasswordField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = stringResource(R.string.new_password),
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(8.dp))

                PasswordConfirmationField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    passwordValue = TextFieldValue(newPassword.text),
                    modifier = Modifier.fillMaxWidth(),
                )

                error?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = it,
                        color = colorResource(R.color.error_text_color),
                        fontSize = 12.sp,
                        fontFamily = Montserrat,
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        currentPassword.text.isBlank() -> error = currentPasswordRequired
                        newPassword.text.isBlank() -> error = newPasswordRequired
                        !isPasswordValid(newPassword.text) -> {
                            error = null
                        }
                        currentPassword == newPassword -> error = currentAndNewPasswordsAreEqual
                        newPassword.text != confirmPassword.text -> error = passwordsNotMatchError
                        else -> {
                            error = null
                            onSubmit(currentPassword.text, newPassword.text)
                        }
                    }
                },
                enabled =
                    !isLoading &&
                        isPasswordValid(newPassword.text) &&
                        newPassword.text == confirmPassword.text &&
                        currentPassword.text.isNotEmpty(),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.button_enabled),
                        disabledContainerColor = colorResource(R.color.button_disabled),
                    ),
            ) {
                Text(
                    text = stringResource(R.string.confirm_button),
                    fontFamily = Montserrat,
                )
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                enabled = !isLoading,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.button_enabled),
                    ),
            ) {
                Text(
                    text = stringResource(R.string.cancel_button),
                    fontFamily = Montserrat,
                )
            }
        },
        containerColor = colorResource(R.color.background),
    )
}

@PreviewLightDark
@Composable
private fun PasswordChangeDialogPreview() {
    PasswordChangeDialog(
        onDismiss = {},
        onSubmit = { _, _ -> },
        isLoading = false,
    )
}
