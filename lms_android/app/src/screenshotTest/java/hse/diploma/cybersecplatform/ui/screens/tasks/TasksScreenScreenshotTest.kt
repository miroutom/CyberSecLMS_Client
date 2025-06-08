package hse.diploma.cybersecplatform.ui.screens.tasks

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import hse.diploma.cybersecplatform.WithTheme
import hse.diploma.cybersecplatform.mock.mockTasks
import hse.diploma.cybersecplatform.ui.state.screen_state.TasksScreenState

@Composable
@Preview(showBackground = true, apiLevel = 30)
fun PreviewTasksScreen() =
    WithTheme {
        val state =
            TasksScreenState(
                tasks = mockTasks,
                searchQuery = TextFieldValue(""),
            )

        TasksScreen(
            state = state,
            onSearchQueryChange = {},
            onFilterClick = {},
            onFilterSelected = {},
            onDismissFilter = {},
            onClearFilters = {},
            rememberNavController(),
        )
    }
