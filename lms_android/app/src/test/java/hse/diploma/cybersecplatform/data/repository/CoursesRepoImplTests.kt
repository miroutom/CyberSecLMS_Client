package hse.diploma.cybersecplatform.data.repository

import hse.diploma.cybersecplatform.data.api.ApiService
import hse.diploma.cybersecplatform.data.model.response.AllCoursesResponse
import hse.diploma.cybersecplatform.data.model.response.MyCoursesResponse
import hse.diploma.cybersecplatform.mock.mockAllCourses
import hse.diploma.cybersecplatform.mock.mockCourses
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
    private lateinit var coursesRepo: CoursesRepoImpl

    @Before
    fun setup() {
        coursesRepo = CoursesRepoImpl(apiService)
    }

    @Test
    fun `getAllCourses should return success with mock data`() =
        runTest {
            coEvery { apiService.getAllCourses() } returns Response.success(AllCoursesResponse(mockAllCourses))

            val result = coursesRepo.getAllCourses()

            assertTrue(result.isSuccess)
            assertEquals(mockAllCourses, result.getOrNull())
        }

    @Test
    fun `getAllCourses should return failure when response fails`() =
        runTest {
            coEvery { apiService.getAllCourses() } returns Response.error(400, "Error".toResponseBody())

            val result = coursesRepo.getAllCourses()

            assertTrue(result.isFailure)
            assertEquals("Error", result.exceptionOrNull()?.message)
        }

    @Test
    fun `getMyCourses should return success with mock data`() =
        runTest {
            coEvery { apiService.getMyCourses() } returns Response.success(MyCoursesResponse(mockCourses))

            val result = coursesRepo.getMyCourses()

            assertTrue(result.isSuccess)
            assertEquals(mockCourses, result.getOrNull())
        }

    @Test
    fun `getMyCourses should return failure when response fails`() =
        runTest {
            coEvery { apiService.getMyCourses() } returns Response.error(400, "Error".toResponseBody())

            val result = coursesRepo.getMyCourses()

            assertTrue(result.isFailure)
            assertEquals("Error", result.exceptionOrNull()?.message)
        }
}
