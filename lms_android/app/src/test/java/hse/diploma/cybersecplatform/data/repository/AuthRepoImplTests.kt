package hse.diploma.cybersecplatform.data.repository

import hse.diploma.cybersecplatform.data.api.ApiService
import hse.diploma.cybersecplatform.data.api.TokenManager
import hse.diploma.cybersecplatform.data.model.response.LoginResponse
import hse.diploma.cybersecplatform.data.model.response.MessageResponse
import hse.diploma.cybersecplatform.data.model.response.RegisterResponse
import hse.diploma.cybersecplatform.data.model.response.TempTokenResponse
import hse.diploma.cybersecplatform.mock.mockUser
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class AuthRepoImplTests {
    private val apiService: ApiService = mockk(relaxed = true)
    private val tokenManager: TokenManager = mockk(relaxed = true)

    private lateinit var authRepo: AuthRepoImpl

    @Before
    fun setup() {
        authRepo = AuthRepoImpl(apiService, tokenManager)
    }

    @Test
    fun `when login is called with valid credentials, then return success with token`() =
        runTest {
            val mockResponse =
                mockk<Response<TempTokenResponse>> {
                    every { isSuccessful } returns true
                    every { body() } returns TempTokenResponse("success", "temp_token")
                }
            coEvery { apiService.login(any()) } returns mockResponse

            val result = authRepo.login("user", "pass")

            assertTrue(result.isSuccess)
            assertEquals("temp_token", result.getOrNull()?.tempToken)
        }

    @Test
    fun `when login is called with invalid credentials, then return failure with error message`() =
        runTest {
            val mockResponse =
                mockk<Response<TempTokenResponse>> {
                    every { isSuccessful } returns false
                    every { errorBody()?.string() } returns "Invalid credentials"
                }
            coEvery { apiService.login(any()) } returns mockResponse

            val result = authRepo.login("user", "wrong_pass")

            assertTrue(result.isFailure)
            assertEquals("Invalid credentials", result.exceptionOrNull()?.message)
        }

    @Test
    fun `when register is called and succeeds, then save token and return success`() =
        runTest {
            val mockResponse =
                mockk<Response<RegisterResponse>> {
                    every { isSuccessful } returns true
                    every { body() } returns RegisterResponse("auth_token", "success")
                }
            coEvery { apiService.register(any()) } returns mockResponse

            val result = authRepo.register("user", "pass", "email@test.com", "Full Name", isTeacher = false)

            verify { tokenManager.saveToken("auth_token") }
            assertTrue(result.isSuccess)
        }

    @Test
    fun `when verifyOtp is called with valid code, then save token and return success`() =
        runTest {
            val mockResponse =
                mockk<Response<LoginResponse>> {
                    every { isSuccessful } returns true
                    every { body() } returns LoginResponse("auth_token", mockUser)
                }
            coEvery { apiService.verifyOtp(any()) } returns mockResponse

            val result = authRepo.verifyOtp("123456", "temp_token")

            verify { tokenManager.saveToken("auth_token") }
            assertTrue(result.isSuccess)
        }

    @Test
    fun `when logout is called, then token is cleared`() {
        authRepo.logout()
        verify { tokenManager.clearToken() }
    }

    @Test
    fun `when isAuthorized is called and token exists, then return true`() {
        every { tokenManager.hasToken() } returns true
        assertTrue(authRepo.isAuthorized())
    }

    @Test
    fun `when isAuthorized is called and token does not exist, then return false`() {
        every { tokenManager.hasToken() } returns false
        assertFalse(authRepo.isAuthorized())
    }

    @Test
    fun `when forgotPassword is called with valid email, then return success with token`() =
        runTest {
            val expected = TempTokenResponse("success", "temp")
            coEvery { apiService.forgotPassword(any()) } returns Response.success(expected)

            val result = authRepo.forgotPassword(email = "test@test.com")

            assertTrue(result.isSuccess)
            assertEquals(expected, result.getOrNull())
        }

    @Test
    fun `when resetPassword is called with valid code, then return success message`() =
        runTest {
            val expected = MessageResponse("success")
            coEvery { apiService.resetPassword(any()) } returns Response.success(expected)

            val result = authRepo.resetPassword("temp", "code", "new")

            assertTrue(result.isSuccess)
            assertEquals(expected, result.getOrNull())
        }

    @Test
    fun `when changePassword is called with valid credentials, then return success message`() =
        runTest {
            val expected = mapOf("status" to "success")
            coEvery { apiService.changePassword(any()) } returns Response.success(expected)

            val result = authRepo.changePassword("old", "new")

            assertTrue(result.isSuccess)
            assertEquals(expected, result.getOrNull())
        }

    @Test
    fun `when requestDeleteAccount is called with valid password, then return success with token`() =
        runTest {
            val expected = TempTokenResponse("success", "temp")
            coEvery { apiService.requestDeleteAccount(any()) } returns Response.success(expected)

            val result = authRepo.requestDeleteAccount("pass")

            assertTrue(result.isSuccess)
            assertEquals(expected, result.getOrNull())
        }

    @Test
    fun `when confirmDeleteAccount is called with valid code, then logout and return success`() =
        runTest {
            val expected = MessageResponse("success")
            coEvery { apiService.confirmDeleteAccount(any()) } returns Response.success(expected)

            val result = authRepo.confirmDeleteAccount("code")

            verify { tokenManager.clearToken() }
            assertTrue(result.isSuccess)
            assertEquals(expected, result.getOrNull())
        }
}
