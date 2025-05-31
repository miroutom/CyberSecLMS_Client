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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.ui.components.textFields.PasswordField
import hse.diploma.cybersecplatform.ui.theme.Montserrat

@Composable
fun DeleteAccountDialog(
    onDismiss: () -> Unit,
    onConfirm: (password: String) -> Unit,
    isLoading: Boolean,
) {
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var error by remember { mutableStateOf<String?>(null) }

    val passwordRequired = stringResource(R.string.current_password_required)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.delete_account_title),
                fontFamily = Montserrat,
                fontWeight = FontWeight.Medium,
                color = colorResource(R.color.error_text_color),
            )
        },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.delete_account_confirmation),
                    fontFamily = Montserrat,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.auth_label_confirm_password),
                    fontFamily = Montserrat,
                    fontSize = 14.sp,
                )

                Spacer(modifier = Modifier.height(8.dp))

                PasswordField(
                    value = password,
                    onValueChange = {
                        password = it
                        error = null
                    },
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
                    if (password.text.isBlank()) {
                        error = passwordRequired
                    } else {
                        error = null
                        onConfirm(password.text)
                    }
                },
                enabled = !isLoading && password.text.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.error_text_color),
                    disabledContainerColor = colorResource(R.color.button_disabled),
                ),
            ) {
                Text(
                    text = stringResource(R.string.delete_button),
                    fontFamily = Montserrat,
                )
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
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
