package hse.diploma.cybersecplatform.data.repository

import hse.diploma.cybersecplatform.data.api.ApiService
import hse.diploma.cybersecplatform.data.model.response.MessageResponse
import hse.diploma.cybersecplatform.data.model.response.TaskSubmissionResponse
import hse.diploma.cybersecplatform.mock.mockTasks
import hse.diploma.cybersecplatform.mock.mockUser
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.util.Date

class TasksRepoImplTests {
    private val apiService: ApiService = mockk()
    private lateinit var tasksRepo: TasksRepoImpl

    private val mockTask = mockTasks.first()
    private val mockSubmissionResponse =
        TaskSubmissionResponse(
            status = "Completed",
            message = "Success",
            submissionId = 1,
            submittedAt = Date().toString(),
            taskId = 1,
        )

    @Before
    fun setup() {
        tasksRepo = TasksRepoImpl(apiService)
    }

    @Test
    fun `when getTaskById is called with valid ids and API returns success, then return task`() =
        runTest {
            coEvery { apiService.getTaskById(any(), any()) } returns Response.success(mockTask)

            val result = tasksRepo.getTaskById(1, 1)

            assertTrue(result.isSuccess)
            assertEquals(mockTask, result.getOrNull())
        }

    @Test
    fun `when getTaskById is called and API returns error, then return failure`() =
        runTest {
            coEvery { apiService.getTaskById(any(), any()) } returns
                Response.error(404, "Not found".toResponseBody())

            val result = tasksRepo.getTaskById(1, 1)

            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull()?.message?.contains("Failed to load task") == true)
        }

    @Test
    fun `when getTaskById is called and API returns null body, then return failure`() =
        runTest {
            coEvery { apiService.getTaskById(any(), any()) } returns Response.success(null)

            val result = tasksRepo.getTaskById(1, 1)

            assertTrue(result.isFailure)
            assertEquals("Task data is null", result.exceptionOrNull()?.message)
        }

    @Test
    fun `when markTaskAsCompleted is called and user profile is available, then complete task successfully`() =
        runTest {
            coEvery { apiService.getUserProfile() } returns Response.success(mockUser)
            coEvery { apiService.completeTask(any(), any()) } returns Response.success(MessageResponse("msg"))

            val result = tasksRepo.markTaskAsCompleted(1)

            assertTrue(result.isSuccess)
        }

    @Test
    fun `when markTaskAsCompleted is called and user profile is null, then return failure`() =
        runTest {
            coEvery { apiService.getUserProfile() } returns Response.success(null)

            val result = tasksRepo.markTaskAsCompleted(1)

            assertTrue(result.isFailure)
            assertEquals("User data is null", result.exceptionOrNull()?.message)
        }

    @Test
    fun `when markTaskAsCompleted is called and profile request fails, then return failure`() =
        runTest {
            coEvery { apiService.getUserProfile() } returns
                Response.error(401, "Unauthorized".toResponseBody())

            val result = tasksRepo.markTaskAsCompleted(1)

            assertTrue(result.isFailure)
        }

    @Test
    fun `when markTaskAsCompleted is called and complete task fails, then return failure`() =
        runTest {
            coEvery { apiService.getUserProfile() } returns Response.success(mockUser)
            coEvery { apiService.completeTask(any(), any()) } returns
                Response.error(500, "Server error".toResponseBody())

            val result = tasksRepo.markTaskAsCompleted(1)

            assertTrue(result.isFailure)
        }

    @Test
    fun `when submitTaskSolution is called with valid data and API returns success, then return submission response`() =
        runTest {
            coEvery { apiService.getUserProfile() } returns Response.success(mockUser)
            coEvery { apiService.submitTask(any(), any(), any()) } returns
                Response.success(mockSubmissionResponse)

            val result = tasksRepo.submitTaskSolution(mockTask, "test code")

            assertTrue(result.isSuccess)
            assertEquals(mockSubmissionResponse, result.getOrNull())
        }

    @Test
    fun `when submitTaskSolution is called and user profile is null, then return failure`() =
        runTest {
            coEvery { apiService.getUserProfile() } returns Response.success(null)

            val result = tasksRepo.submitTaskSolution(mockTask, "test code")

            assertTrue(result.isFailure)
            assertEquals("User data is null", result.exceptionOrNull()?.message)
        }

    @Test
    fun `when submitTaskSolution is called and profile request fails, then return failure`() =
        runTest {
            coEvery { apiService.getUserProfile() } returns
                Response.error(401, "Unauthorized".toResponseBody())

            val result = tasksRepo.submitTaskSolution(mockTask, "test code")

            assertTrue(result.isFailure)
        }

    @Test
    fun `when submitTaskSolution is called and API returns error, then return failure`() =
        runTest {
            coEvery { apiService.getUserProfile() } returns Response.success(mockUser)
            coEvery { apiService.submitTask(any(), any(), any()) } returns
                Response.error(400, "Bad request".toResponseBody())

            val result = tasksRepo.submitTaskSolution(mockTask, "test code")

            assertTrue(result.isFailure)
        }

    @Test
    fun `when submitTaskSolution is called and API returns null body, then return failure`() =
        runTest {
            coEvery { apiService.getUserProfile() } returns Response.success(mockUser)
            coEvery { apiService.submitTask(any(), any(), any()) } returns Response.success(null)

            val result = tasksRepo.submitTaskSolution(mockTask, "test code")

            assertTrue(result.isFailure)
            assertEquals("Empty response body", result.exceptionOrNull()?.message)
        }
}
