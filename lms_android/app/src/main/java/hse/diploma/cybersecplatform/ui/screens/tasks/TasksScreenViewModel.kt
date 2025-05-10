package hse.diploma.cybersecplatform.ui.screens.tasks

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import hse.diploma.cybersecplatform.domain.model.Difficulty
import hse.diploma.cybersecplatform.mock.mockTasksItems
import hse.diploma.cybersecplatform.utils.logD
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class TasksScreenViewModel @Inject constructor() : ViewModel() {
    // TODO: replace with real data
    private val _tasks = MutableStateFlow(mockTasksItems)
    val tasks = _tasks.asStateFlow()

    private val _searchQuery = MutableStateFlow(TextFieldValue(""))
    val searchQuery = _searchQuery.asStateFlow()

    fun onSearchQueryChange(newSearchQuery: TextFieldValue) {
        _searchQuery.value = newSearchQuery
        searchForTask(newSearchQuery.text)
    }

    fun filterTaskByDifficulty(selectedDifficulties: List<Difficulty>) {
        logD(TAG, "filterTaskByDifficulty: ${selectedDifficulties.joinToString(" ")}")
        _tasks.value =
            mockTasksItems.filter { task ->
                selectedDifficulties.contains(task.difficulty)
            }
    }

    fun resetFilters() {
        logD(TAG, "resetFilters")
        _tasks.value = mockTasksItems
    }

    private fun searchForTask(query: String) {
        logD(TAG, "searchForTask: $query")
        _tasks.value = mockTasksItems.filter { it.description.contains(query, true) }
    }

    companion object {
        private const val TAG = "TasksScreenViewModel"
    }
}
