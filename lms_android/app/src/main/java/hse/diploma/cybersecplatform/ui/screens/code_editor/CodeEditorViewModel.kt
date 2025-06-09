package hse.diploma.cybersecplatform.ui.screens.code_editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hse.diploma.cybersecplatform.data.model.submission.SubmissionResult
import hse.diploma.cybersecplatform.domain.model.Task
import hse.diploma.cybersecplatform.domain.repository.CoursesRepo
import hse.diploma.cybersecplatform.domain.repository.TasksRepo
import hse.diploma.cybersecplatform.utils.logD
import hse.diploma.cybersecplatform.utils.logE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class CodeEditorViewModel @Inject constructor(
    private val tasksRepo: TasksRepo,
    private val coursesRepo: CoursesRepo,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CodeEditorUiState())
    val uiState: StateFlow<CodeEditorUiState> = _uiState.asStateFlow()

    private val _isTaskCompleted = MutableStateFlow(false)
    val isTaskCompleted: StateFlow<Boolean> = _isTaskCompleted.asStateFlow()

    private var currentTask: Task? = null

    fun loadTask(
        courseId: Int,
        taskId: Int,
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            coursesRepo.getCourseById(courseId).onSuccess { course ->
                val task = course.tasks.firstOrNull { it.id == taskId }
                if (task != null) {
                    currentTask = task
                    _uiState.value =
                        CodeEditorUiState(
                            task = task,
                            code = task.content,
                            isLoading = false,
                        )
                    logD(TAG, "Task loaded: ${task.title}")
                } else {
                    logE(TAG, "Task with ID $taskId not found in course $courseId", Throwable())
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            error = "Task not found",
                        )
                }
            }.onFailure { e ->
                logE(TAG, "Failed to load task $taskId", e)
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to load task",
                    )
            }
        }
    }

    fun updateCode(newCode: String) {
        _uiState.value = _uiState.value.copy(code = newCode)
    }

    fun submitSolution() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true)

            val currentCode = _uiState.value.code
            val task = _uiState.value.task

            if (task != null) {
                tasksRepo.submitTaskSolution(task, currentCode).onSuccess { response ->
                    _uiState.value =
                        _uiState.value.copy(
                            lastResult =
                                SubmissionResult(
                                    response.status == "Completed",
                                    if (response.status == "Completed") 10 else 0,
                                    response.message,
                                ),
                        )

                    if (response.status == "Completed") {
                        tasksRepo.markTaskAsCompleted(task.id).onSuccess {
                            _isTaskCompleted.value = true
                        }.onFailure { e ->
                            _uiState.value =
                                _uiState.value.copy(
                                    error = "Failed to mark task as completed",
                                )
                        }
                    }
                }.onFailure { e ->
                    _uiState.value =
                        _uiState.value.copy(
                            error = "Submission failed: ${e.message}",
                        )
                }
            }

            _uiState.value = _uiState.value.copy(isSubmitting = false)
        }
    }

    companion object {
        private const val TAG = "CodeEditorViewModel"
    }
}

data class CodeEditorUiState(
    val task: Task? = null,
    val code: String = "",
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val lastResult: SubmissionResult? = null,
    val error: String? = null,
)
