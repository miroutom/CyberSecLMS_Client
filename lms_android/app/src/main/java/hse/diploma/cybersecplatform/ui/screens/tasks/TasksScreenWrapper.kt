package hse.diploma.cybersecplatform.ui.screens.tasks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import hse.diploma.cybersecplatform.di.vm.LocalViewModelFactory
import hse.diploma.cybersecplatform.ui.state.screen_state.TasksScreenState

@Composable
fun TasksScreenWrapper(
    viewModel: TasksViewModel = viewModel(factory = LocalViewModelFactory.current),
    courseId: Int,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val tasks by viewModel.tasks.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var showFilterDialog by remember { mutableStateOf(false) }

    LaunchedEffect(courseId) {
        viewModel.loadTasksForCourse(courseId)
    }

    val state =
        TasksScreenState(
            tasks = tasks,
            searchQuery = searchQuery,
            showFilterDialog = showFilterDialog,
        )

    TasksScreen(
        state = state,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onFilterClick = { showFilterDialog = true },
        onFilterSelected = { difficulties ->
            viewModel.filterTaskByDifficulty(difficulties)
            showFilterDialog = false
        },
        onDismissFilter = { showFilterDialog = false },
        onClearFilters = {
            viewModel.resetFilters()
            showFilterDialog = false
        },
        navController = navController,
        modifier = modifier,
    )
}
