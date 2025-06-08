package hse.diploma.cybersecplatform.ui.components.textFields

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.ui.theme.CyberSecPlatformTheme
import hse.diploma.cybersecplatform.ui.theme.Montserrat
import hse.diploma.cybersecplatform.ui.theme.Typography

@Composable
fun PasswordConfirmationField(
    value: TextFieldValue,
    passwordValue: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
) {
    val label = stringResource(R.string.auth_label_confirm_password)

    val isPasswordConfirmationVisible = remember { mutableStateOf(false) }
    val isConfirmationEqualsPassword = passwordValue.text == value.text

    Column(modifier = modifier.padding(8.dp)) {
        if (!isConfirmationEqualsPassword && value.text.isNotEmpty()) {
            Text(
                text = stringResource(R.string.confirmation_and_password_are_not_equal),
                color = colorResource(R.color.error_text_color),
                style = Typography.labelSmall,
                modifier = Modifier.padding(start = 8.dp),
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        TextField(
            value = value,
            textStyle = Typography.labelLarge,
            onValueChange = onValueChange,
            label = {
                Text(
                    text = label,
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Normal,
                    fontSize = if (value.text.isEmpty()) 16.sp else 10.sp,
                    color = colorResource(R.color.main_text_color),
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation =
                if (isPasswordConfirmationVisible.value) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation(mask = '*')
                },
            trailingIcon = {
                IconButton(
                    onClick = { isPasswordConfirmationVisible.value = !isPasswordConfirmationVisible.value },
                ) {
                    Icon(
                        painter =
                            if (isPasswordConfirmationVisible.value) {
                                painterResource(R.drawable.ic_eye_off)
                            } else {
                                painterResource(R.drawable.ic_eye)
                            },
                        contentDescription = "Password confirmation visibility icon",
                    )
                }
            },
            colors =
                TextFieldDefaults.colors(
                    focusedContainerColor = colorResource(R.color.background),
                    unfocusedContainerColor = colorResource(R.color.background),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
            modifier =
                modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color =
                            if (!isConfirmationEqualsPassword && value.text.isNotEmpty()) {
                                colorResource(R.color.error_text_color)
                            } else {
                                colorResource(R.color.main_text_color)
                            },
                        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_radius_large)),
                    ),
        )
    }
}

@Preview(showBackground = true, apiLevel = 30)
@Composable
fun PasswordConfirmationFieldPreview() {
    CyberSecPlatformTheme {
        PasswordConfirmationField(
            value = TextFieldValue("9400g004A.2"),
            passwordValue = TextFieldValue("9300315295"),
            onValueChange = {},
        )
    }
}
