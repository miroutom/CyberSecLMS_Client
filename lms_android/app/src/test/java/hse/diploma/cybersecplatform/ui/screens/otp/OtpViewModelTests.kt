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
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OtpViewModelTests {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var authRepo: AuthRepo
    private lateinit var viewModel: OtpViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        authRepo = mockk()
        viewModel = OtpViewModel(authRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial isLoading state is false`() =
        runTest {
            assertFalse(viewModel.isLoading.value)
        }

    @Test
    fun `verifyOtp calls onResult with success when repo succeeds`() =
        runTest {
            val mockResponse = mockk<LoginResponse>()
            coEvery { authRepo.verifyOtp(any(), any()) } returns Result.success(mockResponse)
            var receivedResult: Result<LoginResponse>? = null

            viewModel.verifyOtp("tempToken", "123456") { result ->
                receivedResult = result
            }
            testDispatcher.scheduler.advanceUntilIdle()

            assertNotNull(receivedResult)
            assertTrue(receivedResult!!.isSuccess)
            assertEquals(mockResponse, receivedResult?.getOrNull())
        }

    @Test
    fun `verifyOtp calls onResult with failure when repo fails`() =
        runTest {
            val exception = Exception("Verification failed")
            coEvery { authRepo.verifyOtp(any(), any()) } returns Result.failure(exception)
            var receivedResult: Result<LoginResponse>? = null

            viewModel.verifyOtp("tempToken", "123456") { result ->
                receivedResult = result
            }
            testDispatcher.scheduler.advanceUntilIdle()

            assertNotNull(receivedResult)
            assertTrue(receivedResult!!.isFailure)
            assertEquals(exception, receivedResult?.exceptionOrNull())
        }
}
