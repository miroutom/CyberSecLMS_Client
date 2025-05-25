package hse.diploma.cybersecplatform.ui.screens.home

import androidx.compose.ui.text.input.TextFieldValue
import hse.diploma.cybersecplatform.domain.model.Course
import hse.diploma.cybersecplatform.domain.repository.CoursesRepo
import hse.diploma.cybersecplatform.ui.model.VulnerabilityType
import hse.diploma.cybersecplatform.ui.state.AllCoursesState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTests {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var coursesRepo: CoursesRepo
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coursesRepo = mockk()
        viewModel = HomeViewModel(coursesRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() =
        runTest {
            assertTrue(viewModel.allCoursesState.value is AllCoursesState.Loading)
        }

    @Test
    fun `loadCourses success updates state correctly`() =
        runTest {
            val mockCourses =
                listOf(
                    Course(VulnerabilityType.XSS, 5, 10),
                    Course(VulnerabilityType.SQL, 0, 8),
                )
            coEvery { coursesRepo.getAllCourses() } returns Result.success(mockCourses)
            viewModel.loadCourses()
            testDispatcher.scheduler.advanceUntilIdle()
            val state = viewModel.allCoursesState.value
            assertTrue(state is AllCoursesState.Success)
            if (state is AllCoursesState.Success) {
                assertEquals(mockCourses, state.uiState.courses)
                assertEquals(1, state.uiState.startedCourses.size)
                assertEquals(1, state.uiState.completedCourses.size)
            }
        }

    @Test
    fun `loadCourses failure updates state with error`() =
        runTest {
            coEvery { coursesRepo.getAllCourses() } returns Result.failure(Exception("fail"))
            viewModel.loadCourses()
            testDispatcher.scheduler.advanceUntilIdle()
            val state = viewModel.allCoursesState.value
            assertTrue(state is AllCoursesState.Error)
        }

    @Test
    fun `filters to SQL`() =
        runTest {
            val mockCourses =
                listOf(
                    Course(VulnerabilityType.XSS, 5, 10),
                    Course(VulnerabilityType.SQL, 0, 8),
                )
            coEvery { coursesRepo.getAllCourses() } returns Result.success(mockCourses)
            viewModel.loadCourses()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onSearchQueryChange(TextFieldValue("SQL"))
            val state = viewModel.allCoursesState.value
            assertTrue(state is AllCoursesState.Success)
            if (state is AllCoursesState.Success) {
                assertEquals(1, state.uiState.filteredCourses.size)
                assertEquals(VulnerabilityType.SQL, state.uiState.filteredCourses[0].vulnerabilityType)
            }
        }

    @Test
    fun `filters to XSS`() =
        runTest {
            val mockCourses =
                listOf(
                    Course(VulnerabilityType.XSS, 5, 10),
                    Course(VulnerabilityType.SQL, 0, 8),
                )
            coEvery { coursesRepo.getAllCourses() } returns Result.success(mockCourses)
            viewModel.loadCourses()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onSearchQueryChange(TextFieldValue("XSS"))
            val state = viewModel.allCoursesState.value
            assertTrue(state is AllCoursesState.Success)
            if (state is AllCoursesState.Success) {
                assertEquals(1, state.uiState.filteredCourses.size)
                assertEquals(VulnerabilityType.XSS, state.uiState.filteredCourses[0].vulnerabilityType)
            }
        }

    @Test
    fun `filters to all on empty`() =
        runTest {
            val mockCourses =
                listOf(
                    Course(VulnerabilityType.XSS, 5, 10),
                    Course(VulnerabilityType.SQL, 0, 8),
                )
            coEvery { coursesRepo.getAllCourses() } returns Result.success(mockCourses)
            viewModel.loadCourses()
            testDispatcher.scheduler.advanceUntilIdle()
            viewModel.onSearchQueryChange(TextFieldValue(""))
            val state = viewModel.allCoursesState.value
            assertTrue(state is AllCoursesState.Success)
            if (state is AllCoursesState.Success) {
                assertEquals(2, state.uiState.filteredCourses.size)
            }
        }

    @Test
    fun `filters to no courses on garbage input`() =
        runTest {
            val mockCourses =
                listOf(
                    Course(VulnerabilityType.XSS, 5, 10),
                    Course(VulnerabilityType.SQL, 0, 8),
                )
            coEvery { coursesRepo.getAllCourses() } returns Result.success(mockCourses)
            viewModel.loadCourses()
            testDispatcher.scheduler.advanceUntilIdle()
            viewModel.onSearchQueryChange(TextFieldValue("NOPE"))
            val state = viewModel.allCoursesState.value
            assertTrue(state is AllCoursesState.Success)
            if (state is AllCoursesState.Success) {
                assertTrue(state.uiState.filteredCourses.isEmpty())
            }
        }

    @Test
    fun `filter is case insensitive`() =
        runTest {
            val mockCourses =
                listOf(
                    Course(VulnerabilityType.XSS, 5, 10),
                    Course(VulnerabilityType.SQL, 0, 8),
                )
            coEvery { coursesRepo.getAllCourses() } returns Result.success(mockCourses)
            viewModel.loadCourses()
            testDispatcher.scheduler.advanceUntilIdle()
            viewModel.onSearchQueryChange(TextFieldValue("sql"))
            val state = viewModel.allCoursesState.value
            assertTrue(state is AllCoursesState.Success)
            if (state is AllCoursesState.Success) {
                assertEquals(1, state.uiState.filteredCourses.size)
                assertEquals(VulnerabilityType.SQL, state.uiState.filteredCourses[0].vulnerabilityType)
            }
        }
}
