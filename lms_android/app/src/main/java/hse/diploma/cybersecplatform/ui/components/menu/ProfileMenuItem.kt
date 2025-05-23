package hse.diploma.cybersecplatform.ui.components.menu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.ui.theme.Typography

@Composable
fun MenuItem(
    icon: Painter,
    text: String,
    iconTint: Color = colorResource(R.color.icon_black_tint),
    textColor: Color = colorResource(R.color.main_text_color),
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(vertical = 16.dp),
    ) {
        Icon(
            painter = icon,
            contentDescription = text,
            tint = iconTint,
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = text,
            style = Typography.bodyMedium,
            color = textColor,
        )
    }
}
