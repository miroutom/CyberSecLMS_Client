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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import hse.diploma.cybersecplatform.di.vm.LocalViewModelFactory
import hse.diploma.cybersecplatform.domain.model.Task
import hse.diploma.cybersecplatform.domain.model.VulnerabilityType
import hse.diploma.cybersecplatform.ui.components.SearchBar
import hse.diploma.cybersecplatform.ui.components.cards.TaskCard
import hse.diploma.cybersecplatform.ui.components.dialogs.FilterSelectionDialog

@Composable
fun TasksScreen(
    vulnerabilityType: VulnerabilityType,
    viewModel: TasksScreenViewModel = viewModel(factory = LocalViewModelFactory.current),
    modifier: Modifier = Modifier,
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    var showFilterDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
    ) {
        SearchBar(
            searchQuery = searchQuery,
            onSearchQueryChange = viewModel::onSearchQueryChange,
            onFilterClick = { showFilterDialog = true },
            modifier = Modifier.background(Color.White),
        )

        TasksContent(tasks, vulnerabilityType)
    }

    if (showFilterDialog) {
        FilterSelectionDialog(
            onFilterSelected = { selectedDifficulties ->
                viewModel.filterTaskByDifficulty(selectedDifficulties)
                showFilterDialog = false
            },
            onDismiss = {
                showFilterDialog = false
            },
            onClearFilters = {
                showFilterDialog = false
                viewModel.resetFilters()
            },
        )
    }
}

@Composable
fun TasksContent(
    items: List<Task>,
    vulnerabilityType: VulnerabilityType,
    modifier: Modifier = Modifier,
) {
    val filteredItems = items.filter { it.vulnerabilityType == vulnerabilityType }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier =
            modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
    ) {
        items(filteredItems) { item ->
            TaskCard(
                task = item,
                onClick = { },
            )
        }

        item {
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
