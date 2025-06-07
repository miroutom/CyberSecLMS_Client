package hse.diploma.cybersecplatform.ui.components.buttons

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.ui.theme.Typography

@Composable
fun SubmitButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(50.dp),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50),
            ),
    ) {
        Text(
            text = stringResource(R.string.submit_button),
            style = Typography.bodyLarge,
        )
    }
}
