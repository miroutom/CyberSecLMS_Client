package hse.diploma.cybersecplatform.ui.screens.courses

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import hse.diploma.cybersecplatform.di.vm.LocalViewModelFactory
import hse.diploma.cybersecplatform.navigation.Screen

@Composable
fun MyCoursesScreenWrapper(
    viewModel: MyCoursesViewModel = viewModel(factory = LocalViewModelFactory.current),
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.myCoursesState.collectAsState()
    var isStartedSelected by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }

    MyCoursesScreen(
        state = state,
        isStartedSelected = isStartedSelected,
        onTabChange = { selected ->
            isStartedSelected = selected
        },
        onCourseClick = { vulnerabilityTypeName ->
            navController.navigate(Screen.TaskScreen.createRoute(vulnerabilityTypeName))
        },
        onRestartRequest = { course ->
            viewModel.selectCourseForRestart(course)
            showDialog = true
        },
        onRestartConfirm = { course ->
            viewModel.onCompletedCourseRestart(course)
            showDialog = false
        },
        onReload = { viewModel.loadCourses() },
        showDialog = showDialog,
        onDismissDialog = { showDialog = false },
        modifier = modifier,
    )
}
