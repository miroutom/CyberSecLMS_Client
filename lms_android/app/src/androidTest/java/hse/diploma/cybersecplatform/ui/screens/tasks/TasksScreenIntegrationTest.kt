package hse.diploma.cybersecplatform.ui.screens.tasks

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.mock.mockTasksItems
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
        viewModel = mockk()
        navController = mockk(relaxed = true)

        every { viewModel.tasks } returns MutableStateFlow(mockTasksItems)
        every { viewModel.searchQuery } returns MutableStateFlow(TextFieldValue(""))
        every { viewModel.onSearchQueryChange(any()) } answers {
            val query = firstArg<TextFieldValue>().text
            every { viewModel.tasks } returns
                MutableStateFlow(
                    mockTasksItems.filter { it.description.contains(query, true) },
                )
        }
    }

    private fun getString(resId: Int): String = context.getString(resId)

    @Test
    fun tasksScreen_displaysTasksForVulnerabilityType() {
        composeRule.setContent {
            TasksScreen(
                viewModel = viewModel,
                vulnerabilityType = testVulnerabilityType,
            )
        }

        mockTasksItems
            .filter { it.vulnerabilityType == testVulnerabilityType }
            .forEach { task ->
                composeRule.onNodeWithText(task.description)
                    .assertIsDisplayed()
            }
    }

    @Test
    fun searchFunctionality_filtersTasks() {
        composeRule.setContent {
            TasksScreen(
                viewModel = viewModel,
                vulnerabilityType = testVulnerabilityType,
            )
        }

        val testQuery = "test"
        composeRule.onNodeWithText(getString(R.string.search_bar_label))
            .performTextInput(testQuery)

        mockTasksItems
            .filter { it.description.contains(testQuery, true) }
            .forEach { task ->
                composeRule.onNodeWithText(task.description)
                    .assertIsDisplayed()
            }
    }

    @Test
    fun filterDialog_displaysWhenFilterButtonClicked() {
        composeRule.setContent {
            TasksScreen(
                viewModel = viewModel,
                vulnerabilityType = testVulnerabilityType,
            )
        }

        composeRule.onNodeWithContentDescription("Filter Icon")
            .performClick()

        composeRule.onNodeWithText(getString(R.string.filter_selection_dialog_title))
            .assertIsDisplayed()
    }
}
