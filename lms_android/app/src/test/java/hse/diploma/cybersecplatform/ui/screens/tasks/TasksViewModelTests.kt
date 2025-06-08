package hse.diploma.cybersecplatform.ui.screens.tasks

import androidx.compose.ui.text.input.TextFieldValue
import hse.diploma.cybersecplatform.domain.model.Course
import hse.diploma.cybersecplatform.domain.model.Task
import hse.diploma.cybersecplatform.domain.repository.CoursesRepo
import hse.diploma.cybersecplatform.ui.model.Difficulty
import hse.diploma.cybersecplatform.ui.model.VulnerabilityType
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
            vulnerabilityType = VulnerabilityType.XSS,
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
                        vulnerabilityType = VulnerabilityType.XSS,
                        number = 1,
                        difficulty = Difficulty.EASY,
                        type = "XSS",
                        points = 10,
                        isCompleted = false,
                    ),
                    Task(
                        id = 2,
                        courseId = 1,
                        title = "DOM XSS",
                        description = "DOM-based XSS attack",
                        content = "Exploit client-side rendering",
                        solution = "Решение задания...",
                        vulnerabilityType = VulnerabilityType.XSS,
                        number = 2,
                        difficulty = Difficulty.MEDIUM,
                        type = "XSS",
                        points = 20,
                        isCompleted = false,
                    ),
                    Task(
                        id = 3,
                        courseId = 1,
                        title = "CSRF Attack",
                        description = "Cross-site request forgery",
                        content = "Bypass CSRF protections",
                        solution = "Решение задания...",
                        vulnerabilityType = VulnerabilityType.CSRF,
                        number = 3,
                        difficulty = Difficulty.HARD,
                        type = "CSRF",
                        points = 30,
                        isCompleted = false,
                    ),
                ),
        )

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
    fun `when tasks are loaded for course, then tasks and originalTasks should be set`() =
        runTest {
            viewModel.loadTasksForCourse(1)

            advanceUntilIdle()

            assertEquals(testCourse.tasks, viewModel.tasks.first())
        }

    @Test
    fun `when search query changes, then tasks should be filtered by query`() =
        runTest {
            viewModel.loadTasksForCourse(1)

            advanceUntilIdle()

            viewModel.onSearchQueryChange(TextFieldValue("XSS"))

            assertEquals(TextFieldValue("XSS"), viewModel.searchQuery.first())
            assertEquals(
                testCourse.tasks.filter { it.title.contains("XSS") || it.description.contains("XSS") },
                viewModel.tasks.first(),
            )
        }

    @Test
    fun `when difficulty filter is applied, then tasks should be filtered by selected difficulties`() =
        runTest {
            viewModel.loadTasksForCourse(1)

            advanceUntilIdle()

            viewModel.filterTaskByDifficulty(listOf(Difficulty.EASY, Difficulty.MEDIUM))

            assertEquals(
                testCourse.tasks.filter { it.difficulty in listOf(Difficulty.EASY, Difficulty.MEDIUM) },
                viewModel.tasks.first(),
            )
        }

    @Test
    fun `when filters are reset, then all tasks should be restored and search cleared`() =
        runTest {
            viewModel.loadTasksForCourse(1)

            advanceUntilIdle()

            viewModel.onSearchQueryChange(TextFieldValue("XSS"))
            viewModel.filterTaskByDifficulty(listOf(Difficulty.HARD))

            viewModel.resetFilters()

            assertEquals(testCourse.tasks, viewModel.tasks.first())
            assertEquals(TextFieldValue(""), viewModel.searchQuery.first())
        }

    @Test
    fun `when search query is empty, then all tasks should be shown`() =
        runTest {
            viewModel.loadTasksForCourse(1)

            advanceUntilIdle()

            viewModel.onSearchQueryChange(TextFieldValue("XSS"))
            viewModel.onSearchQueryChange(TextFieldValue(""))

            assertEquals(testCourse.tasks, viewModel.tasks.first())
        }
}
