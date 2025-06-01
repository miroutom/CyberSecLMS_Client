package hse.diploma.cybersecplatform.ui.screens.tasks

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import hse.diploma.cybersecplatform.WithTheme
import hse.diploma.cybersecplatform.ui.model.VulnerabilityType

@Composable
@PreviewLightDark
@Preview(showBackground = true, device = "spec:parent=pixel_5")
fun PreviewTasksScreen() =
    WithTheme {
        TasksScreen(
            vulnerabilityType = VulnerabilityType.SQL,
        )
    }
