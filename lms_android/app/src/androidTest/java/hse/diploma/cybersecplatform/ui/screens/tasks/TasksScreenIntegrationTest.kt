package hse.diploma.cybersecplatform.ui.screens.tasks

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.mock.mockTasks
import hse.diploma.cybersecplatform.ui.model.VulnerabilityType
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TasksScreenIntegrationTest {
    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var viewModel: TasksViewModel
    private lateinit var navController: NavHostController
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val testVulnerabilityType = VulnerabilityType.XSS

    @Before
    fun setUp() {
        viewModel = mockk(relaxed = true)
        navController = mockk(relaxed = true)

        every { viewModel.tasks } returns MutableStateFlow(mockTasks)
        every { viewModel.searchQuery } returns MutableStateFlow(TextFieldValue(""))
        every { viewModel.onSearchQueryChange(any()) } answers {
            val query = firstArg<TextFieldValue>().text
            every { viewModel.tasks } returns
                MutableStateFlow(
                    mockTasks.filter { it.description.contains(query, true) },
                )
        }
        every { viewModel.loadTasksForCourse(any()) } returns Unit
    }

    private fun getString(resId: Int): String = context.getString(resId)

    @Test
    fun tasksScreen_displaysTasksForVulnerabilityType() {
        composeRule.setContent {
            TasksScreenWrapper(
                viewModel = viewModel,
                courseId = 1,
                rememberNavController(),
            )
        }

        mockTasks
            .filter { it.vulnerabilityType == testVulnerabilityType.name }
            .forEach { task ->
                composeRule.onNodeWithText(task.description)
                    .assertIsDisplayed()
            }
    }

    @Test
    fun searchFunctionality_filtersTasks() {
        composeRule.setContent {
            TasksScreenWrapper(
                viewModel = viewModel,
                courseId = 1,
                rememberNavController(),
            )
        }

        val testQuery = "test"
        composeRule.onNodeWithText(getString(R.string.search_bar_label))
            .performTextInput(testQuery)

        mockTasks
            .filter { it.description.contains(testQuery, true) }
            .forEach { task ->
                composeRule.onNodeWithText(task.description)
                    .assertIsDisplayed()
            }
    }

    @Test
    fun filterDialog_displaysWhenFilterButtonClicked() {
        composeRule.setContent {
            TasksScreenWrapper(
                viewModel = viewModel,
                courseId = 1,
                rememberNavController(),
            )
        }

        composeRule.onNodeWithContentDescription("Filter Icon")
            .performClick()

        composeRule.onNodeWithText(getString(R.string.filter_selection_dialog_title))
            .assertIsDisplayed()
    }
}
