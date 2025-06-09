package hse.diploma.cybersecplatform.ui.screens.code_editor

import hse.diploma.cybersecplatform.data.model.response.TaskSubmissionResponse
import hse.diploma.cybersecplatform.domain.model.Course
import hse.diploma.cybersecplatform.domain.model.Task
import hse.diploma.cybersecplatform.domain.repository.CoursesRepo
import hse.diploma.cybersecplatform.domain.repository.TasksRepo
import hse.diploma.cybersecplatform.ui.model.Difficulty
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CodeEditorViewModelTests {
    private val testDispatcher = StandardTestDispatcher()
    private val tasksRepo: TasksRepo = mockk()
    private val coursesRepo: CoursesRepo = mockk()

    private lateinit var viewModel: CodeEditorViewModel

    private val mockTask =
        Task(
            id = 1,
            courseId = 1,
            title = "Test Task",
            description = "Test Description",
            content = "console.log('hello');",
            solution = "console.log('hello');",
            vulnerabilityType = "XSS",
            number = 1,
            difficulty = "easy",
            type = "practice",
            points = 10,
            isCompleted = false,
        )

    private val mockCourse =
        Course(
            id = 1,
            title = "Test Course",
            description = "Test Description",
            vulnerabilityType = "XSS",
            tasks = listOf(mockTask),
            difficultyLevel = Difficulty.EASY.name,
            category = "category",
        )

    private val successSubmissionResponse =
        TaskSubmissionResponse(
            status = "Completed",
            message = "Success",
            submissionId = 1,
            submittedAt = "2023-01-01",
            taskId = 1,
        )

    private val failedSubmissionResponse =
        TaskSubmissionResponse(
            status = "Failed",
            message = "Error",
            submissionId = 1,
            submittedAt = "2023-01-01",
            taskId = 1,
        )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        coEvery { coursesRepo.getCourseById(any()) } returns Result.success(mockCourse)
        coEvery { tasksRepo.submitTaskSolution(any(), any()) } returns Result.success(successSubmissionResponse)
        coEvery { tasksRepo.markTaskAsCompleted(any()) } returns Result.success(Unit)

        viewModel = CodeEditorViewModel(tasksRepo, coursesRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when loading task with valid ids then state contains task data`() =
        runTest {
            viewModel.loadTask(1, 1)
            advanceUntilIdle()

            val state = viewModel.uiState.first()
            assertEquals(mockTask, state.task)
            assertEquals(mockTask.content, state.code)
            assertFalse(state.isLoading)
        }

    @Test
    fun `when loading task fails then error state is set`() =
        runTest {
            coEvery { coursesRepo.getCourseById(any()) } returns Result.failure(Exception("DB error"))

            viewModel.loadTask(1, 1)
            advanceUntilIdle()

            val state = viewModel.uiState.first()
            assertEquals("Failed to load task", state.error)
        }

    @Test
    fun `when code is updated then state reflects new code`() =
        runTest {
            val newCode = "updated test code"

            viewModel.updateCode(newCode)

            assertEquals(newCode, viewModel.uiState.first().code)
        }

    @Test
    fun `when submitting correct solution then task is marked as completed`() =
        runTest {
            viewModel.loadTask(1, 1)
            advanceUntilIdle()

            viewModel.submitSolution()
            advanceUntilIdle()

            assertTrue(viewModel.isTaskCompleted.first())
            assertEquals("Success", viewModel.uiState.first().lastResult?.message)
        }

    @Test
    fun `when submitting incorrect solution then task remains uncompleted`() =
        runTest {
            coEvery {
                tasksRepo.submitTaskSolution(any(), any())
            } returns Result.success(failedSubmissionResponse)

            viewModel.loadTask(1, 1)
            advanceUntilIdle()

            viewModel.submitSolution()
            advanceUntilIdle()

            assertFalse(viewModel.isTaskCompleted.first())
            assertEquals("Error", viewModel.uiState.first().lastResult?.message)
        }

    @Test
    fun `when submission fails then error state is set`() =
        runTest {
            coEvery {
                tasksRepo.submitTaskSolution(any(), any())
            } returns Result.failure(Exception("Network error"))

            viewModel.loadTask(1, 1)
            advanceUntilIdle()

            viewModel.submitSolution()
            advanceUntilIdle()

            assertTrue(viewModel.uiState.first().error?.contains("Network error") == true)
        }

    @Test
    fun `when marking completion fails then error state is set`() =
        runTest {
            coEvery {
                tasksRepo.markTaskAsCompleted(any())
            } returns Result.failure(Exception("DB error"))

            viewModel.loadTask(1, 1)
            advanceUntilIdle()

            viewModel.submitSolution()
            advanceUntilIdle()

            assertTrue(viewModel.uiState.first().error?.contains("mark task as completed") == true)
        }
}
