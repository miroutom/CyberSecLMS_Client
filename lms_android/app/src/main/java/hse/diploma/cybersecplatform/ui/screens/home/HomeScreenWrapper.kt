package hse.diploma.cybersecplatform.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import hse.diploma.cybersecplatform.di.vm.LocalViewModelFactory

@Composable
fun HomeScreenWrapper(
    viewModel: HomeViewModel = viewModel(factory = LocalViewModelFactory.current),
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.allCoursesState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    HomeScreen(
        state = state,
        searchQuery = searchQuery,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onCourseClick = { vulnerabilityType ->
            navController.navigate("task/${vulnerabilityType}")
        },
        onReload = { viewModel.loadCourses() },
        modifier = modifier,
    )
}
