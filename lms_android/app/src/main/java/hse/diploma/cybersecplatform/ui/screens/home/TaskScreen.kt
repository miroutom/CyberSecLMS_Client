package hse.diploma.cybersecplatform.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import hse.diploma.cybersecplatform.model.Task
import hse.diploma.cybersecplatform.model.VulnerabilityType
import hse.diploma.cybersecplatform.ui.components.cards.TaskCard
import hse.diploma.cybersecplatform.utils.mock.mockTasksItems

@Composable
fun TaskScreen(
    vulnerabilityType: VulnerabilityType,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .padding(PaddingValues(0.dp))
                .fillMaxSize()
                .background(Color.White),
    ) {
        // TODO: replace with real data
        TasksContent(mockTasksItems, vulnerabilityType)
    }
}

@Composable
fun TasksContent(
    items: List<Task>,
    vulnerabilityType: VulnerabilityType,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth().padding(8.dp),
    ) {
        items.filter { it.vulnerabilityType == vulnerabilityType }.forEach { item ->
            item {
                TaskCard(
                    task = item,
                    onClick = { },
                )
            }
        }
    }
}
