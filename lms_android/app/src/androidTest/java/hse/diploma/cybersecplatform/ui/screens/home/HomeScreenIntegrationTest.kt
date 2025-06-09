package hse.diploma.cybersecplatform.ui.screens.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.domain.error.ErrorType
import hse.diploma.cybersecplatform.mock.mockAllCourses
import hse.diploma.cybersecplatform.ui.screens.courses.CoursesUiState
import hse.diploma.cybersecplatform.ui.state.shared.AllCoursesState
import hse.diploma.cybersecplatform.utils.toVulnerabilityType
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenIntegrationTest {
    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var viewModel: HomeViewModel
    private lateinit var navController: NavHostController

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setUp() {
        viewModel = mockk(relaxed = true)
        navController = mockk(relaxed = true)

        every { viewModel.searchQuery } returns MutableStateFlow(TextFieldValue(""))
    }

    private fun getString(resId: Int): String = context.getString(resId)

    @Test
    fun shouldDisplayLoadingIndicatorWhenLoadingState() {
        every { viewModel.allCoursesState } returns MutableStateFlow(AllCoursesState.Loading)

        composeRule.setContent {
            HomeScreen(
                state = AllCoursesState.Loading,
                searchQuery = TextFieldValue(""),
                onSearchQueryChange = {},
                onCourseClick = {},
                onReload = {},
                onCreateCourseClick = {},
                isTeacher = false,
            )
        }

        composeRule.onNodeWithTag("LoadingIndicator").assertIsDisplayed()
    }

    @Test
    fun shouldDisplayErrorScreenWhenErrorState() {
        val errorMessage = getString(R.string.no_internet_error)

        composeRule.setContent {
            HomeScreen(
                state = AllCoursesState.Error(ErrorType.NoInternet),
                searchQuery = TextFieldValue(""),
                onSearchQueryChange = {},
                onCourseClick = {},
                onReload = {},
                onCreateCourseClick = {},
                isTeacher = false,
            )
        }

        composeRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }

    @Test
    fun shouldDisplayCoursesWhenSuccessState() {
        val testCourses = mockAllCourses.take(1)
        val testCourse = testCourses.first()

        composeRule.setContent {
            HomeScreen(
                state =
                    AllCoursesState.Success(
                        CoursesUiState(
                            courses = testCourses,
                            startedCourses = testCourses.filter { it.isStarted },
                            completedCourses = testCourses.filter { !it.isStarted },
                        ),
                    ),
                searchQuery = TextFieldValue(""),
                onSearchQueryChange = {},
                onCourseClick = {},
                onReload = {},
                onCreateCourseClick = {},
                isTeacher = false,
            )
        }

        composeRule.onNodeWithText(testCourse.vulnerabilityType.toVulnerabilityType().name, substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun shouldNavigateToTaskScreenWhenCourseClicked() {
        val testCourse = mockAllCourses.first()

        composeRule.setContent {
            HomeScreen(
                state =
                    AllCoursesState.Success(
                        CoursesUiState(
                            courses = listOf(testCourse),
                            startedCourses = listOf(testCourse),
                            completedCourses = emptyList(),
                        ),
                    ),
                searchQuery = TextFieldValue(""),
                onSearchQueryChange = {},
                onCourseClick = {
                    navController.navigate(
                        "task/${testCourse.vulnerabilityType.toVulnerabilityType().name}",
                    )
                },
                onReload = {},
                onCreateCourseClick = {},
                isTeacher = false,
            )
        }

        composeRule.onNodeWithText(
            testCourse.vulnerabilityType.toVulnerabilityType().name,
            substring = true,
        )
            .performClick()

        verify {
            navController.navigate(
                "task/${testCourse.vulnerabilityType.toVulnerabilityType().name}",
            )
        }
    }
}
