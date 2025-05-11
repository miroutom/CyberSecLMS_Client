package hse.diploma.cybersecplatform.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Modifier
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
import hse.diploma.cybersecplatform.ui.components.SearchBar
import hse.diploma.cybersecplatform.ui.components.cards.BaseCourseCard
import hse.diploma.cybersecplatform.ui.navigation.Screen
import hse.diploma.cybersecplatform.ui.screens.error.ErrorScreen
import hse.diploma.cybersecplatform.ui.state.AllCoursesState
import hse.diploma.cybersecplatform.ui.theme.CyberSecPlatformTheme

@Composable
fun HomeScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val viewModel: HomeScreenViewModel = viewModel(factory = LocalViewModelFactory.current)
    val searchQuery by viewModel.searchQuery.collectAsState()
    val allCoursesState by viewModel.allCoursesState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadCourses()
    }

    Column(
        modifier = modifier,
    ) {
        when (allCoursesState) {
            is AllCoursesState.Loading -> CoursesContentShimmer()

            is AllCoursesState.Success -> {
                val coursesUiState = (allCoursesState as AllCoursesState.Success).uiState

                SearchBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = viewModel::onSearchQueryChange,
                    enableFiltering = false,
                    modifier = Modifier.background(Color.White),
                )
                val coursesToShow =
                    coursesUiState.filteredCourses.ifEmpty {
                        coursesUiState.courses
                    }
                CoursesContent(coursesToShow, navController)
            }

            is AllCoursesState.Error -> {
                val errorType = (allCoursesState as AllCoursesState.Error).errorType
                ErrorScreen(errorType, onReload = { viewModel.loadCourses() })
            }
        }
    }
}

@Composable
fun CoursesContent(
    items: List<Course>,
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

        items(items) { item ->
            BaseCourseCard(
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
private fun CoursesContentShimmer(
    modifier: Modifier = Modifier,
    itemsCount: Int = 6,
) {
    val baseColor = colorResource(id = R.color.dialog_color)
    val shimmerHighlight = colorResource(id = R.color.button_enabled)

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        items(itemsCount) {
            CoursesCardShimmer(baseColor, shimmerHighlight)
        }
    }
}

@Composable
private fun CoursesCardShimmer(
    baseColor: Color,
    shimmerHighlight: Color,
) {
    ElevatedCard(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(140.dp),
        shape = RoundedCornerShape(24.dp),
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
                    .size(36.dp)
                    .placeholder(
                        visible = true,
                        color = baseColor,
                        highlight =
                            PlaceholderHighlight.shimmer(
                                highlightColor = shimmerHighlight,
                            ),
                    ),
            )
            Spacer(Modifier.height(18.dp))
            Box(
                Modifier
                    .fillMaxWidth(0.8f)
                    .height(16.dp)
                    .placeholder(
                        visible = true,
                        color = baseColor,
                        highlight =
                            PlaceholderHighlight.shimmer(
                                highlightColor = shimmerHighlight,
                            ),
                    ),
            )
            Spacer(Modifier.height(12.dp))
            Box(
                Modifier
                    .fillMaxWidth(0.5f)
                    .height(12.dp)
                    .placeholder(
                        visible = true,
                        color = baseColor,
                        highlight =
                            PlaceholderHighlight.shimmer(
                                highlightColor = shimmerHighlight,
                            ),
                    ),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    CyberSecPlatformTheme {
        HomeScreen(navController = rememberNavController())
    }
}
