package hse.diploma.cybersecplatform.ui.screens.courses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.di.vm.LocalViewModelFactory
import hse.diploma.cybersecplatform.domain.model.Course
import hse.diploma.cybersecplatform.ui.components.buttons.TabButton
import hse.diploma.cybersecplatform.ui.components.cards.CompletedCourseCard
import hse.diploma.cybersecplatform.ui.components.cards.StartedCourseCard
import hse.diploma.cybersecplatform.ui.components.dialogs.ConfirmResetProgressDialog
import hse.diploma.cybersecplatform.ui.navigation.Screen
import hse.diploma.cybersecplatform.ui.screens.error.ErrorScreen
import hse.diploma.cybersecplatform.ui.state.MyCoursesState
import hse.diploma.cybersecplatform.utils.logD

private const val TAG = "MyCoursesScreen"

@Composable
fun MyCoursesScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val viewModel: MyCoursesScreenViewModel = viewModel(factory = LocalViewModelFactory.current)
    val myCoursesState by viewModel.myCoursesState.collectAsState()
    var isStartedSelected by remember { mutableStateOf(true) }

    var showDialog by remember { mutableStateOf(false) }
    var courseToRestart by remember { mutableStateOf<Course?>(null) }

    LaunchedEffect(isStartedSelected) {
        logD(TAG, "Tab changed: isStartedSelected = $isStartedSelected")
    }

    LaunchedEffect(Unit) {
        viewModel.loadCourses()
    }

    when (myCoursesState) {
        is MyCoursesState.Loading -> CoursesShimmer()
        is MyCoursesState.Success -> {
            val coursesUiState = (myCoursesState as MyCoursesState.Success).uiState
            Column(modifier = modifier) {
                Row {
                    TabButton(
                        textId = R.string.started_courses_tab_button,
                        selected = isStartedSelected,
                        onClick = {
                            logD(TAG, "onStartedClick()")
                            isStartedSelected = true
                        },
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    TabButton(
                        textId = R.string.completed_courses_tab_button,
                        selected = !isStartedSelected,
                        onClick = {
                            logD(TAG, "onCompletedClick()")
                            isStartedSelected = false
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                if (isStartedSelected) {
                    StartedCoursesScreen(coursesUiState.startedCourses, navController)
                } else {
                    CompletedCoursesScreen(
                        courses = coursesUiState.completedCourses,
                        onRestartRequest = { course ->
                            courseToRestart = course
                            showDialog = true
                        },
                        navController = navController,
                    )
                }
            }

            if (showDialog) {
                ConfirmResetProgressDialog(
                    onConfirm = {
                        viewModel.onCompletedCourseRestart(courseToRestart!!)
                        showDialog = false
                        courseToRestart = null
                    },
                    onDismiss = {
                        showDialog = false
                        courseToRestart = null
                    },
                )
            }
        }
        is MyCoursesState.Error -> {
            val errorType = (myCoursesState as MyCoursesState.Error).errorType
            ErrorScreen(errorType, onReload = { viewModel.loadCourses() })
        }
    }
}

@Composable
private fun StartedCoursesScreen(
    courses: List<Course>,
    navController: NavHostController,
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
                onClick = { navController.navigate(Screen.TaskScreen.createRoute(item.vulnerabilityType)) },
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
    onRestartRequest: (Course) -> Unit,
    navController: NavHostController,
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
                onCardClick = { navController.navigate(Screen.TaskScreen.createRoute(item.vulnerabilityType)) },
                onRestartClick = { onRestartRequest(item) },
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun CoursesShimmer(
    modifier: Modifier = Modifier,
    itemsCount: Int = 6,
) {
    val baseColor = colorResource(id = R.color.dialog_color)
    val highlightColor = colorResource(id = R.color.button_enabled)

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        items(itemsCount) {
            ShimmerCourseCard(baseColor, highlightColor)
        }
    }
}

@Composable
private fun ShimmerCourseCard(
    baseColor: Color,
    shimmerHighlight: Color,
) {
    ElevatedCard(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(180.dp),
        shape = RoundedCornerShape(32.dp),
        colors =
            CardDefaults.elevatedCardColors(
                containerColor = baseColor,
            ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .placeholder(
                        visible = true,
                        color = shimmerHighlight,
                        highlight =
                            PlaceholderHighlight.shimmer(
                                highlightColor = shimmerHighlight,
                            ),
                    ),
            )
            Spacer(Modifier.height(12.dp))
            Box(
                Modifier
                    .fillMaxWidth(0.6f)
                    .height(20.dp)
                    .placeholder(
                        visible = true,
                        color = shimmerHighlight,
                        highlight =
                            PlaceholderHighlight.shimmer(
                                highlightColor = shimmerHighlight,
                            ),
                    ),
            )
            Spacer(Modifier.height(10.dp))
            Box(
                Modifier
                    .fillMaxWidth(0.8f)
                    .height(14.dp)
                    .placeholder(
                        visible = true,
                        color = shimmerHighlight,
                        highlight =
                            PlaceholderHighlight.shimmer(
                                highlightColor = shimmerHighlight,
                            ),
                    ),
            )
        }
    }
}

@Preview
@Composable
private fun MyCoursesScreenPreview() {
    MyCoursesScreen(navController = rememberNavController())
}
