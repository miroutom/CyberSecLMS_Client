package hse.diploma.cybersecplatform.ui.screens.tasks

import androidx.compose.ui.text.input.TextFieldValue
import hse.diploma.cybersecplatform.domain.model.Course
import hse.diploma.cybersecplatform.domain.model.Task
import hse.diploma.cybersecplatform.domain.repository.CoursesRepo
import hse.diploma.cybersecplatform.ui.model.Difficulty
import hse.diploma.cybersecplatform.ui.model.VulnerabilityType
import hse.diploma.cybersecplatform.utils.toDifficulty
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
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
    private val coursesRepo: CoursesRepo = mockk()
    private lateinit var viewModel: TasksViewModel

    private val testCourse =
        Course(
            id = 1,
            title = "Web Security",
            description = "Learn web vulnerabilities",
            vulnerabilityType = "XSS",
            difficultyLevel = "medium",
            category = "web",
            tasks =
                listOf(
                    Task(
                        id = 1,
                        courseId = 1,
                        title = "Basic XSS",
                        description = "Simple reflected XSS attack",
                        content = "Find XSS in search field",
                        solution = "Решение задания...",
                        vulnerabilityType = VulnerabilityType.XSS.name,
                        number = 1,
                        difficulty = "easy",
                        type = "type",
                        points = 10,
                        isCompleted = false,
                        language = "javascript",
                    ),
                    Task(
                        id = 2,
                        courseId = 1,
                        title = "DOM XSS",
                        description = "DOM-based XSS attack",
                        content = "Exploit client-side rendering",
                        solution = "Решение задания...",
                        vulnerabilityType = VulnerabilityType.XSS.name,
                        number = 2,
                        difficulty = "medium",
                        type = "type",
                        points = 20,
                        isCompleted = false,
                        language = "javascript",
                    ),
                ),
        )

    private val expectedTasks =
        testCourse.tasks.map {
            it.copy(vulnerabilityType = testCourse.vulnerabilityType)
        }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = TasksViewModel(coursesRepo)
        coEvery { coursesRepo.getCourseById(1) } returns Result.success(testCourse)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when view model is initialized, then tasks and search query should be empty`() =
        runTest {
            assertEquals(emptyList<Task>(), viewModel.tasks.first())
            assertEquals(TextFieldValue(""), viewModel.searchQuery.first())
        }

    @Test
    fun `when tasks are loaded for course, then tasks should contain vulnerabilityType from course`() =
        runTest {
            viewModel.loadTasksForCourse(1)
            advanceUntilIdle()

            assertEquals(expectedTasks, viewModel.tasks.first())
        }

    @Test
    fun `when search query changes, then tasks should be filtered by query`() =
        runTest {
            viewModel.loadTasksForCourse(1)
            advanceUntilIdle()

            viewModel.onSearchQueryChange(TextFieldValue("DOM"))

            assertEquals(TextFieldValue("DOM"), viewModel.searchQuery.first())
            assertEquals(
                expectedTasks.filter { it.title.contains("DOM") },
                viewModel.tasks.first(),
            )
        }

    @Test
    fun `when difficulty filter is applied, then tasks should be filtered by selected difficulties`() =
        runTest {
            viewModel.loadTasksForCourse(1)
            advanceUntilIdle()

            viewModel.filterTaskByDifficulty(listOf(Difficulty.MEDIUM))

            assertEquals(
                expectedTasks.filter { it.difficulty.toDifficulty() == Difficulty.MEDIUM },
                viewModel.tasks.first(),
            )
        }

    @Test
    fun `when filters are reset, then all tasks should be restored and search cleared`() =
        runTest {
            viewModel.loadTasksForCourse(1)
            advanceUntilIdle()

            viewModel.onSearchQueryChange(TextFieldValue("XSS"))
            viewModel.filterTaskByDifficulty(listOf(Difficulty.EASY))

            viewModel.resetFilters()

            assertEquals(expectedTasks, viewModel.tasks.first())
            assertEquals(TextFieldValue(""), viewModel.searchQuery.first())
        }

    @Test
    fun `when search query is empty, then all tasks should be shown`() =
        runTest {
            viewModel.loadTasksForCourse(1)
            advanceUntilIdle()

            viewModel.onSearchQueryChange(TextFieldValue("XSS"))
            viewModel.onSearchQueryChange(TextFieldValue(""))

            assertEquals(expectedTasks, viewModel.tasks.first())
        }
}
