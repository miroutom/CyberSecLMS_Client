package hse.diploma.cybersecplatform.ui.screens.courses

import hse.diploma.cybersecplatform.domain.model.Course
import hse.diploma.cybersecplatform.domain.repository.CoursesRepo
import hse.diploma.cybersecplatform.ui.model.VulnerabilityType
import hse.diploma.cybersecplatform.ui.state.MyCoursesState
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
class MyCoursesViewModelTests {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var coursesRepo: CoursesRepo
    private lateinit var viewModel: MyCoursesViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coursesRepo = mockk()
        viewModel = MyCoursesViewModel(coursesRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() =
        runTest {
            assertTrue(viewModel.myCoursesState.value is MyCoursesState.Loading)
        }

    @Test
    fun `loadCourses success updates state correctly`() =
        runTest {
            val mockCourses =
                listOf(
                    Course(VulnerabilityType.XSS, 5, 10),
                    Course(VulnerabilityType.SQL, 0, 8),
                )
            coEvery { coursesRepo.getMyCourses() } returns Result.success(mockCourses)

            viewModel.loadCourses()
            testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.myCoursesState.value
            assertTrue(state is MyCoursesState.Success)
            if (state is MyCoursesState.Success) {
                assertEquals(2, state.uiState.courses.size)
                assertEquals(1, state.uiState.startedCourses.size)
                assertEquals(1, state.uiState.completedCourses.size)
            }
        }

    @Test
    fun `loadCourses failure updates state with error`() =
        runTest {
            coEvery { coursesRepo.getMyCourses() } returns Result.failure(Exception("fail"))
            viewModel.loadCourses()
            testDispatcher.scheduler.advanceUntilIdle()
            val state = viewModel.myCoursesState.value
            assertTrue(state is MyCoursesState.Error)
        }

    @Test
    fun `onCompletedCourseRestart updates course state correctly`() =
        runTest {
            val mockCourses =
                listOf(
                    Course(VulnerabilityType.XSS, 5, 10),
                    Course(VulnerabilityType.SQL, 0, 8),
                )
            coEvery { coursesRepo.getMyCourses() } returns Result.success(mockCourses)

            viewModel.loadCourses()
            testDispatcher.scheduler.advanceUntilIdle()
            val startState = viewModel.myCoursesState.value as MyCoursesState.Success
            val sqlCourse = startState.uiState.completedCourses[0]

            viewModel.onCompletedCourseRestart(sqlCourse)
            val finalState = viewModel.myCoursesState.value

            assertTrue(finalState is MyCoursesState.Success)
            if (finalState is MyCoursesState.Success) {
                assertEquals(2, finalState.uiState.startedCourses.size)
                assertEquals(0, finalState.uiState.completedCourses.size)
                val updated = finalState.uiState.courses.find { it.vulnerabilityType == VulnerabilityType.SQL }
                assertEquals(true, updated?.isStarted)
                assertEquals(0, updated?.completedTasks)
            }
        }
}
