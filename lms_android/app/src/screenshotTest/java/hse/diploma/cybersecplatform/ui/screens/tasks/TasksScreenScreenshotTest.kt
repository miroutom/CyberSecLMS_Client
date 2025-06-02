package hse.diploma.cybersecplatform.ui.screens.tasks

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import hse.diploma.cybersecplatform.WithTheme
import hse.diploma.cybersecplatform.mock.mockTasksItems
import hse.diploma.cybersecplatform.ui.model.VulnerabilityType
import hse.diploma.cybersecplatform.ui.state.screen_state.TasksScreenState

@Composable
@Preview(showBackground = true, apiLevel = 30)
fun PreviewTasksScreen() =
    WithTheme {
        val state =
            TasksScreenState(
                tasks = mockTasksItems,
                searchQuery = TextFieldValue(""),
            )

        TasksScreen(
            state = state,
            vulnerabilityType = VulnerabilityType.SQL,
            onSearchQueryChange = {},
            onFilterClick = {},
            onFilterSelected = {},
            onDismissFilter = {},
            onClearFilters = {},
        )
    }
