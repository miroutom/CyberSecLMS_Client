package hse.diploma.cybersecplatform.ui.components.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.ui.theme.Montserrat

@Composable
fun ConfirmResetProgressDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.reset_progress_title),
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                fontFamily = Montserrat,
            )
        },
        text = {
            Text(
                text = stringResource(R.string.reset_progress_body),
                fontSize = 16.sp,
                fontFamily = Montserrat,
                color = Color.Red,
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors =
                    ButtonDefaults.filledTonalButtonColors(
                        containerColor = colorResource(R.color.button_enabled),
                        contentColor = Color.White,
                    ),
            ) {
                Text(
                    text = stringResource(R.string.yes_button),
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
                        contentColor = Color.White,
                    ),
            ) {
                Text(
                    text = stringResource(R.string.no_button),
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Normal,
                )
            }
        },
        containerColor = colorResource(R.color.dialog_color),
        tonalElevation = 8.dp,
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
private fun ConfirmResetProgressDialogPreview() {
    ConfirmResetProgressDialog(onConfirm = {}, onDismiss = {})
}
