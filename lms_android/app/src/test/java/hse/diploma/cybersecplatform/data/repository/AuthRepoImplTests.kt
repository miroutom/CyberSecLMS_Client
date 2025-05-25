package hse.diploma.cybersecplatform.data.repository

import hse.diploma.cybersecplatform.data.api.ApiService
import hse.diploma.cybersecplatform.data.api.TokenManager
import hse.diploma.cybersecplatform.data.model.UserData
import hse.diploma.cybersecplatform.data.model.response.LoginResponse
import hse.diploma.cybersecplatform.data.model.response.MessageResponse
import hse.diploma.cybersecplatform.data.model.response.RegisterResponse
import hse.diploma.cybersecplatform.data.model.response.TempTokenResponse
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
    fun `login should return success when response is successful`() =
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
    fun `login should return failure when response fails`() =
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
    fun `register should save token when successful`() =
        runTest {
            val mockResponse =
                mockk<Response<RegisterResponse>> {
                    every { isSuccessful } returns true
                    every { body() } returns RegisterResponse("auth_token", "success")
                }
            coEvery { apiService.register(any()) } returns mockResponse

            val result = authRepo.register("user", "pass", "email@test.com", "Full Name")

            verify { tokenManager.saveToken("auth_token") }
            assertTrue(result.isSuccess)
        }

    @Test
    fun `verifyOtp should save token when successful`() =
        runTest {
            val mockResponse =
                mockk<Response<LoginResponse>> {
                    every { isSuccessful } returns true
                    every { body() } returns LoginResponse("auth_token", UserData("u", "f", "e", null))
                }
            coEvery { apiService.verifyOtp(any()) } returns mockResponse

            val result = authRepo.verifyOtp("123456", "temp_token")

            verify { tokenManager.saveToken("auth_token") }
            assertTrue(result.isSuccess)
        }

    @Test
    fun `logout should clear token`() {
        authRepo.logout()
        verify { tokenManager.clearToken() }
    }

    @Test
    fun `isAuthorized should return tokenManager hasToken`() {
        every { tokenManager.hasToken() } returns true
        assertTrue(authRepo.isAuthorized())

        every { tokenManager.hasToken() } returns false
        assertFalse(authRepo.isAuthorized())
    }

    @Test
    fun `forgotPassword should return success when response is successful`() =
        runTest {
            val mockResponse =
                mockk<Response<TempTokenResponse>> {
                    every { isSuccessful } returns true
                    every { body() } returns TempTokenResponse("success", "temp_token")
                }
            coEvery { apiService.forgotPassword(any()) } returns mockResponse

            val result = authRepo.forgotPassword(email = "test@example.com")

            assertTrue(result.isSuccess)
            assertEquals("temp_token", result.getOrNull()?.tempToken)
        }

    @Test
    fun `resetPassword should return success message when successful`() =
        runTest {
            val mockResponse =
                mockk<Response<MessageResponse>> {
                    every { isSuccessful } returns true
                    every { body() } returns MessageResponse("Password reset successful")
                }
            coEvery { apiService.resetPassword(any()) } returns mockResponse

            val result = authRepo.resetPassword("temp", "code", "newPass")

            assertTrue(result.isSuccess)
            assertEquals("Password reset successful", result.getOrNull()?.message)
        }

    @Test
    fun `confirmDeleteAccount should logout when successful`() =
        runTest {
            val mockResponse =
                mockk<Response<MessageResponse>> {
                    every { isSuccessful } returns true
                    every { body() } returns MessageResponse("Account deleted")
                }
            coEvery { apiService.confirmDeleteAccount(any()) } returns mockResponse

            val result = authRepo.confirmDeleteAccount("123456", "temp_token")

            verify { tokenManager.clearToken() }
            assertTrue(result.isSuccess)
        }
}
