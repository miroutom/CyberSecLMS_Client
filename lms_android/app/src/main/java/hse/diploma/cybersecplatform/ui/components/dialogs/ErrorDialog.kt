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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.ui.theme.Montserrat

@Composable
fun ErrorDialog(
    errorMessage: String,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.error_dialog_title),
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                fontFamily = Montserrat,
                color = Color.Black,
            )
        },
        text = {
            Text(
                text = errorMessage,
                fontFamily = Montserrat,
                fontSize = 14.sp,
                color = colorResource(R.color.error_dialog_text),
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors =
                    ButtonDefaults.filledTonalButtonColors(
                        containerColor = colorResource(R.color.error_dialog_text),
                        contentColor = Color.White,
                    ),
            ) {
                Text(
                    text = stringResource(R.string.ok_button),
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Normal,
                )
            }
        },
        containerColor = colorResource(R.color.error_dialog_background),
        tonalElevation = 8.dp,
    )
}

@Preview
@Composable
private fun ErrorDialogPreview() {
    ErrorDialog(
        errorMessage = "Error message",
        onDismiss = {},
    )
}
