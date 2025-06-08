package hse.diploma.cybersecplatform.ui.screens.courses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.domain.model.Course
import hse.diploma.cybersecplatform.mock.mockAllCourses
import hse.diploma.cybersecplatform.mock.mockCourses
import hse.diploma.cybersecplatform.ui.components.buttons.TabButton
import hse.diploma.cybersecplatform.ui.components.cards.CompletedCourseCard
import hse.diploma.cybersecplatform.ui.components.cards.StartedCourseCard
import hse.diploma.cybersecplatform.ui.components.dialogs.ConfirmResetProgressDialog
import hse.diploma.cybersecplatform.ui.screens.error.ErrorScreen
import hse.diploma.cybersecplatform.ui.screens.loading.LoadingScreen
import hse.diploma.cybersecplatform.ui.state.shared.MyCoursesState

private const val TAG = "MyCoursesScreen"

@Composable
fun MyCoursesScreen(
    state: MyCoursesState,
    isStartedSelected: Boolean,
    onTabChange: (Boolean) -> Unit,
    onCourseClick: (Int) -> Unit,
    onRestartRequest: (Course) -> Unit,
    onRestartConfirm: (Course) -> Unit,
    onReload: () -> Unit,
    showDialog: Boolean,
    onDismissDialog: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state) {
        is MyCoursesState.Loading -> LoadingScreen()
        is MyCoursesState.Success -> {
            val coursesUiState = state.uiState
            Column(modifier = modifier) {
                Row {
                    TabButton(
                        textId = R.string.started_courses_tab_button,
                        selected = isStartedSelected,
                        onClick = { onTabChange(true) },
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    TabButton(
                        textId = R.string.completed_courses_tab_button,
                        selected = !isStartedSelected,
                        onClick = { onTabChange(false) },
                        modifier = Modifier.weight(1f),
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                if (isStartedSelected) {
                    StartedCoursesScreen(
                        courses = coursesUiState.startedCourses,
                        onCourseClick = onCourseClick,
                    )
                } else {
                    CompletedCoursesScreen(
                        courses = coursesUiState.completedCourses,
                        onCourseClick = onCourseClick,
                        onRestartRequest = onRestartRequest,
                    )
                }
            }

            if (showDialog) {
                ConfirmResetProgressDialog(
                    onConfirm = { onRestartConfirm(state.selectedCourseForRestart!!) },
                    onDismiss = onDismissDialog,
                )
            }
        }
        is MyCoursesState.Error -> {
            ErrorScreen(state.errorType, onReload = onReload)
        }
    }
}

@Composable
private fun StartedCoursesScreen(
    courses: List<Course>,
    onCourseClick: (Int) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(courses) { item ->
            StartedCourseCard(
                course = item,
                onClick = { onCourseClick(item.id) },
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun CompletedCoursesScreen(
    courses: List<Course>,
    onCourseClick: (Int) -> Unit,
    onRestartRequest: (Course) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(courses) { item ->
            CompletedCourseCard(
                course = item,
                onCardClick = { onCourseClick(item.id) },
                onRestartClick = { onRestartRequest(item) },
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
@Preview(name = "MyCoursesScreen", showBackground = true, apiLevel = 30)
private fun MyCoursesScreenPreview() {
    MyCoursesScreen(
        state = MyCoursesState.Success(CoursesUiState(mockCourses, mockAllCourses)),
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
