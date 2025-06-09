package hse.diploma.cybersecplatform.ui.screens.code_editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.data.model.submission.SubmissionResult
import hse.diploma.cybersecplatform.domain.model.Task
import hse.diploma.cybersecplatform.mock.mockTasks
import hse.diploma.cybersecplatform.ui.components.bars.SubmissionResultBar
import hse.diploma.cybersecplatform.ui.components.buttons.SubmitButton
import hse.diploma.cybersecplatform.ui.screens.loading.LoadingScreen
import hse.diploma.cybersecplatform.ui.theme.Montserrat
import hse.diploma.cybersecplatform.utils.logD
import hse.diploma.cybersecplatform.utils.toVulnerabilityType

@Composable
fun CodeEditorScreen(
    task: Task,
    code: String,
    isLoading: Boolean,
    isSubmitting: Boolean,
    lastResult: SubmissionResult?,
    onCodeChange: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    logD("CodeEditorScreen", "code: $code")
    var activeTab by remember { mutableIntStateOf(0) }

    if (isLoading) {
        LoadingScreen()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = colorResource(R.color.background),
            contentColor = colorResource(R.color.button_enabled),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = {
                    Text(
                        text = stringResource(R.string.description_tab),
                        fontFamily = Montserrat,
                    )
                },
            )
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = {
                    Text(
                        text = stringResource(R.string.editor_tab),
                        fontFamily = Montserrat,
                    )
                },
            )
        }

        when (activeTab) {
            0 -> TaskDescriptionScreen(task)
            1 ->
                CodeEditorContent(
                    code = code,
                    onCodeChange = onCodeChange,
                    language = task.language,
                )
        }

        if (isSubmitting) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = colorResource(R.color.button_enabled),
            )
        }

        lastResult?.let { result ->
            SubmissionResultBar(result)
        }

        SubmitButton(
            onClick = onSubmit,
            enabled = !isSubmitting && lastResult == null,
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Composable
private fun TaskDescriptionScreen(task: Task) {
    SelectionContainer {
        Column(
            modifier =
                Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
        ) {
            Text(
                text = task.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Montserrat,
                color = colorResource(R.color.main_text_color),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = task.description,
                fontSize = 16.sp,
                fontFamily = Montserrat,
                color = colorResource(R.color.main_text_color),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.task_points, task.points),
                fontSize = 14.sp,
                color = colorResource(R.color.supporting_text),
                fontFamily = Montserrat,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text =
                    stringResource(
                        R.string.vulnerability_type,
                        task.vulnerabilityType.toVulnerabilityType().name,
                    ),
                fontSize = 14.sp,
                fontFamily = Montserrat,
                color = colorResource(R.color.main_text_color),
            )
        }
    }
}

@Composable
private fun CodeEditorContent(
    code: String,
    onCodeChange: (String) -> Unit,
    language: String,
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.DarkGray),
    ) {
        MonacoEditor(
            initialCode = code,
            language = language,
            onCodeChange = onCodeChange,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
@Preview(showBackground = true, apiLevel = 30)
fun CodeEditorScreenPreview() {
    CodeEditorScreen(
        task = mockTasks.first(),
        onSubmit = {},
        code = "",
        isLoading = false,
        isSubmitting = false,
        lastResult = null,
        onCodeChange = {},
    )
}
