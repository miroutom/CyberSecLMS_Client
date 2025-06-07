package hse.diploma.cybersecplatform.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import hse.diploma.cybersecplatform.WithTheme
import hse.diploma.cybersecplatform.mock.mockAllCourses
import hse.diploma.cybersecplatform.ui.screens.courses.CoursesUiState
import hse.diploma.cybersecplatform.ui.state.shared.AllCoursesState

@Composable
@Preview(showBackground = true, apiLevel = 30)
fun PreviewHomeScreen() =
    WithTheme {
        val state =
            AllCoursesState.Success(
                CoursesUiState(
                    courses = mockAllCourses,
                    filteredCourses = emptyList(),
                ),
            )

        HomeScreen(
            state = state,
            searchQuery = TextFieldValue(""),
            onSearchQueryChange = {},
            onCourseClick = {},
            onReload = {},
            onCreateCourseClick = {},
            isTeacher = true,
        )
    }
