package hse.diploma.cybersecplatform.ui.components.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.ui.theme.Montserrat

@Composable
fun DeleteAccountDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    isLoading: Boolean
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.delete_account_title),
                fontFamily = Montserrat,
                fontWeight = FontWeight.Medium,
                color = Color.Red
            )
        },
        text = {
            Text(
                text = stringResource(R.string.delete_account_confirmation),
                fontFamily = Montserrat
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                )
            ) {
                Text(
                    text = stringResource(R.string.delete_button),
                    fontFamily = Montserrat
                )
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.button_enabled)
                )
            ) {
                Text(
                    text = stringResource(R.string.cancel_button),
                    fontFamily = Montserrat
                )
            }
        },
        containerColor = colorResource(id = R.color.dialog_color)
    )
}
