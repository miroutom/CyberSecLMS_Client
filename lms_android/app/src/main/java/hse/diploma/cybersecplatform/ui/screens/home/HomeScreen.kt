package hse.diploma.cybersecplatform.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.domain.model.Course
import hse.diploma.cybersecplatform.mock.mockAllCourses
import hse.diploma.cybersecplatform.ui.components.bars.SearchBar
import hse.diploma.cybersecplatform.ui.components.cards.BaseCourseCard
import hse.diploma.cybersecplatform.ui.screens.courses.CoursesUiState
import hse.diploma.cybersecplatform.ui.screens.error.ErrorScreen
import hse.diploma.cybersecplatform.ui.screens.loading.LoadingScreen
import hse.diploma.cybersecplatform.ui.state.shared.AllCoursesState
import hse.diploma.cybersecplatform.ui.theme.CyberSecPlatformTheme

@Composable
fun HomeScreen(
    state: AllCoursesState,
    searchQuery: TextFieldValue,
    onSearchQueryChange: (TextFieldValue) -> Unit,
    onCourseClick: (Int) -> Unit,
    onReload: () -> Unit,
    onCreateCourseClick: () -> Unit,
    isTeacher: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize()) {
            when (state) {
                is AllCoursesState.Loading -> LoadingScreen()
                is AllCoursesState.Success -> {
                    val coursesUiState = state.uiState

                    SearchBar(
                        searchQuery = searchQuery,
                        onSearchQueryChange = onSearchQueryChange,
                        enableFiltering = false,
                        modifier = Modifier.background(colorResource(R.color.background)),
                    )

                    val coursesToShow =
                        coursesUiState.filteredCourses.ifEmpty {
                            coursesUiState.courses
                        }
                    CoursesContent(
                        items = coursesToShow,
                        onCourseClick = onCourseClick,
                    )
                }
                is AllCoursesState.Error -> {
                    ErrorScreen(
                        errorType = state.errorType,
                        onReload = onReload,
                    )
                }
            }
        }

        if (isTeacher) {
            FloatingActionButton(
                onClick = onCreateCourseClick,
                modifier =
                    Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                containerColor = colorResource(R.color.button_enabled),
                contentColor = Color.White,
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Create course")
            }
        }
    }
}

@Composable
fun CoursesContent(
    items: List<Course>,
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

        items(items) { item ->
            BaseCourseCard(
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
@Preview(name = "HomeScreen", showBackground = true, apiLevel = 30)
fun HomeScreenPreview() {
    CyberSecPlatformTheme {
        HomeScreen(
            state = AllCoursesState.Success(CoursesUiState(mockAllCourses)),
            searchQuery = TextFieldValue(""),
            onSearchQueryChange = {},
            onCourseClick = {},
            onReload = {},
            isTeacher = true,
            onCreateCourseClick = {},
        )
    }
}
