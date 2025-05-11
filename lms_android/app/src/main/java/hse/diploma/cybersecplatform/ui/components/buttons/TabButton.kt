package hse.diploma.cybersecplatform.ui.components.buttons

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.ui.theme.Montserrat
import hse.diploma.cybersecplatform.ui.theme.Typography

@Composable
fun TabButton(
    @StringRes textId: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (selected) {
        FilledButton(
            text = stringResource(textId),
            onClick = onClick,
            modifier = modifier,
        )
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier.fillMaxWidth().wrapContentHeight(),
            colors =
                ButtonDefaults.outlinedButtonColors(
                    contentColor = colorResource(R.color.tab_button_not_selected_color),
                ),
            border =
                BorderStroke(
                    width = 1.dp,
                    color = colorResource(R.color.tab_button_not_selected_color),
                ),
            shape = RoundedCornerShape(dimensionResource(R.dimen.corner_radius_large)),
        ) {
            Text(
                text = stringResource(textId),
                fontFamily = Montserrat,
                style = Typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 8.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TabButtonPreview() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TabButton(
            textId = R.string.started_courses_tab_button,
            selected = true,
            onClick = {},
            modifier = Modifier.weight(1f),
        )
        TabButton(
            textId = R.string.completed_courses_tab_button,
            selected = false,
            onClick = {},
            modifier = Modifier.weight(1f),
        )
    }
}
