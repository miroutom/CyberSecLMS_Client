package hse.diploma.cybersecplatform.ui.screens.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import hse.diploma.cybersecplatform.model.Task
import hse.diploma.cybersecplatform.model.VulnerabilityType
import hse.diploma.cybersecplatform.ui.components.cards.TaskCard
import hse.diploma.cybersecplatform.utils.mock.mockTasksItems

@Composable
fun TasksScreen(
    vulnerabilityType: VulnerabilityType,
    modifier: Modifier = Modifier,
) {
    // TODO: replace with real data
    TasksContent(mockTasksItems, vulnerabilityType, modifier)
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
