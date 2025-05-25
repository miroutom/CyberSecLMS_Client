package hse.diploma.cybersecplatform.data.repository

import android.content.ContentResolver
import android.net.Uri
import hse.diploma.cybersecplatform.data.api.ApiService
import hse.diploma.cybersecplatform.data.model.UserData
import hse.diploma.cybersecplatform.data.model.response.MessageResponse
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class UserRepoImplTests {
    private val apiService: ApiService = mockk()
    private val contentResolver: ContentResolver = mockk()
    private lateinit var userRepo: UserRepoImpl

    @Before
    fun setup() {
        userRepo = UserRepoImpl(apiService)
    }

    @Test
    fun `getUserProfile should return user data on success`() =
        runTest {
            val mockUser = UserData("test", "Test User", "test@example.com", null)
            coEvery { apiService.getUserProfile() } returns Response.success(mockUser)

            val result = userRepo.getUserProfile()

            assertTrue(result.isSuccess)
            assertEquals(mockUser, result.getOrNull())
        }

    @Test
    fun `updateProfile should return success message`() =
        runTest {
            val mockMessage = MessageResponse("Profile updated")
            coEvery { apiService.updateProfile(any()) } returns Response.success(mockMessage)

            val result = userRepo.updateProfile(UserData("test", "New Name", "email", null))

            assertTrue(result.isSuccess)
            assertEquals(mockMessage, result.getOrNull())
        }

    @Test
    fun `uploadAvatar should fail when cannot open stream`() =
        runTest {
            val mockUri = mockk<Uri>()
            every { contentResolver.openInputStream(mockUri) } returns null

            val result = userRepo.uploadAvatar(mockUri, contentResolver)

            assertTrue(result.isFailure)
            assertEquals("Can't open image stream", result.exceptionOrNull()?.message)
        }
}

