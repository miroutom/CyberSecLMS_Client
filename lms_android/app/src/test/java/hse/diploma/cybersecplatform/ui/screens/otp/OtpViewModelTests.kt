package hse.diploma.cybersecplatform.ui.screens.otp

import hse.diploma.cybersecplatform.data.model.response.LoginResponse
import hse.diploma.cybersecplatform.domain.repository.AuthRepo
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OtpViewModelTests {
    private val testDispatcher = StandardTestDispatcher()
    private val authRepo: AuthRepo = mockk()

    private lateinit var viewModel: OtpViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        viewModel = OtpViewModel(authRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when viewModel is initialized, then isLoading is false`() =
        runTest {
            assertFalse(viewModel.isLoading.value)
        }

    @Test
    fun `when verifyOtp is called and repo succeeds, then onResult is called with success`() =
        runTest {
            val mockResponse = mockk<LoginResponse>()
            coEvery { authRepo.verifyOtp(any(), any()) } returns Result.success(mockResponse)
            var receivedResult: Result<LoginResponse>? = null

            viewModel.verifyOtp("tempToken", "123456") { result ->
                receivedResult = result
            }

            advanceUntilIdle()

            assertNotNull(receivedResult)
            assertTrue(receivedResult!!.isSuccess)
            assertEquals(mockResponse, receivedResult?.getOrNull())
        }

    @Test
    fun `when verifyOtp is called and repo fails, then onResult is called with failure`() =
        runTest {
            val exception = Exception("Verification failed")
            coEvery { authRepo.verifyOtp(any(), any()) } returns Result.failure(exception)
            var receivedResult: Result<LoginResponse>? = null

            viewModel.verifyOtp("tempToken", "123456") { result ->
                receivedResult = result
            }

            advanceUntilIdle()

            assertNotNull(receivedResult)
            assertTrue(receivedResult!!.isFailure)
            assertEquals(exception, receivedResult?.exceptionOrNull())
        }
}
