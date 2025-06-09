package hse.diploma.cybersecplatform.ui.screens.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.domain.model.Task
import hse.diploma.cybersecplatform.ui.components.buttons.FilledButton
import hse.diploma.cybersecplatform.ui.components.buttons.SegmentedButton
import hse.diploma.cybersecplatform.ui.components.textFields.EditorTextField
import hse.diploma.cybersecplatform.ui.model.Difficulty
import hse.diploma.cybersecplatform.ui.model.VulnerabilityType
import hse.diploma.cybersecplatform.ui.theme.Typography
import hse.diploma.cybersecplatform.utils.toDifficulty

@Composable
fun TaskEditorScreen(
    task: Task? = null,
    onSave: (Task) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var title by remember { mutableStateOf(task?.title ?: "") }
    var description by remember { mutableStateOf(task?.description ?: "") }
    var content by remember { mutableStateOf(task?.content ?: "") }
    var solution by remember { mutableStateOf(task?.solution ?: "") }
    var difficulty by remember { mutableStateOf(task?.difficulty ?: Difficulty.MEDIUM.name) }
    var type by remember { mutableStateOf(task?.vulnerabilityType ?: VulnerabilityType.XSS.name) }

    val difficulties = Difficulty.entries

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(colorResource(R.color.background))
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text =
                if (task == null) {
                    stringResource(R.string.create_task)
                } else {
                    stringResource(R.string.edit_task)
                },
            style = Typography.titleMedium,
            color = colorResource(R.color.main_text_color),
        )

        EditorTextField(
            value = TextFieldValue(title),
            onValueChange = { title = it.text },
            label = stringResource(R.string.task_title),
            modifier = Modifier.fillMaxWidth(),
        )

        EditorTextField(
            value = TextFieldValue(description),
            onValueChange = { description = it.text },
            label = stringResource(R.string.task_description),
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
        )

        Column {
            Text(
                text = stringResource(R.string.difficulty),
                style = Typography.titleMedium,
                color = colorResource(R.color.main_text_color),
            )
            Spacer(modifier = Modifier.height(8.dp))
            SegmentedButton(
                items = difficulties.map { it.name },
                selectedItem = difficulty,
                onItemSelected = { selected ->
                    difficulty = Difficulty.valueOf(selected).name
                },
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            FilledButton(
                text = stringResource(R.string.cancel_button),
                onClick = onCancel,
                modifier = Modifier.weight(1f),
            )

            FilledButton(
                text = stringResource(R.string.save_button),
                onClick = {
                    val newTask =
                        Task(
                            id = task?.id ?: 0,
                            courseId = task?.courseId ?: 0,
                            title = title,
                            description = description,
                            vulnerabilityType = type,
                            difficulty = difficulty,
                            number = task?.number ?: 0,
                            type = type,
                            points =
                                when (difficulty.toDifficulty()) {
                                    Difficulty.EASY -> 10
                                    Difficulty.MEDIUM -> 20
                                    Difficulty.HARD -> 30
                                },
                            content = content,
                            solution = solution,
                            isCompleted = false,
                        )
                    onSave(newTask)
                },
                modifier = Modifier.weight(1f),
                enabled = title.isNotBlank() && description.isNotBlank(),
            )
        }
    }
}

@Composable
@Preview(showBackground = true, apiLevel = 30)
fun TaskEditorScreenPreview() {
    TaskEditorScreen(onSave = {}, onCancel = {})
}
