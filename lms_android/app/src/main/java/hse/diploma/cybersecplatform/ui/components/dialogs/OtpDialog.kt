package hse.diploma.cybersecplatform.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.ui.theme.Montserrat
import hse.diploma.cybersecplatform.utils.maskEmail

@Composable
fun OtpDialog(
    email: String,
    isLoading: Boolean,
    onOtpSubmit: (String) -> Unit,
    onDismiss: () -> Unit,
    error: String? = null,
) {
    var code by remember { mutableStateOf("") }
    val codeLength = 6
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val maskedEmail = maskEmail(email)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.otp_dialog_title),
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                fontFamily = Montserrat,
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.otp_sent_to_email, maskedEmail),
                    fontFamily = Montserrat,
                    fontSize = 14.sp,
                    color = Color.Black,
                )
                Spacer(Modifier.height(16.dp))
                Box(
                    modifier =
                        Modifier
                            .padding(vertical = 8.dp)
                            .clickable { focusRequester.requestFocus() },
                ) {
                    BasicTextField(
                        value = code,
                        onValueChange = {
                            val value = it.filter { c -> c.isDigit() }.take(codeLength)
                            code = value
                            if (value.length == codeLength) {
                                focusManager.clearFocus()
                            }
                        },
                        modifier =
                            Modifier
                                .width((codeLength * 48).dp)
                                .height(48.dp)
                                .alpha(0f)
                                .focusRequester(focusRequester),
                    )

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.matchParentSize(),
                    ) {
                        for (i in 0 until codeLength) {
                            val char = code.getOrNull(i)?.toString() ?: ""
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier =
                                    Modifier
                                        .size(40.dp)
                                        .padding(2.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.White)
                                        .border(
                                            width = 2.dp,
                                            color =
                                                if (code.length == i) {
                                                    colorResource(R.color.button_enabled)
                                                } else {
                                                    Color.LightGray
                                                },
                                            shape = RoundedCornerShape(6.dp),
                                        ),
                            ) {
                                Text(
                                    text = char,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = Montserrat,
                                    color = if (char.isEmpty()) Color.Gray else Color.Black,
                                )
                            }
                        }
                    }
                }
                if (!error.isNullOrEmpty()) {
                    Text(
                        text = error,
                        color = Color.Red,
                        fontSize = 12.sp,
                        fontFamily = Montserrat,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onOtpSubmit(code) },
                enabled = code.length == codeLength && !isLoading,
                colors =
                    ButtonDefaults.filledTonalButtonColors(
                        containerColor = colorResource(R.color.button_enabled),
                        contentColor = Color.White,
                    ),
            ) {
                Text(
                    text = stringResource(R.string.confirm_button),
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
                enabled = !isLoading,
            ) {
                Text(
                    text = stringResource(R.string.cancel_button),
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Normal,
                )
            }
        },
        containerColor = colorResource(R.color.dialog_color),
        tonalElevation = 8.dp,
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Preview
@Composable
private fun OtpDialogPreview() {
    OtpDialog(
        email = "test@test.com",
        isLoading = false,
        onOtpSubmit = {},
        onDismiss = {},
    )
}
