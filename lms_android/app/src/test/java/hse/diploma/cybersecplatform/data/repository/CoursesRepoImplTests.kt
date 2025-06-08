package hse.diploma.cybersecplatform.data.repository

import hse.diploma.cybersecplatform.data.api.ApiService
import hse.diploma.cybersecplatform.data.model.request.CreateCourseRequest
import hse.diploma.cybersecplatform.data.model.request.CreateTaskRequest
import hse.diploma.cybersecplatform.data.model.request.UpdateCourseRequest
import hse.diploma.cybersecplatform.data.model.request.UpdateTaskRequest
import hse.diploma.cybersecplatform.data.model.response.MessageResponse
import hse.diploma.cybersecplatform.mock.mockAllCourses
import hse.diploma.cybersecplatform.mock.mockCourses
import hse.diploma.cybersecplatform.mock.mockTasks
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class CoursesRepoImplTests {
    private val apiService: ApiService = mockk()

    private val course = mockCourses.first()
    private val createCourseRequest =
        CreateCourseRequest(
            title = course.title,
            description = course.description,
            difficultyLevel = course.difficultyLevel,
            category = course.category,
        )
    private val updateCourseRequest =
        UpdateCourseRequest(
            title = mockCourses.last().title,
            description = mockCourses.last().description,
            difficultyLevel = mockCourses.last().difficultyLevel,
            category = mockCourses.last().category,
        )

    private val task = mockTasks.first()
    private val createTaskRequest =
        CreateTaskRequest(
            title = task.title,
            description = task.description,
            type = task.type,
            points = task.points,
            content = task.content,
        )
    private val updateTaskRequest =
        UpdateTaskRequest(
            title = mockTasks.last().title,
            description = mockTasks.last().description,
            type = mockTasks.last().type,
            points = mockTasks.last().points,
            content = mockTasks.last().content,
        )

    private lateinit var coursesRepo: CoursesRepoImpl

    @Before
    fun setup() {
        coursesRepo = CoursesRepoImpl(apiService)
    }

    @Test
    fun `when getAllCourses is called and API returns success, then return courses data`() =
        runTest {
            coEvery { apiService.getAllCourses() } returns
                Response.success(
                    mockAllCourses,
                )

            val result = coursesRepo.getAllCourses()

            assertTrue(result.isSuccess)
            assertEquals(mockAllCourses, result.getOrNull())
        }

    @Test
    fun `when getAllCourses is called and API fails, then return failure with error message`() =
        runTest {
            coEvery { apiService.getAllCourses() } returns Response.error(400, "Error".toResponseBody())

            val result = coursesRepo.getAllCourses()

            assertTrue(result.isFailure)
            assertEquals("Error", result.exceptionOrNull()?.message)
        }

    @Test
    fun `when getCourseById is called with valid ID, then return course data`() =
        runTest {

            coEvery { apiService.getCourseById(any()) } returns Response.success(course)

            val result = coursesRepo.getCourseById(1)

            assertTrue(result.isSuccess)
            assertEquals(course, result.getOrNull())
        }

    @Test
    fun `when createCourse is called with valid data, then return created course`() =
        runTest {
            val course = mockCourses.first()
            coEvery { apiService.createCourse(any()) } returns Response.success(course)

            val result = coursesRepo.createCourse(createCourseRequest)

            assertTrue(result.isSuccess)
            assertEquals(course, result.getOrNull())
        }

    @Test
    fun `when updateCourse is called with valid data, then return updated course`() =
        runTest {
            coEvery { apiService.updateCourse(any(), any()) } returns Response.success(course)

            val result = coursesRepo.updateCourse(1, updateCourseRequest)

            assertTrue(result.isSuccess)
            assertEquals(course, result.getOrNull())
        }

    @Test
    fun `when deleteCourse is called with valid ID, then return success message`() =
        runTest {
            val message = MessageResponse("Deleted")
            coEvery { apiService.deleteCourse(any()) } returns Response.success(message)

            val result = coursesRepo.deleteCourse(1)

            assertTrue(result.isSuccess)
            assertEquals(message, result.getOrNull())
        }

    @Test
    fun `when createTask is called with valid data, then return created task`() =
        runTest {
            coEvery { apiService.createTask(any(), any()) } returns Response.success(task)

            val result = coursesRepo.createTask(1, createTaskRequest)

            assertTrue(result.isSuccess)
            assertEquals(task, result.getOrNull())
        }

    @Test
    fun `when updateTask is called with valid data, then return updated task`() =
        runTest {
            coEvery { apiService.updateTask(any(), any(), any()) } returns Response.success(task)

            val result = coursesRepo.updateTask(1, 1, updateTaskRequest)

            assertTrue(result.isSuccess)
            assertEquals(task, result.getOrNull())
        }

    @Test
    fun `when deleteTask is called with valid IDs, then return success message`() =
        runTest {
            val message = MessageResponse("Task deleted")
            coEvery { apiService.deleteTask(any(), any()) } returns Response.success(message)

            val result = coursesRepo.deleteTask(1, 1)

            assertTrue(result.isSuccess)
            assertEquals(message, result.getOrNull())
        }
}
