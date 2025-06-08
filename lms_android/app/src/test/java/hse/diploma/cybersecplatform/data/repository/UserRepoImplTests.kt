package hse.diploma.cybersecplatform.data.repository

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import hse.diploma.cybersecplatform.data.api.ApiService
import hse.diploma.cybersecplatform.data.model.response.MessageResponse
import hse.diploma.cybersecplatform.data.model.user.UserProgress
import hse.diploma.cybersecplatform.mock.mockStats
import hse.diploma.cybersecplatform.mock.mockUser
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.ByteArrayInputStream

@OptIn(ExperimentalCoroutinesApi::class)
class UserRepoImplTests {
    private val testDispatcher = StandardTestDispatcher()
    private val apiService: ApiService = mockk()
    private val contentResolver: ContentResolver = mockk()

    private val mockUserProgress =
        UserProgress(
            userId = 1,
            completed =
                mapOf(
                    1 to true,
                    2 to false,
                    3 to true,
                ),
        )

    private lateinit var userRepo: UserRepoImpl

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        userRepo = UserRepoImpl(apiService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when getUserProfile is called and API returns success, then return user data`() =
        runTest {
            coEvery { apiService.getUserProfile() } returns Response.success(mockUser)

            val result = userRepo.getUserProfile()

            assertTrue(result.isSuccess)
            assertEquals(mockUser, result.getOrNull())
        }

    @Test
    fun `when getUserProfile is called and API returns error, then return failure`() =
        runTest {
            coEvery { apiService.getUserProfile() } returns Response.error(400, "Error".toResponseBody())

            val result = userRepo.getUserProfile()

            assertTrue(result.isFailure)
            assertNotNull(result.exceptionOrNull())
        }

    @Test
    fun `when updateProfile is called with valid data, then return success message`() =
        runTest {
            val mockMessage = MessageResponse("Profile updated")
            coEvery { apiService.updateProfile(any()) } returns Response.success(mockMessage)

            val result = userRepo.updateProfile(mockUser)

            assertTrue(result.isSuccess)
            assertEquals(mockMessage, result.getOrNull())
        }

    @Test
    fun `when updateProfile fails, then return failure`() =
        runTest {
            coEvery { apiService.updateProfile(any()) } returns Response.error(400, "Error".toResponseBody())

            val result = userRepo.updateProfile(mockUser)

            assertTrue(result.isFailure)
            assertNotNull(result.exceptionOrNull())
        }

    @Test
    fun `when getUserProgress is called and API returns success, then return progress data`() =
        runTest {
            coEvery { apiService.getUserProgress(any()) } returns Response.success(mockUserProgress)

            val result = userRepo.getUserProgress(1)

            assertTrue(result.isSuccess)
            assertEquals(mockUserProgress, result.getOrNull())
        }

    @Test
    fun `when getUserProgress fails, then return failure`() =
        runTest {
            coEvery { apiService.getUserProgress(any()) } returns Response.error(400, "Error".toResponseBody())

            val result = userRepo.getUserProgress(1)

            assertTrue(result.isFailure)
            assertNotNull(result.exceptionOrNull())
        }

    @Test
    fun `when completeTask is called and API returns success, then return success message`() =
        runTest {
            val mockMessage = MessageResponse("Task completed")
            coEvery { apiService.completeTask(any(), any()) } returns Response.success(mockMessage)

            val result = userRepo.completeTask(1, 1)

            assertTrue(result.isSuccess)
            assertEquals(mockMessage, result.getOrNull())
        }

    @Test
    fun `when completeTask fails, then return failure`() =
        runTest {
            coEvery { apiService.completeTask(any(), any()) } returns Response.error(400, "Error".toResponseBody())

            val result = userRepo.completeTask(1, 1)

            assertTrue(result.isFailure)
            assertNotNull(result.exceptionOrNull())
        }

    @Test
    fun `when uploadAvatar is called and stream cannot be opened, then return failure`() =
        runTest {
            val mockUri = mockk<Uri>()
            every { contentResolver.openInputStream(mockUri) } returns null

            val result = userRepo.uploadAvatar(mockUri, contentResolver)

            assertTrue(result.isFailure)
            assertEquals("Can't open image stream", result.exceptionOrNull()?.message)
        }

    @Test
    fun `when uploadAvatar fails to upload, then return failure`() =
        runTest {
            val mockUri = mockk<Uri>()
            val mockInputStream = ByteArrayInputStream(ByteArray(10))

            every { contentResolver.openInputStream(mockUri) } returns mockInputStream
            every { contentResolver.getType(mockUri) } returns "image/jpeg"
            every { contentResolver.query(mockUri, null, null, null, null) } returns
                mockk {
                    every { moveToFirst() } returns true
                    every { getColumnIndex(OpenableColumns.DISPLAY_NAME) } returns 0
                    every { getString(0) } returns "avatar.jpg"
                    every { close() } returns Unit
                }

            coEvery { apiService.uploadAvatar(any()) } returns Response.error(400, "Error".toResponseBody())

            val result = userRepo.uploadAvatar(mockUri, contentResolver)

            assertTrue(result.isFailure)
            assertNotNull(result.exceptionOrNull())
        }

    @Test
    fun `when getUserStatistics is called and API returns success, then return statistics`() =
        runTest {
            coEvery { apiService.getUserStatistics(any()) } returns Response.success(mockStats)

            val result = userRepo.getUserStatistics(1)

            assertTrue(result.isSuccess)
            assertEquals(mockStats, result.getOrNull())
        }

    @Test
    fun `when getUserStatistics fails, then return failure`() =
        runTest {
            coEvery { apiService.getUserStatistics(any()) } returns Response.error(400, "Error".toResponseBody())

            val result = userRepo.getUserStatistics(1)

            assertTrue(result.isFailure)
            assertNotNull(result.exceptionOrNull())
        }

    @Test
    fun `when getFileName, then return correct name for content URI`() {
        val mockUri =
            mockk<Uri> {
                every { scheme } returns ContentResolver.SCHEME_CONTENT
            }
        val mockCursor =
            mockk<android.database.Cursor> {
                every { moveToFirst() } returns true
                every { getColumnIndex(OpenableColumns.DISPLAY_NAME) } returns 0
                every { getString(0) } returns "test.jpg"
                every { close() } returns Unit
            }

        every { contentResolver.query(mockUri, null, null, null, null) } returns mockCursor

        val fileName = userRepo.getFileName(mockUri, contentResolver)

        assertEquals("test.jpg", fileName)
    }

    @Test
    fun `when getFileName, then return null for unsupported scheme`() {
        val mockUri =
            mockk<Uri> {
                every { scheme } returns "unknown"
            }

        val fileName = userRepo.getFileName(mockUri, contentResolver)

        assertEquals(null, fileName)
    }
}
