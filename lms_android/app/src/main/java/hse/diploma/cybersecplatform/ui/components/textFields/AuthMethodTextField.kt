package hse.diploma.cybersecplatform.ui.components.textFields

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.ui.theme.CyberSecPlatformTheme
import hse.diploma.cybersecplatform.ui.theme.Montserrat
import hse.diploma.cybersecplatform.ui.theme.Typography
import hse.diploma.cybersecplatform.utils.isLoginValidAndAuthMethodType

@Composable
fun AuthMethodTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
) {
    val label = stringResource(R.string.auth_label_email)
    val isAuthMethodTypeValid = isLoginValidAndAuthMethodType(value.text)

    Column(modifier = modifier.padding(8.dp)) {
        if (!isAuthMethodTypeValid && value.text.isNotEmpty()) {
            Text(
                text = "${stringResource(R.string.auth_label_error)} ${label.lowercase()}",
                color = Color.Red,
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
                    color = Color.Black,
                )
            },
            colors =
                TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
            modifier =
                modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = if (!isAuthMethodTypeValid && value.text.isNotEmpty()) Color.Red else Color.Black,
                        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_radius_large)),
                    ),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AuthMethodTextFieldPreview() {
    CyberSecPlatformTheme {
        AuthMethodTextField(
            value = TextFieldValue(""),
            onValueChange = {},
        )
    }
}
