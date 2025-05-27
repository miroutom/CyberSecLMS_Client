package hse.diploma.cybersecplatform.ui.screens.profile

import android.content.ContentResolver
import android.net.Uri
import hse.diploma.cybersecplatform.data.model.UserData
import hse.diploma.cybersecplatform.data.model.response.MessageResponse
import hse.diploma.cybersecplatform.domain.repository.UserRepo
import hse.diploma.cybersecplatform.ui.state.ProfileState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
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
    private lateinit var userRepo: UserRepo
    private lateinit var viewModel: ProfileViewModel

    private val user = UserData("u", "f", "e", null)
    private val userUpdated = UserData("unew", "fnew", "enew", null)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        userRepo = mockk()
        coEvery { userRepo.getUserProfile() } returns Result.success(user)
        viewModel = ProfileViewModel(userRepo)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadProfile success sets SuccessState`() =
        runTest {
            viewModel.loadProfile()
            testDispatcher.scheduler.advanceUntilIdle()
            val state = viewModel.profileState.value
            assertTrue(state is ProfileState.Success)
            if (state is ProfileState.Success) assertEquals(user, state.uiState.userData)
        }

    @Test
    fun `loadProfile failure sets ErrorState`() =
        runTest {
            coEvery { userRepo.getUserProfile() } returns Result.failure(Exception("fail"))
            viewModel.loadProfile()
            testDispatcher.scheduler.advanceUntilIdle()
            val state = viewModel.profileState.value
            assertTrue(state is ProfileState.Error)
        }

    @Test
    fun `updateProfile success should set SuccessState with updated UserData`() =
        runTest {
            coEvery { userRepo.updateProfile(any()) } returns Result.success(MessageResponse("success"))
            coEvery { userRepo.getUserProfile() } returns Result.success(userUpdated)

            viewModel.updateProfile("unew", "fnew", "enew")
            testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.profileState.value
            assertTrue(state is ProfileState.Success)
            if (state is ProfileState.Success) {
                assertEquals(userUpdated, state.uiState.userData)
            }
        }

    @Test
    fun `updateProfile failure sets ErrorState`() =
        runTest {
            coEvery { userRepo.updateProfile(any()) } returns Result.failure(Exception("fail"))
            viewModel.updateProfile("err", "err", "err")
            testDispatcher.scheduler.advanceUntilIdle()
            val state = viewModel.profileState.value
            assertTrue(state is ProfileState.Error)
        }

    @Test
    fun `uploadPhoto success sets SuccessState`() =
        runTest {
            val uri = mockk<Uri>()
            val cr = mockk<ContentResolver>()
            coEvery { userRepo.uploadAvatar(uri, cr) } returns Result.success(user)
            coEvery { userRepo.getUserProfile() } returns Result.success(user)
            viewModel.uploadPhoto(uri, cr)
            testDispatcher.scheduler.advanceUntilIdle()
            val state = viewModel.profileState.value
            assertTrue(state is ProfileState.Success)
            if (state is ProfileState.Success) assertEquals(user, state.uiState.userData)
        }

    @Test
    fun `uploadPhoto failure sets ErrorState`() =
        runTest {
            val uri = mockk<Uri>()
            val cr = mockk<ContentResolver>()
            coEvery { userRepo.uploadAvatar(uri, cr) } returns Result.failure(Exception("fail"))
            viewModel.uploadPhoto(uri, cr)
            testDispatcher.scheduler.advanceUntilIdle()
            val state = viewModel.profileState.value
            assertTrue(state is ProfileState.Error)
        }
}
