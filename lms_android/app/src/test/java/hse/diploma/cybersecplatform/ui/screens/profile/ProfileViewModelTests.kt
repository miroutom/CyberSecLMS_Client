package hse.diploma.cybersecplatform.ui.screens.profile

import android.content.ContentResolver
import android.net.Uri
import hse.diploma.cybersecplatform.data.model.response.MessageResponse
import hse.diploma.cybersecplatform.domain.repository.UserRepo
import hse.diploma.cybersecplatform.mock.mockStats
import hse.diploma.cybersecplatform.mock.mockUser
import hse.diploma.cybersecplatform.ui.state.shared.ProfileState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTests {
    private val testDispatcher = StandardTestDispatcher()
    private val userRepo: UserRepo = mockk()

    private lateinit var viewModel: ProfileViewModel

    private val userUpdated =
        mockUser.copy(
            username = "unew",
            fullName = "fnew",
            email = "enew",
        )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { userRepo.getUserProfile() } returns Result.success(mockUser)
        coEvery { userRepo.getUserStatistics(any()) } returns Result.success(mockStats)
        viewModel = ProfileViewModel(userRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when loadProfile is called and data is available, then state is updated with success`() =
        runTest {
            viewModel.loadProfile()

            advanceUntilIdle()

            val state = viewModel.profileState.value
            assertTrue(state is ProfileState.Success)
            if (state is ProfileState.Success) {
                assertEquals(mockUser, state.uiState.userData)
                assertEquals(mockStats, state.uiState.stats)
            }
        }

    @Test
    fun `when loadProfile is called and profile data is unavailable, then state is updated with error`() =
        runTest {
            coEvery { userRepo.getUserProfile() } returns Result.failure(Exception("fail"))
            viewModel.loadProfile()

            advanceUntilIdle()

            val state = viewModel.profileState.value
            assertTrue(state is ProfileState.Error)
        }

    @Test
    fun `when loadProfile is called and statistics are unavailable, then fallback statistics are created`() =
        runTest {
            coEvery { userRepo.getUserStatistics(any()) } returns Result.failure(Exception("fail"))
            viewModel.loadProfile()

            advanceUntilIdle()

            val state = viewModel.profileState.value
            assertTrue(state is ProfileState.Success)
            if (state is ProfileState.Success) {
                assertEquals(mockUser, state.uiState.userData)
                // Проверяем, что статистика создана с базовыми значениями
                assertEquals(mockUser.id, state.uiState.stats.userId)
            }
        }

    @Test
    fun `when updateProfile is called with valid data, then user profile is updated`() =
        runTest {
            coEvery { userRepo.updateProfile(any()) } returns Result.success(MessageResponse("success"))
            coEvery { userRepo.getUserProfile() } returns Result.success(userUpdated)
            coEvery { userRepo.getUserStatistics(any()) } returns Result.success(mockStats)

            viewModel.updateProfile("unew", "fnew", "enew")

            advanceUntilIdle()

            val state = viewModel.profileState.value
            assertTrue(state is ProfileState.Success)
            if (state is ProfileState.Success) {
                assertEquals(userUpdated, state.uiState.userData)
            }
        }

    @Test
    fun `when updateProfile is called and API fails, then state is updated with error`() =
        runTest {
            coEvery { userRepo.updateProfile(any()) } returns Result.failure(Exception("fail"))
            viewModel.updateProfile("err", "err", "err")

            advanceUntilIdle()

            val state = viewModel.profileState.value
            assertTrue(state is ProfileState.Error)
        }

    @Test
    fun `when uploadAvatar is called with valid image, then user profile is updated with new image`() =
        runTest {
            val uri = mockk<Uri>()
            val cr = mockk<ContentResolver>()

            coEvery { userRepo.uploadAvatar(uri, cr) } returns Result.success(mockUser)

            viewModel.uploadAvatar(uri, cr)

            advanceUntilIdle()

            val state = viewModel.profileState.value
            assertTrue(state is ProfileState.Success)
            if (state is ProfileState.Success) {
                assertEquals(mockUser, state.uiState.userData)
            }
        }

    @Test
    fun `when uploadAvatar is called and API fails, then state is updated with error`() =
        runTest {
            val uri = mockk<Uri>()
            val cr = mockk<ContentResolver>()

            coEvery { userRepo.uploadAvatar(uri, cr) } returns Result.failure(Exception("fail"))

            viewModel.uploadAvatar(uri, cr)

            advanceUntilIdle()

            val state = viewModel.profileState.value
            assertTrue(state is ProfileState.Error)
        }
}
