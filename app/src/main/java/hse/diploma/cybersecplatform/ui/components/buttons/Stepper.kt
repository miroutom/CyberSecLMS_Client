package hse.diploma.cybersecplatform.ui.components.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hse.diploma.cybersecplatform.R

@Composable
fun CustomStepper(
    currentStep: Int,
    totalSteps: Int = 3,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalSteps) { index ->
            if (index == currentStep) {
                Box(
                    modifier = Modifier
                        .size(width = 50.dp, height = 8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(colorResource(R.color.button_enabled))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .border(
                            width = 1.dp,
                            color = colorResource(R.color.button_enabled),
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

@Preview
@Composable
fun StepperPreview() {
    MaterialTheme {
        CustomStepper(
            currentStep = 0,
            totalSteps = 3,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
