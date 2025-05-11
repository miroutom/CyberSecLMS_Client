package hse.diploma.cybersecplatform.ui.components.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.ui.theme.Montserrat

@Composable
fun RestartCourseButton(
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = true,
        modifier = modifier.fillMaxWidth().wrapContentHeight(),
        colors =
            ButtonDefaults.outlinedButtonColors(
                contentColor = color,
            ),
        border =
            BorderStroke(
                width = 1.dp,
                color = color,
            ),
        shape = RoundedCornerShape(10.dp),
    ) {
        Text(
            text = stringResource(R.string.restart_course_button),
            fontFamily = Montserrat,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            fontStyle = FontStyle.Normal,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 4.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RestartCourseButtonPreview() {
    RestartCourseButton(
        color = Color(0xFF4869F0),
        onClick = {},
    )
}
