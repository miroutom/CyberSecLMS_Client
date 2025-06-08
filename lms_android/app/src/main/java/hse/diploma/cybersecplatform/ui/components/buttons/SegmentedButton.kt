package hse.diploma.cybersecplatform.ui.components.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.ui.theme.Montserrat
import hse.diploma.cybersecplatform.ui.theme.Typography

@Composable
fun SegmentedButton(
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_radius_large)))
                .border(
                    width = 1.dp,
                    color = colorResource(R.color.button_enabled),
                    shape = RoundedCornerShape(dimensionResource(R.dimen.corner_radius_large)),
                ),
        horizontalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        items.forEachIndexed { index, item ->
            val shape =
                when (index) {
                    0 ->
                        RoundedCornerShape(
                            topStart = dimensionResource(R.dimen.corner_radius_large),
                            bottomStart = dimensionResource(R.dimen.corner_radius_large),
                            topEnd = 0.dp,
                            bottomEnd = 0.dp,
                        )
                    items.lastIndex ->
                        RoundedCornerShape(
                            topStart = 0.dp,
                            bottomStart = 0.dp,
                            topEnd = dimensionResource(R.dimen.corner_radius_large),
                            bottomEnd = dimensionResource(R.dimen.corner_radius_large),
                        )
                    else -> RoundedCornerShape(0.dp)
                }

            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 48.dp)
                        .clickable { onItemSelected(item) }
                        .background(
                            color =
                                if (item == selectedItem) {
                                    colorResource(R.color.button_enabled)
                                } else {
                                    Color.Transparent
                                },
                            shape = shape,
                        )
                        .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = item,
                    color =
                        if (item == selectedItem) {
                            colorResource(R.color.background)
                        } else {
                            colorResource(R.color.main_text_color)
                        },
                    fontFamily = Montserrat,
                    style = Typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true, apiLevel = 30)
fun SegmentedButtonPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SegmentedButton(
            items = listOf("Easy", "Medium", "Hard"),
            selectedItem = "Medium",
            onItemSelected = {},
        )

        SegmentedButton(
            items = listOf("XSS", "CSRF", "SQL Injection"),
            selectedItem = "XSS",
            onItemSelected = {},
        )
    }
}
