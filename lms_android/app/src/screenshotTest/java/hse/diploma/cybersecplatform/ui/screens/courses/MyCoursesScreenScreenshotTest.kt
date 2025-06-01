package hse.diploma.cybersecplatform.ui.screens.courses

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.navigation.compose.rememberNavController
import hse.diploma.cybersecplatform.WithTheme

@Composable
@PreviewLightDark
@Preview(showBackground = true, device = "spec:parent=pixel_5")
fun PreviewMyCoursesScreen() =
    WithTheme {
        MyCoursesScreen(
            navController = rememberNavController(),
        )
    }
