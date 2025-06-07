package hse.diploma.cybersecplatform.ui.screens.teacher

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import hse.diploma.cybersecplatform.di.vm.LocalViewModelFactory

@Composable
fun TaskEditorScreenWrapper(
    navController: NavController,
    taskId: Int? = null,
    viewModel: TeacherCoursesViewModel = viewModel(factory = LocalViewModelFactory.current),
) {
    val selectedCourse by viewModel.selectedCourse.collectAsState()
    val task = if (taskId == null) null else selectedCourse?.tasks?.find { it.id == taskId }

    TaskEditorScreen(
        task = task,
        onSave = { updatedTask ->
            if (task == null) {
                viewModel.createTask(
                    courseId = viewModel.selectedCourse.value?.id ?: 0,
                    title = updatedTask.title,
                    description = updatedTask.description,
                    type = updatedTask.type,
                    points = updatedTask.points,
                    content = updatedTask.content,
                )
            } else {
                viewModel.updateTask(
                    courseId = viewModel.selectedCourse.value?.id ?: 0,
                    taskId = task.id,
                    title = updatedTask.title,
                    description = updatedTask.description,
                    type = updatedTask.type,
                    points = updatedTask.points,
                    content = updatedTask.content,
                )
            }
            navController.popBackStack()
        },
        onCancel = {
            navController.popBackStack()
        },
    )
}
