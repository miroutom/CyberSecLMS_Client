package hse.diploma.cybersecplatform.ui.components.bars

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.data.model.submission.SubmissionResult
import hse.diploma.cybersecplatform.ui.theme.Typography

@Composable
fun SubmissionResultBar(
    result: SubmissionResult,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (result.success) Color(0xFF4CAF50) else Color(0xFFF44336)

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .padding(16.dp),
    ) {
        Text(
            text = if (result.success) stringResource(R.string.success) else stringResource(R.string.failure),
            color = colorResource(R.color.main_text_color),
            style = Typography.bodySmall,
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (result.score != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.task_points, result.score),
                color = colorResource(R.color.main_text_color),
                style = Typography.bodyMedium,
            )
        }
    }
}

@Composable
@Preview(showBackground = true, apiLevel = 30)
fun SubmissionResultBarPreview() {
    SubmissionResultBar(
        result =
            SubmissionResult(
                success = true,
                message = "Success message",
                score = 100,
            ),
    )
}
