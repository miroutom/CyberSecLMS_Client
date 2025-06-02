package hse.diploma.cybersecplatform.ui.screens.courses

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import hse.diploma.cybersecplatform.WithTheme
import hse.diploma.cybersecplatform.mock.mockCourses
import hse.diploma.cybersecplatform.ui.state.shared.MyCoursesState

@Composable
@PreviewLightDark
@Preview(showBackground = true, device = "spec:parent=pixel_5")
fun PreviewMyCoursesScreen() =
    WithTheme {
        val state =
            MyCoursesState.Success(
                CoursesUiState(
                    startedCourses = mockCourses,
                    completedCourses = mockCourses,
                ),
            )

        MyCoursesScreen(
            state = state,
            isStartedSelected = true,
            onTabChange = {},
            onCourseClick = {},
            onRestartRequest = {},
            onRestartConfirm = {},
            onReload = {},
            showDialog = false,
            onDismissDialog = {},
        )
    }
