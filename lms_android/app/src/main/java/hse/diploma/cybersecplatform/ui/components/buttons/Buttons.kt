package hse.diploma.cybersecplatform.ui.components.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.ui.theme.Montserrat
import hse.diploma.cybersecplatform.ui.theme.Typography

@Composable
fun FilledButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().wrapContentHeight(),
        enabled = enabled,
        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_radius_large)),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.button_enabled),
                contentColor = colorResource(R.color.background),
                disabledContainerColor = colorResource(R.color.button_disabled),
                disabledContentColor = colorResource(R.color.background),
            ),
    ) {
        Text(
            text = text,
            fontFamily = Montserrat,
            style = Typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp),
        )
    }
}

@Composable
fun CustomOutlinedButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().wrapContentHeight(),
        colors =
            ButtonDefaults.outlinedButtonColors(
                contentColor = colorResource(R.color.main_text_color),
                disabledContentColor = colorResource(R.color.text_disabled),
            ),
        border =
            BorderStroke(
                width = 1.dp,
                color =
                    if (enabled) {
                        colorResource(R.color.button_enabled)
                    } else {
                        colorResource(R.color.button_disabled)
                    },
            ),
        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_radius_large)),
    ) {
        Text(
            text = text,
            fontFamily = Montserrat,
            style = Typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp),
        )
    }
}

@Composable
fun TextButton(
    text: String,
    onClick: () -> Unit,
    textAlign: TextAlign = TextAlign.Center,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        fontFamily = Montserrat,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        textAlign = textAlign,
        color = colorResource(R.color.main_text_color),
        modifier =
            modifier
                .clickable(onClick = onClick)
                .fillMaxWidth(),
    )
}

@Composable
@PreviewLightDark
@Preview(name = "Buttons", showBackground = true, showSystemUi = true)
fun ButtonsPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        FilledButton(
            text = "Filled Button",
            onClick = { },
            enabled = true,
        )

        FilledButton(
            text = "Filled Button (Disabled)",
            onClick = { },
            enabled = false,
        )

        CustomOutlinedButton(
            text = "Outlined Button",
            onClick = { },
            enabled = true,
        )

        CustomOutlinedButton(
            text = "Outlined Button (Disabled)",
            onClick = { },
            enabled = false,
        )
        TextButton(
            text = "Text Button",
            onClick = { },
        )
    }
}
