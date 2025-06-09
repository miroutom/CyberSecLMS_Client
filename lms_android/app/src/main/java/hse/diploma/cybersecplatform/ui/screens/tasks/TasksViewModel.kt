package hse.diploma.cybersecplatform.ui.screens.tasks

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hse.diploma.cybersecplatform.domain.model.Task
import hse.diploma.cybersecplatform.domain.repository.CoursesRepo
import hse.diploma.cybersecplatform.ui.model.Difficulty
import hse.diploma.cybersecplatform.utils.logD
import hse.diploma.cybersecplatform.utils.logE
import hse.diploma.cybersecplatform.utils.toDifficulty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class TasksViewModel @Inject constructor(
    private val coursesRepo: CoursesRepo,
) : ViewModel() {
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks = _tasks.asStateFlow()

    private val _searchQuery = MutableStateFlow(TextFieldValue(""))
    val searchQuery = _searchQuery.asStateFlow()

    private var courseId: Int = -1
    private var originalTasks: List<Task> = emptyList()

    fun loadTasksForCourse(courseId: Int) {
        this.courseId = courseId
        viewModelScope.launch {
            logD(TAG, "Loading tasks for course: $courseId")
            coursesRepo.getCourseById(courseId).onSuccess { course ->
                originalTasks =
                    course.tasks.map { task ->
                        Task(
                            id = task.id,
                            courseId = task.courseId,
                            title = task.title,
                            description = task.description,
                            content = task.content,
                            solution = task.solution,
                            vulnerabilityType = course.vulnerabilityType,
                            number = task.number,
                            difficulty = task.difficulty,
                            points = task.points,
                            isCompleted = task.isCompleted,
                            type = "type",
                            language = "javascript",
                        )
                    }
                _tasks.value = originalTasks
                logD(TAG, "Loaded ${originalTasks.joinToString(" ")} tasks for course ${course.id}")
            }.onFailure { e ->
                logE(TAG, "Failed to load tasks for course $courseId", e)
                _tasks.value = emptyList()
            }
        }
    }

    fun onSearchQueryChange(newSearchQuery: TextFieldValue) {
        _searchQuery.value = newSearchQuery
        searchForTask(newSearchQuery.text)
    }

    fun filterTaskByDifficulty(selectedDifficulties: List<Difficulty>) {
        logD(TAG, "filterTaskByDifficulty: ${selectedDifficulties.joinToString(" ")}")
        _tasks.value =
            if (selectedDifficulties.isEmpty()) {
                originalTasks
            } else {
                originalTasks.filter { task ->
                    selectedDifficulties.contains(task.difficulty.toDifficulty())
                }
            }
    }

    fun resetFilters() {
        logD(TAG, "resetFilters")
        _tasks.value = originalTasks
        _searchQuery.value = TextFieldValue("")
    }

    private fun searchForTask(query: String) {
        logD(TAG, "searchForTask: $query")
        _tasks.value =
            if (query.isEmpty()) {
                originalTasks
            } else {
                originalTasks.filter {
                    it.description.contains(query, true) ||
                        it.title.contains(query, true)
                }
            }
    }

    companion object {
        private const val TAG = "TasksViewModel"
    }
}
