package hse.diploma.cybersecplatform.ui.screens.code_editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import hse.diploma.cybersecplatform.di.vm.LocalViewModelFactory
import hse.diploma.cybersecplatform.ui.screens.loading.LoadingScreen

@Composable
fun CodeEditorScreenWrapper(
    taskId: Int,
    courseId: Int,
    viewModel: CodeEditorViewModel = viewModel(factory = LocalViewModelFactory.current),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(courseId, taskId) {
        viewModel.loadTask(courseId, taskId)
    }

    if (uiState.task != null) {
        CodeEditorScreen(
            task = uiState.task!!,
            code = uiState.code,
            isLoading = uiState.isLoading,
            isSubmitting = uiState.isSubmitting,
            lastResult = uiState.lastResult,
            onCodeChange = viewModel::updateCode,
            onSubmit = { viewModel.submitSolution() },
        )
    } else if (uiState.isLoading) {
        LoadingScreen()
    }
}
