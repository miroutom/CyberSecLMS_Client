package hse.diploma.cybersecplatform.ui.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.domain.model.Task
import hse.diploma.cybersecplatform.navigation.Screen
import hse.diploma.cybersecplatform.ui.components.bars.SearchBar
import hse.diploma.cybersecplatform.ui.components.cards.TaskCard
import hse.diploma.cybersecplatform.ui.components.dialogs.FilterSelectionDialog
import hse.diploma.cybersecplatform.ui.model.Difficulty
import hse.diploma.cybersecplatform.ui.state.screen_state.TasksScreenState
import hse.diploma.cybersecplatform.ui.theme.CyberSecPlatformTheme

@Composable
fun TasksScreen(
    state: TasksScreenState,
    onSearchQueryChange: (TextFieldValue) -> Unit,
    onFilterClick: () -> Unit,
    onFilterSelected: (List<Difficulty>) -> Unit,
    onDismissFilter: () -> Unit,
    onClearFilters: () -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        SearchBar(
            searchQuery = state.searchQuery,
            onSearchQueryChange = onSearchQueryChange,
            onFilterClick = onFilterClick,
            modifier = Modifier.background(colorResource(R.color.background)),
        )

        TasksContent(
            items = state.tasks,
            navController = navController,
        )
    }

    if (state.showFilterDialog) {
        FilterSelectionDialog(
            onFilterSelected = { selectedDifficulties ->
                onFilterSelected(selectedDifficulties)
            },
            onDismiss = onDismissFilter,
            onClearFilters = onClearFilters,
        )
    }
}

@Composable
private fun TasksContent(
    items: List<Task>,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier =
            modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
    ) {
        items(items) { item ->
            TaskCard(
                task = item,
                onClick = { navController.navigate(Screen.CodeEditor.createRoute(item.courseId, item.id)) },
            )
        }

        item {
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
@Preview(name = "TasksScreen", showBackground = true, apiLevel = 30)
private fun TasksScreenPreview() {
    CyberSecPlatformTheme {
        TasksScreen(
            state = TasksScreenState(tasks = emptyList()),
            onSearchQueryChange = {},
            onFilterClick = {},
            onFilterSelected = {},
            onDismissFilter = {},
            onClearFilters = {},
            rememberNavController(),
        )
    }
}
