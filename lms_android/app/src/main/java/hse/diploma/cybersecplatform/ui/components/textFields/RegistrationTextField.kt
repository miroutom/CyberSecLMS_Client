package hse.diploma.cybersecplatform.ui.components.textFields

import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.ui.theme.CyberSecPlatformTheme
import hse.diploma.cybersecplatform.ui.theme.Montserrat
import hse.diploma.cybersecplatform.ui.theme.Typography

@Composable
fun RegistrationTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    @StringRes labelId: Int,
    modifier: Modifier = Modifier,
) {
    val label = stringResource(labelId)

    Column(modifier = modifier.padding(8.dp)) {
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
                        color = colorResource(R.color.main_text_color),
                        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_radius_large)),
                    ),
        )
    }
}

@PreviewLightDark
@Composable
fun RegistrationTextFieldPreview() {
    CyberSecPlatformTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            RegistrationTextField(
                value = TextFieldValue(""),
                labelId = R.string.auth_label_full_name,
                onValueChange = {},
            )
            RegistrationTextField(
                value = TextFieldValue(""),
                labelId = R.string.auth_label_username,
                onValueChange = {},
            )
        }
    }
}
