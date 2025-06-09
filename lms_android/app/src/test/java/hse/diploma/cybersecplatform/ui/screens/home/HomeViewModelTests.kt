package hse.diploma.cybersecplatform.ui.screens.home

import androidx.compose.ui.text.input.TextFieldValue
import hse.diploma.cybersecplatform.domain.repository.CoursesRepo
import hse.diploma.cybersecplatform.mock.mockAllCourses
import hse.diploma.cybersecplatform.mock.mockCourses
import hse.diploma.cybersecplatform.ui.model.VulnerabilityType
import hse.diploma.cybersecplatform.ui.state.shared.AllCoursesState
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
class HomeViewModelTests {
    private val testDispatcher = StandardTestDispatcher()
    private val coursesRepo: CoursesRepo = mockk()

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        coEvery { coursesRepo.getAllCourses() } coAnswers {
            Result.success(mockAllCourses)
        }

        viewModel = HomeViewModel(coursesRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when loadCourses is called and data is available, then state is updated with courses`() =
        runTest {
            coEvery { coursesRepo.getAllCourses() } returns Result.success(mockCourses)
            viewModel.loadCourses()

            advanceUntilIdle()

            val state = viewModel.allCoursesState.value
            assertTrue(state is AllCoursesState.Success)
            if (state is AllCoursesState.Success) {
                assertEquals(mockCourses, state.uiState.courses)
            }
        }

    @Test
    fun `when loadCourses is called and data is unavailable, then state is updated with error`() =
        runTest {
            coEvery { coursesRepo.getAllCourses() } returns Result.failure(Exception("fail"))
            viewModel.loadCourses()

            advanceUntilIdle()

            val state = viewModel.allCoursesState.value
            assertTrue(state is AllCoursesState.Error)
        }

    @Test
    fun `when search query changes to SQL, then courses are filtered to show only SQL courses`() =
        runTest {
            coEvery { coursesRepo.getAllCourses() } returns Result.success(mockCourses)
            viewModel.loadCourses()

            advanceUntilIdle()

            viewModel.onSearchQueryChange(TextFieldValue("SQL"))
            val state = viewModel.allCoursesState.value
            assertTrue(state is AllCoursesState.Success)
            if (state is AllCoursesState.Success) {
                assertEquals(2, state.uiState.filteredCourses.size)
                assertEquals(VulnerabilityType.SQL.name, state.uiState.filteredCourses[0].vulnerabilityType)
            }
        }

    @Test
    fun `when search query changes to XSS, then courses are filtered to show only XSS courses`() =
        runTest {
            coEvery { coursesRepo.getAllCourses() } returns Result.success(mockCourses)
            viewModel.loadCourses()

            advanceUntilIdle()

            viewModel.onSearchQueryChange(TextFieldValue("XSS"))
            val state = viewModel.allCoursesState.value
            assertTrue(state is AllCoursesState.Success)
            if (state is AllCoursesState.Success) {
                assertEquals(1, state.uiState.filteredCourses.size)
                assertEquals(VulnerabilityType.XSS.name, state.uiState.filteredCourses[0].vulnerabilityType)
            }
        }

    @Test
    fun `when search query is empty, then all courses are shown`() =
        runTest {
            coEvery { coursesRepo.getAllCourses() } returns Result.success(mockCourses)
            viewModel.loadCourses()

            advanceUntilIdle()

            viewModel.onSearchQueryChange(TextFieldValue(""))
            val state = viewModel.allCoursesState.value
            assertTrue(state is AllCoursesState.Success)
            if (state is AllCoursesState.Success) {
                assertEquals(mockCourses.size, state.uiState.filteredCourses.size)
            }
        }

    @Test
    fun `when search query does not match any course, then no courses are shown`() =
        runTest {
            coEvery { coursesRepo.getAllCourses() } returns Result.success(mockCourses)
            viewModel.loadCourses()

            advanceUntilIdle()

            viewModel.onSearchQueryChange(TextFieldValue("NOPE"))
            val state = viewModel.allCoursesState.value
            assertTrue(state is AllCoursesState.Success)
            if (state is AllCoursesState.Success) {
                assertTrue(state.uiState.filteredCourses.isEmpty())
            }
        }

    @Test
    fun `when search query is entered in lowercase, then filtering is case insensitive`() =
        runTest {
            coEvery { coursesRepo.getAllCourses() } returns Result.success(mockCourses)
            viewModel.loadCourses()

            advanceUntilIdle()

            viewModel.onSearchQueryChange(TextFieldValue("sql"))
            val state = viewModel.allCoursesState.value
            assertTrue(state is AllCoursesState.Success)
            if (state is AllCoursesState.Success) {
                assertEquals(2, state.uiState.filteredCourses.size)
                assertEquals(VulnerabilityType.SQL.name, state.uiState.filteredCourses[0].vulnerabilityType)
            }
        }
}
