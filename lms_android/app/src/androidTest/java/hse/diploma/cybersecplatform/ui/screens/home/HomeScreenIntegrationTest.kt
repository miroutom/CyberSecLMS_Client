package hse.diploma.cybersecplatform.ui.screens.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.domain.error.ErrorType
import hse.diploma.cybersecplatform.mock.mockAllCourses
import hse.diploma.cybersecplatform.ui.screens.courses.CoursesUiState
import hse.diploma.cybersecplatform.ui.state.shared.AllCoursesState
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
    }

    private fun getString(resId: Int): String = context.getString(resId)

    @Test
    fun loadingState_displaysLoadingIndicator() {
        every { viewModel.allCoursesState } returns MutableStateFlow(AllCoursesState.Loading)

        composeRule.setContent {
            HomeScreenWrapper(viewModel = viewModel, navController = navController)
        }

        composeRule.onNodeWithTag("LoadingIndicator").assertIsDisplayed()
    }

    @Test
    fun errorState_displaysErrorScreen() {
        every { viewModel.allCoursesState } returns
            MutableStateFlow(AllCoursesState.Error(ErrorType.NoInternet))

        composeRule.setContent {
            HomeScreenWrapper(viewModel = viewModel, navController = navController)
        }

        composeRule.onNodeWithText(getString(R.string.no_internet_error))
            .assertIsDisplayed()
    }

    @Test
    fun successState_displaysCourses() {
        every { viewModel.searchQuery } returns MutableStateFlow(TextFieldValue(""))

        val testCourses = mockAllCourses.take(1)
        every { viewModel.allCoursesState } returns
            MutableStateFlow(
                AllCoursesState.Success(
                    CoursesUiState(
                        courses = testCourses,
                        startedCourses = testCourses.filter { it.isStarted },
                        completedCourses = testCourses.filter { !it.isStarted },
                        filteredCourses = emptyList(),
                    ),
                ),
            )

        composeRule.setContent {
            HomeScreenWrapper(viewModel = viewModel, navController = navController)
        }

        testCourses.forEach { course ->
            composeRule.onNodeWithText(course.vulnerabilityType.name, substring = true)
                .assertIsDisplayed()
        }
    }

    @Test
    fun searchFunctionality_filtersCourses() {
        val searchQueryState = MutableStateFlow(TextFieldValue(""))
        every { viewModel.searchQuery } returns searchQueryState

        val testCourses = mockAllCourses.take(2)
        val testQuery = "SQL"
        val filteredCourses =
            testCourses.filter {
                it.vulnerabilityType.name.contains(testQuery, ignoreCase = true)
            }

        every { viewModel.allCoursesState } returns
            MutableStateFlow(
                AllCoursesState.Success(
                    CoursesUiState(
                        courses = testCourses,
                        startedCourses = testCourses.filter { it.isStarted },
                        completedCourses = testCourses.filter { !it.isStarted },
                        filteredCourses = filteredCourses,
                    ),
                ),
            )

        composeRule.setContent {
            HomeScreenWrapper(viewModel = viewModel, navController = navController)
        }

        composeRule.onNodeWithText(getString(R.string.search_bar_label))
            .performTextInput(testQuery)

        filteredCourses.forEach { course ->
            composeRule.onNodeWithText(course.vulnerabilityType.name, substring = true)
                .assertIsDisplayed()
        }

        testCourses.filterNot { it.vulnerabilityType.name.contains(testQuery, ignoreCase = true) }
            .forEach { course ->
                composeRule.onNodeWithText(course.vulnerabilityType.name, substring = true)
                    .assertDoesNotExist()
            }
    }

    @Test
    fun clickingCourse_navigatesToCorrectTaskScreen() {
        val initialQuery = TextFieldValue("")
        every { viewModel.searchQuery } returns MutableStateFlow(initialQuery)

        val testCourse = mockAllCourses.first()
        every { viewModel.allCoursesState } returns
            MutableStateFlow(
                AllCoursesState.Success(
                    CoursesUiState(
                        courses = listOf(testCourse),
                        startedCourses = listOf(testCourse),
                        completedCourses = emptyList(),
                        filteredCourses = emptyList(),
                    ),
                ),
            )

        composeRule.setContent {
            HomeScreenWrapper(viewModel = viewModel, navController = navController)
        }

        composeRule.onNodeWithText(testCourse.vulnerabilityType.name, substring = true)
            .performClick()

        verify { navController.navigate("taskScreen/${testCourse.vulnerabilityType.name}") }
    }
}
