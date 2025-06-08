package hse.diploma.cybersecplatform.ui.screens.code_editor

import hse.diploma.cybersecplatform.data.api.ApiService
import hse.diploma.cybersecplatform.data.model.response.TaskSubmissionResponse
import hse.diploma.cybersecplatform.data.model.user.UserData
import hse.diploma.cybersecplatform.domain.model.Task
import hse.diploma.cybersecplatform.domain.repository.TasksRepo
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class CodeEditorViewModelTests {
    private val testDispatcher = StandardTestDispatcher()
    private val tasksRepo: TasksRepo = mockk()
    private val apiService: ApiService = mockk()
    private val mockUserData =
        UserData(
            id = 1,
            username = "test",
            fullName = "Test User",
            email = "test@example.com",
        )

    private lateinit var viewModel: CodeEditorViewModel

    private val mockTask =
        Task(
            id = 1,
            courseId = 1,
            title = "Test Task",
            description = "Test Description",
            content = "console.log('hello');",
            solution = "console.log('hello');",
            vulnerabilityType = VulnerabilityType.XSS,
            number = 1,
            difficulty = Difficulty.EASY,
            type = "practice",
            points = 10,
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

        coEvery { apiService.getUserProfile() } returns Response.success(mockUserData)
        coEvery { tasksRepo.getTaskById(any(), any()) } returns Result.success(mockTask)

        viewModel = CodeEditorViewModel(tasksRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when loadTask is called successfully, then task is loaded`() =
        runTest {
            viewModel.loadTask(1, 1)
            advanceUntilIdle()

            val state = viewModel.uiState.first()
            assertFalse(state.isLoading)
            assertEquals(mockTask, state.task)
            assertEquals(mockTask.content, state.code)
        }

    @Test
    fun `when loadTask fails, then error state is set`() =
        runTest {
            coEvery { tasksRepo.getTaskById(any(), any()) } returns Result.failure(Exception("Failed"))

            viewModel.loadTask(1, 1)
            advanceUntilIdle()

            val state = viewModel.uiState.first()
            assertFalse(state.isLoading)
            assertEquals("Failed to load task", state.error)
        }

    @Test
    fun `when updateCode is called, then code in state is updated`() =
        runTest {
            val newCode = "new code"

            viewModel.updateCode(newCode)
            advanceUntilIdle()

            val state = viewModel.uiState.first()
            assertEquals(newCode, state.code)
        }

    @Test
    fun `when submitSolution is successful and correct, then task is marked as completed`() =
        runTest {
            coEvery { tasksRepo.submitTaskSolution(any(), any()) } returns Result.success(successSubmissionResponse)
            coEvery { tasksRepo.markTaskAsCompleted(any()) } returns Result.success(Unit)

            viewModel.loadTask(1, 1)
            advanceUntilIdle()

            viewModel.submitSolution()
            advanceUntilIdle()

            val state = viewModel.uiState.first()
            val isCompleted = viewModel.isTaskCompleted.first()

            assertFalse(state.isSubmitting)
            assertTrue(isCompleted)
        }

    @Test
    fun `when submitSolution is successful but incorrect, then task is not marked as completed`() =
        runTest {
            coEvery { tasksRepo.submitTaskSolution(any(), any()) } returns Result.success(failedSubmissionResponse)

            viewModel.loadTask(1, 1)
            advanceUntilIdle()

            viewModel.submitSolution()
            advanceUntilIdle()

            val state = viewModel.uiState.first()
            val isCompleted = viewModel.isTaskCompleted.first()

            assertFalse(state.isSubmitting)
            assertFalse(isCompleted)
        }

    @Test
    fun `when submitSolution fails, then error state is set`() =
        runTest {
            coEvery {
                tasksRepo.submitTaskSolution(any(), any())
            } returns Result.failure(Exception("Submission failed"))

            viewModel.loadTask(1, 1)
            advanceUntilIdle()

            viewModel.submitSolution()
            advanceUntilIdle()

            val state = viewModel.uiState.first()
            assertFalse(state.isSubmitting)
            assertTrue(state.error?.contains("Submission failed") == true)
        }

    @Test
    fun `when marking task as completed fails, then error state is set`() =
        runTest {
            coEvery { tasksRepo.submitTaskSolution(any(), any()) } returns Result.success(successSubmissionResponse)
            coEvery { tasksRepo.markTaskAsCompleted(any()) } returns Result.failure(Exception("Mark failed"))

            viewModel.loadTask(1, 1)
            advanceUntilIdle()

            viewModel.submitSolution()
            advanceUntilIdle()

            val state = viewModel.uiState.first()
            assertFalse(state.isSubmitting)
            assertTrue(state.error?.contains("Failed to mark task as completed") == true)
        }
}
