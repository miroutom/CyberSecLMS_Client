package hse.diploma.cybersecplatform.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun OutlinedLinearProgressBar(
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(50)
    Box(
        modifier =
            modifier
                .height(12.dp)
                .fillMaxWidth()
                .border(2.dp, color, shape)
                .clip(shape),
        contentAlignment = Alignment.CenterStart,
    ) {
        LinearProgressIndicator(
            progress = { progress },
            color = color,
            trackColor = Color.Transparent,
            modifier =
                Modifier
                    .matchParentSize()
                    .clip(shape)
                    .background(
                        color = Color.White,
                    ),
        )
    }
}

@Preview
@Composable
private fun OutlinedLinearProgressBarPreview() {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        OutlinedLinearProgressBar(progress = 0.34f, color = Color(0xFF4869F0))
        OutlinedLinearProgressBar(progress = 0.70f, color = Color(0xFF5DE6C2))
        OutlinedLinearProgressBar(progress = 0.50f, color = Color(0xFFF0B179))
    }
}
