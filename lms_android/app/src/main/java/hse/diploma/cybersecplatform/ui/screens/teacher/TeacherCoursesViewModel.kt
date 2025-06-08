package hse.diploma.cybersecplatform.ui.screens.teacher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hse.diploma.cybersecplatform.data.model.request.CreateCourseRequest
import hse.diploma.cybersecplatform.data.model.request.CreateTaskRequest
import hse.diploma.cybersecplatform.data.model.request.UpdateCourseRequest
import hse.diploma.cybersecplatform.data.model.request.UpdateTaskRequest
import hse.diploma.cybersecplatform.domain.error.ErrorType
import hse.diploma.cybersecplatform.domain.model.Course
import hse.diploma.cybersecplatform.domain.model.Task
import hse.diploma.cybersecplatform.domain.repository.CoursesRepo
import hse.diploma.cybersecplatform.domain.repository.UserRepo
import hse.diploma.cybersecplatform.extensions.toErrorType
import hse.diploma.cybersecplatform.ui.state.shared.ActionState
import hse.diploma.cybersecplatform.ui.state.shared.TeacherCoursesState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class TeacherCoursesViewModel @Inject constructor(
    private val coursesRepo: CoursesRepo,
    private val userRepo: UserRepo,
) : ViewModel() {
    private val _state = MutableStateFlow<TeacherCoursesState>(TeacherCoursesState.Loading)
    val state: StateFlow<TeacherCoursesState> = _state.asStateFlow()

    private val _actionState = MutableStateFlow<ActionState>(ActionState.Idle)
    val actionState: StateFlow<ActionState> = _actionState.asStateFlow()

    private val _selectedCourse = MutableStateFlow<Course?>(null)
    val selectedCourse: StateFlow<Course?> = _selectedCourse.asStateFlow()

    private val _selectedTask = MutableStateFlow<Task?>(null)
    val selectedTask: StateFlow<Task?> = _selectedTask.asStateFlow()

    private val _isTeacher = MutableStateFlow(false)
    val isTeacher: StateFlow<Boolean> = _isTeacher.asStateFlow()

    init {
        checkTeacherStatus()
    }

    private fun checkTeacherStatus() {
        viewModelScope.launch {
            try {
                val userProfile = userRepo.getUserProfile().getOrThrow()
                if (!userProfile.isTeacher) {
                    _state.value = TeacherCoursesState.Error(ErrorType.AccessDenied)
                    return@launch
                } else {
                    _isTeacher.value = true
                }
            } catch (e: Exception) {
                _state.value = TeacherCoursesState.Error(e.toErrorType(TAG))
            }
        }
    }

    fun createCourse(
        title: String,
        description: String,
        vulnerabilityType: String,
    ) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            try {
                val request =
                    CreateCourseRequest(
                        title = title,
                        description = description,
                        difficultyLevel = "medium",
                        category = vulnerabilityType,
                    )
                val result = coursesRepo.createCourse(request)

                handleCourseActionResult(result, "Course created successfully") { newCourse ->
                    val currentState = _state.value
                    if (currentState is TeacherCoursesState.Success) {
                        _state.value =
                            currentState.copy(
                                courses = currentState.courses + newCourse,
                            )
                    }
                }
            } catch (e: Exception) {
                _actionState.value = ActionState.Error(e.toErrorType(TAG))
            }
        }
    }

    fun updateCourse(
        courseId: Int,
        title: String,
        description: String,
        vulnerabilityType: String,
    ) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            try {
                val request =
                    UpdateCourseRequest(
                        title = title,
                        description = description,
                        difficultyLevel = "medium",
                        category = vulnerabilityType,
                    )
                val result = coursesRepo.updateCourse(courseId, request)

                handleCourseActionResult(result, "Course updated successfully") { updatedCourse ->
                    _selectedCourse.value = updatedCourse
                }
            } catch (e: Exception) {
                _actionState.value = ActionState.Error(e.toErrorType(TAG))
            }
        }
    }

    fun deleteCourse(courseId: Int) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            try {
                val result = coursesRepo.deleteCourse(courseId)
                result.onSuccess {
                    _actionState.value = ActionState.Success(it.message)
                }.onFailure { e ->
                    _actionState.value = ActionState.Error(e.toErrorType(TAG))
                }
            } catch (e: Exception) {
                _actionState.value = ActionState.Error(e.toErrorType(TAG))
            }
        }
    }

    private fun handleCourseActionResult(
        result: Result<Course>,
        successMessage: String,
        onSuccess: (Course) -> Unit,
    ) {
        result.onSuccess { course ->
            _actionState.value = ActionState.Success(successMessage)
            onSuccess(course)
        }.onFailure { e ->
            _actionState.value = ActionState.Error(e.toErrorType(TAG))
        }
    }

    fun createTask(
        courseId: Int,
        title: String,
        description: String,
        type: String,
        points: Int,
        content: String,
    ) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            try {
                val request =
                    CreateTaskRequest(
                        title = title,
                        description = description,
                        type = type,
                        points = points,
                        content = content,
                    )
                val result = coursesRepo.createTask(courseId, request)

                handleTaskActionResult(result, "Task created successfully") { task ->
                    selectedCourse.value?.let { course ->
                        _selectedCourse.value = course.copy(tasks = course.tasks + task)
                    }
                }
            } catch (e: Exception) {
                _actionState.value = ActionState.Error(e.toErrorType(TAG))
            }
        }
    }

    fun updateTask(
        courseId: Int,
        taskId: Int,
        title: String,
        description: String,
        type: String,
        points: Int,
        content: String,
    ) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            try {
                val request =
                    UpdateTaskRequest(
                        title = title,
                        description = description,
                        type = type,
                        points = points,
                        content = content,
                    )
                val result = coursesRepo.updateTask(courseId, taskId, request)

                handleTaskActionResult(result, "Task updated successfully") { updatedTask ->
                    selectedCourse.value?.let { course ->
                        _selectedCourse.value =
                            course.copy(
                                tasks = course.tasks.map { if (it.id == taskId) updatedTask else it },
                            )
                    }
                }
            } catch (e: Exception) {
                _actionState.value = ActionState.Error(e.toErrorType(TAG))
            }
        }
    }

    fun deleteTask(
        courseId: Int,
        taskId: Int,
    ) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            try {
                val result = coursesRepo.deleteTask(courseId, taskId)

                handleTaskActionResult(result, "Task deleted successfully") {
                    selectedCourse.value?.let { course ->
                        _selectedCourse.value =
                            course.copy(
                                tasks = course.tasks.filter { it.id != taskId },
                            )
                    }
                }
            } catch (e: Exception) {
                _actionState.value = ActionState.Error(e.toErrorType(TAG))
            }
        }
    }

    private fun <T> handleTaskActionResult(
        result: Result<T>,
        successMessage: String,
        onSuccess: (T) -> Unit,
    ) {
        result.onSuccess {
            _actionState.value = ActionState.Success(successMessage)
            onSuccess(it)
        }.onFailure { e ->
            _actionState.value = ActionState.Error(e.toErrorType(TAG))
        }
    }

    fun selectCourse(course: Course?) {
        _selectedCourse.value = course
        _selectedTask.value = null
    }

    fun selectTask(task: Task?) {
        _selectedTask.value = task
    }

    fun resetActionState() {
        _actionState.value = ActionState.Idle
    }

    companion object {
        private const val TAG = "TeacherCoursesViewModel"
    }
}
