package hse.diploma.cybersecplatform.ui.components.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import hse.diploma.cybersecplatform.utils.Difficulty

@Composable
fun FilterSelectionDialog(
    onFilterSelected: (List<Difficulty>) -> Unit,
    onDismiss: () -> Unit,
    onClearFilters: () -> Unit,
) {
    val difficulties = Difficulty.entries.toTypedArray()

    val selectedDifficulties = remember { mutableStateListOf<Difficulty>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Difficulty") },
        text = {
            Column {
                difficulties.forEach { difficulty ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp),
                    ) {
                        Checkbox(
                            checked = selectedDifficulties.contains(difficulty),
                            onCheckedChange = { isChecked ->
                                if (isChecked) {
                                    selectedDifficulties.add(difficulty)
                                } else {
                                    selectedDifficulties.remove(difficulty)
                                }
                            },
                        )
                        Text(difficulty.name)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onFilterSelected(selectedDifficulties.toList())
                onDismiss()
            }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = {
                selectedDifficulties.clear()
                onClearFilters()
            }) {
                Text("Reset")
            }
        },
    )
}
