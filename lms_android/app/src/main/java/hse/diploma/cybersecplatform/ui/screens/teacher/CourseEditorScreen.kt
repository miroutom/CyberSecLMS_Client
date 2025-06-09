package hse.diploma.cybersecplatform.ui.screens.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.domain.model.Course
import hse.diploma.cybersecplatform.domain.model.Task
import hse.diploma.cybersecplatform.ui.components.buttons.FilledButton
import hse.diploma.cybersecplatform.ui.components.cards.TaskCard
import hse.diploma.cybersecplatform.ui.components.textFields.EditorTextField
import hse.diploma.cybersecplatform.ui.theme.Typography
import hse.diploma.cybersecplatform.utils.toVulnerabilityType

@Composable
fun CourseEditorScreen(
    course: Course? = null,
    onSaveClick: (String, String, String) -> Unit,
    onDeleteClick: () -> Unit,
    onTaskClick: (Task) -> Unit,
    onAddTaskClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var title by remember { mutableStateOf(course?.title ?: "") }
    var description by remember { mutableStateOf(course?.description ?: "") }
    var vulnerabilityType by remember {
        mutableStateOf(course?.vulnerabilityType?.toVulnerabilityType()?.name ?: "")
    }

    val isSaveEnabled =
        title.isNotBlank() && description.isNotBlank() &&
            (course?.tasks?.isNotEmpty() == true || course == null)

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(colorResource(R.color.background))
                .padding(horizontal = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text =
                    if (course == null) {
                        stringResource(R.string.create_course)
                    } else {
                        stringResource(R.string.edit_course)
                    },
                style = Typography.titleMedium,
                color = colorResource(R.color.main_text_color),
            )

            IconButton(onClick = onDeleteClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_trash),
                    contentDescription = "Delete",
                    tint = colorResource(R.color.error_text_color),
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            EditorTextField(
                value = TextFieldValue(title),
                onValueChange = { title = it.text },
                label = stringResource(R.string.course_title),
                modifier = Modifier.fillMaxWidth(),
            )

            EditorTextField(
                value = TextFieldValue(description),
                onValueChange = { description = it.text },
                label = stringResource(R.string.course_description),
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
            )

            VulnerabilityTypeDropdown(
                selectedType = vulnerabilityType,
                onTypeSelected = { vulnerabilityType = it },
            )

            if (course?.tasks?.isNotEmpty() == true) {
                Text(
                    text = "Tasks:",
                    style = Typography.titleSmall,
                    color = colorResource(R.color.main_text_color),
                )
                EditableTaskList(
                    tasks = course.tasks,
                    onTaskClick = onTaskClick,
                )
            }
        }

        FilledButton(
            text = stringResource(R.string.save_button),
            onClick = { onSaveClick(title, description, vulnerabilityType) },
            enabled = isSaveEnabled,
        )

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.BottomEnd,
        ) {
            FloatingActionButton(
                onClick = onAddTaskClick,
                containerColor = colorResource(R.color.button_enabled),
                modifier = Modifier.padding(bottom = 72.dp),
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add task")
            }
        }
    }
}

@Composable
fun VulnerabilityTypeDropdown(
    selectedType: String,
    onTypeSelected: (String) -> Unit,
) {
    val vulnerabilityTypes = listOf("XSS", "CSRF", "SQL Injection")
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedType,
            onValueChange = {},
            label = {
                Text(
                    stringResource(R.string.vulnerability_type),
                    style = Typography.bodyMedium,
                    color = colorResource(R.color.main_text_color),
                )
            },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                Icon(painterResource(R.drawable.ic_expand_more), null, Modifier.clickable { expanded = true })
            },
            colors =
                TextFieldDefaults.colors(
                    focusedIndicatorColor = colorResource(R.color.button_enabled),
                    focusedContainerColor = colorResource(R.color.background),
                    focusedLabelColor = colorResource(R.color.button_enabled),
                    focusedTextColor = colorResource(R.color.main_text_color),
                    unfocusedLabelColor = colorResource(R.color.supporting_text),
                    unfocusedContainerColor = colorResource(R.color.background),
                ),
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = colorResource(R.color.xss_card_color),
        ) {
            vulnerabilityTypes.forEach { type ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = type,
                            style = Typography.bodyMedium,
                            color = colorResource(R.color.main_text_color),
                        )
                    },
                    onClick = {
                        onTypeSelected(type)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
fun EditableTaskList(
    tasks: List<Task>,
    onTaskClick: (Task) -> Unit,
) {
    LazyColumn {
        items(tasks) { task ->
            TaskCard(
                task = task,
                onClick = { onTaskClick(task) },
            )
        }
    }
}

@Composable
@Preview(showBackground = true, apiLevel = 30)
fun CourseEditorScreenPreview() {
    CourseEditorScreen(
        course = null,
        onSaveClick = { _, _, _ -> Unit },
        onDeleteClick = {},
        onTaskClick = {},
        onAddTaskClick = {},
    )
}
