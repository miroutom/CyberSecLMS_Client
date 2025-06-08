package hse.diploma.cybersecplatform.ui.components.textFields

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.ui.theme.CyberSecPlatformTheme
import hse.diploma.cybersecplatform.ui.theme.Typography

@Composable
fun EditorTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    minLines: Int = if (singleLine) 1 else 3,
    isError: Boolean = false,
    errorMessage: String? = null,
) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = colorResource(R.color.error_text_color),
                style = Typography.labelSmall,
                modifier = Modifier.padding(start = 8.dp),
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
        TextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = Typography.bodyMedium,
            label = {
                Text(
                    text = label,
                    style = Typography.bodySmall,
                    color = colorResource(R.color.main_text_color),
                )
            },
            singleLine = singleLine,
            minLines = minLines,
            colors =
                TextFieldDefaults.colors(
                    focusedContainerColor = colorResource(R.color.background),
                    unfocusedContainerColor = colorResource(R.color.background),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    errorTextColor = colorResource(R.color.error_text_color),
                    errorIndicatorColor = Color.Transparent,
                    errorLabelColor = colorResource(R.color.error_text_color),
                ),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color =
                            if (isError) {
                                colorResource(R.color.error_text_color)
                            } else {
                                colorResource(R.color.main_text_color)
                            },
                        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_radius_large)),
                    ),
            isError = isError,
        )
    }
}

@Composable
fun EditorDropdownField(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null,
) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = colorResource(R.color.error_text_color),
                style = Typography.labelSmall,
                modifier = Modifier.padding(start = 8.dp),
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
        TextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            textStyle = Typography.bodyMedium,
            label = {
                Text(
                    text = label,
                    style = Typography.bodySmall,
                    color = colorResource(R.color.main_text_color),
                )
            },
            trailingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_expand_more),
                    contentDescription = "Expand",
                    tint = colorResource(R.color.main_text_color),
                )
            },
            colors =
                TextFieldDefaults.colors(
                    focusedContainerColor = colorResource(R.color.background),
                    unfocusedContainerColor = colorResource(R.color.background),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    errorTextColor = colorResource(R.color.error_text_color),
                    errorIndicatorColor = Color.Transparent,
                    errorLabelColor = colorResource(R.color.error_text_color),
                ),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color =
                            if (isError) {
                                colorResource(R.color.error_text_color)
                            } else {
                                colorResource(R.color.main_text_color)
                            },
                        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_radius_large)),
                    ),
            isError = isError,
        )
    }
}

@Composable
@Preview(showBackground = true, apiLevel = 30)
fun EditorTextFieldPreview() {
    CyberSecPlatformTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            EditorTextField(
                value = TextFieldValue("Course Title"),
                onValueChange = {},
                label = "Course Title",
            )

            EditorTextField(
                value = TextFieldValue(""),
                onValueChange = {},
                label = "Description",
                singleLine = false,
                isError = true,
                errorMessage = "Description is required",
            )

            EditorDropdownField(
                value = "XSS",
                label = "Vulnerability Type",
            )
        }
    }
}
