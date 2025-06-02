package hse.diploma.cybersecplatform.ui.state.screen_state

import androidx.compose.ui.text.input.TextFieldValue
import hse.diploma.cybersecplatform.domain.model.Task

data class TasksScreenState(
    val tasks: List<Task> = emptyList(),
    val searchQuery: TextFieldValue = TextFieldValue(""),
    val showFilterDialog: Boolean = false,
)
