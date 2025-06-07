package hse.diploma.cybersecplatform.ui.screens.teacher

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import hse.diploma.cybersecplatform.di.vm.LocalViewModelFactory
import hse.diploma.cybersecplatform.navigation.Screen

@Composable
fun CourseEditorScreenWrapper(
    navController: NavController,
    viewModel: TeacherCoursesViewModel = viewModel(factory = LocalViewModelFactory.current),
) {
    val selectedCourse by viewModel.selectedCourse.collectAsState()

    CourseEditorScreen(
        course = selectedCourse,
        onSaveClick = { title, desc, type ->
            if (viewModel.selectedCourse.value == null) {
                viewModel.createCourse(title, desc, type)
            } else {
                viewModel.updateCourse(viewModel.selectedCourse.value?.id ?: 0, title, desc, type)
            }
            navController.popBackStack()
        },
        onDeleteClick = {
            viewModel.selectedCourse.value?.id?.let { viewModel.deleteCourse(it) }
            navController.popBackStack()
        },
        onTaskClick = { task ->
            viewModel.selectTask(task)
            navController.navigate(Screen.TaskEditor.createRoute(task.id))
        },
        onAddTaskClick = {
            navController.navigate(Screen.TaskEditor.createRoute())
        },
    )
}
