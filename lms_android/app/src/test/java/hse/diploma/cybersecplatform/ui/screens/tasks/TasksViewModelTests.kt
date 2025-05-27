package hse.diploma.cybersecplatform.ui.screens.tasks

import androidx.compose.ui.text.input.TextFieldValue
import hse.diploma.cybersecplatform.domain.model.Task
import hse.diploma.cybersecplatform.mock.mockTasksItems
import hse.diploma.cybersecplatform.ui.model.Difficulty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TasksViewModelTests {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: TasksViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = TasksViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should contain all mock tasks`() =
        runTest {
            assertEquals(mockTasksItems, viewModel.tasks.value)
        }

    @Test
    fun `onSearchQueryChange should update search query and filter tasks`() =
        runTest {
            val query = "test"
            viewModel.onSearchQueryChange(TextFieldValue(query))

            assertEquals(TextFieldValue(query), viewModel.searchQuery.value)
            assertEquals(
                mockTasksItems.filter { it.description.contains(query, true) },
                viewModel.tasks.value,
            )
        }

    @Test
    fun `filterTaskByDifficulty should filter tasks by selected difficulties`() =
        runTest {
            val selectedDifficulties = listOf(Difficulty.EASY, Difficulty.MEDIUM)
            viewModel.filterTaskByDifficulty(selectedDifficulties)

            assertEquals(
                mockTasksItems.filter { task -> selectedDifficulties.contains(task.difficulty) },
                viewModel.tasks.value,
            )
        }

    @Test
    fun `filterTaskByDifficulty with empty list should show all tasks`() =
        runTest {
            viewModel.filterTaskByDifficulty(emptyList())
            assertEquals(emptyList<Task>(), viewModel.tasks.value)
        }

    @Test
    fun `resetFilters should restore all tasks`() =
        runTest {
            viewModel.filterTaskByDifficulty(listOf(Difficulty.HARD))
            viewModel.resetFilters()

            assertEquals(mockTasksItems, viewModel.tasks.value)
        }

    @Test
    fun `onSearchQueryChange should filter tasks by description`() =
        runTest {
            val query = "security"
            viewModel.onSearchQueryChange(TextFieldValue(query))

            assertEquals(
                mockTasksItems.filter { it.description.contains(query, true) },
                viewModel.tasks.value,
            )
        }

    @Test
    fun `onSearchQueryChange with empty query should show all tasks`() =
        runTest {
            viewModel.onSearchQueryChange(TextFieldValue("test"))
            viewModel.onSearchQueryChange(TextFieldValue(""))

            assertEquals(mockTasksItems, viewModel.tasks.value)
        }

    @Test
    fun `onSearchQueryChange should update searchQuery state`() =
        runTest {
            val query = "network"
            viewModel.onSearchQueryChange(TextFieldValue(query))

            assertEquals(TextFieldValue(query), viewModel.searchQuery.value)
        }
}
