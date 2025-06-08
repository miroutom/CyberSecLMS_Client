package hse.diploma.cybersecplatform.ui.screens.courses

import hse.diploma.cybersecplatform.domain.repository.CoursesRepo
import hse.diploma.cybersecplatform.domain.repository.UserRepo
import hse.diploma.cybersecplatform.mock.mockAllCourses
import hse.diploma.cybersecplatform.mock.mockCourses
import hse.diploma.cybersecplatform.mock.mockSqlCourse
import hse.diploma.cybersecplatform.mock.mockStats
import hse.diploma.cybersecplatform.mock.mockUser
import hse.diploma.cybersecplatform.mock.mockXssCourse
import hse.diploma.cybersecplatform.ui.state.shared.MyCoursesState
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
class MyCoursesViewModelTests {
    private val testDispatcher = StandardTestDispatcher()
    private val coursesRepo: CoursesRepo = mockk()
    private val userRepo: UserRepo = mockk()

    private lateinit var viewModel: MyCoursesViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        coEvery { userRepo.getUserProfile() } coAnswers {
            Result.success(mockUser)
        }
        coEvery { userRepo.getUserStatistics(any()) } coAnswers {
            Result.success(mockStats)
        }
        coEvery { coursesRepo.getAllCourses() } coAnswers {
            Result.success(mockAllCourses)
        }

        viewModel = MyCoursesViewModel(userRepo, coursesRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when loadCourses is called and data is available, then state is updated with success`() =
        runTest {
            coEvery { userRepo.getUserProfile() } returns Result.success(mockUser)
            coEvery { userRepo.getUserStatistics(mockUser.id) } returns Result.success(mockStats)
            coEvery { coursesRepo.getAllCourses() } returns Result.success(mockCourses)

            viewModel.loadCourses()

            advanceUntilIdle()

            val state = viewModel.myCoursesState.value
            assertTrue(state is MyCoursesState.Success)
            if (state is MyCoursesState.Success) {
                assertEquals(mockCourses.size, state.uiState.courses.size)
            }
        }

    @Test
    fun `when loadCourses is called and user profile fails, then state is updated with error`() =
        runTest {
            coEvery { userRepo.getUserProfile() } returns Result.failure(Exception("fail"))

            viewModel.loadCourses()

            advanceUntilIdle()

            val state = viewModel.myCoursesState.value
            assertTrue(state is MyCoursesState.Error)
        }

    @Test
    fun `when loadCourses is called and courses data fails, then state is updated with error`() =
        runTest {
            coEvery { userRepo.getUserProfile() } returns Result.success(mockUser)
            coEvery { coursesRepo.getAllCourses() } returns Result.failure(Exception("fail"))

            viewModel.loadCourses()

            advanceUntilIdle()

            val state = viewModel.myCoursesState.value
            assertTrue(state is MyCoursesState.Error)
        }

    @Test
    fun `when loadCourses is called and statistics data fails, then state is updated with error`() =
        runTest {
            coEvery { userRepo.getUserProfile() } returns Result.success(mockUser)
            coEvery { coursesRepo.getAllCourses() } returns Result.success(mockCourses)
            coEvery { userRepo.getUserStatistics(mockUser.id) } returns Result.failure(Exception("fail"))

            viewModel.loadCourses()

            advanceUntilIdle()

            val state = viewModel.myCoursesState.value
            assertTrue(state is MyCoursesState.Error)
        }

    @Test
    fun `when onCompletedCourseRestart is called, then course is moved from completed to started`() =
        runTest {

            val completedCourse =
                mockSqlCourse.copy(
                    isStarted = true,
                    completedTasks = mockSqlCourse.tasksCount,
                    progress = 100,
                )
            coEvery { userRepo.getUserProfile() } returns Result.success(mockUser)
            coEvery { userRepo.getUserStatistics(mockUser.id) } returns Result.success(mockStats)
            coEvery { coursesRepo.getAllCourses() } returns Result.success(listOf(completedCourse))

            viewModel.loadCourses()

            advanceUntilIdle()

            viewModel.onCompletedCourseRestart(completedCourse)

            advanceUntilIdle()

            val state = viewModel.myCoursesState.value as? MyCoursesState.Success
            assertEquals(0, state?.uiState?.completedCourses?.size)
            assertEquals(1, state?.uiState?.startedCourses?.size)

            val restartedCourse = state?.uiState?.startedCourses?.first()
            assertEquals(0, restartedCourse?.completedTasks)
            assertEquals(0, restartedCourse?.progress)
            assertEquals(true, restartedCourse?.isStarted)
        }

    @Test
    fun `when selectCourseForRestart is called, then selectedCourseForRestart is updated`() {
        viewModel.selectCourseForRestart(mockXssCourse)

        assertEquals(mockXssCourse, viewModel.selectedCourseForRestart.value)
    }
}
