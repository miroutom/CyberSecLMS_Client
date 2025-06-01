package hse.diploma.cybersecplatform.ui.screens.courses

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.domain.model.Course
import hse.diploma.cybersecplatform.ui.model.VulnerabilityType
import hse.diploma.cybersecplatform.ui.state.MyCoursesState
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MyCoursesScreenIntegrationTest {
    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var viewModel: MyCoursesViewModel
    private lateinit var navController: NavHostController

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setUp() {
        viewModel = mockk(relaxed = true)
        navController = mockk(relaxed = true)
    }

    private fun getString(resId: Int): String = context.getString(resId)

    @Test
    fun successState_displaysTabsAndCourses() {
        val testCourses =
            listOf(
                Course(
                    vulnerabilityType = VulnerabilityType.XSS,
                    completedTasks = 1,
                    tasksCount = 5,
                ),
                Course(
                    vulnerabilityType = VulnerabilityType.SQL,
                    completedTasks = 3,
                    tasksCount = 5,
                ),
            )

        every { viewModel.myCoursesState } returns
            MutableStateFlow(
                MyCoursesState.Success(
                    CoursesUiState(
                        courses = testCourses,
                        startedCourses = testCourses,
                        completedCourses = emptyList(),
                    ),
                ),
            )

        composeRule.setContent {
            MyCoursesScreen(viewModel = viewModel, navController = navController)
        }

        composeRule.onNodeWithText(getString(R.string.started_courses_tab_button))
            .assertIsDisplayed()
        composeRule.onNodeWithText(getString(R.string.completed_courses_tab_button))
            .assertIsDisplayed()

        composeRule.onNodeWithText("XSS", substring = true)
            .assertIsDisplayed()
        composeRule.onNodeWithText("SQL", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun clickingCourse_navigatesToTaskScreen() {
        val testCourse =
            Course(
                vulnerabilityType = VulnerabilityType.XSS,
                completedTasks = 1,
                tasksCount = 5,
            )

        every { viewModel.myCoursesState } returns
            MutableStateFlow(
                MyCoursesState.Success(
                    CoursesUiState(
                        courses = listOf(testCourse),
                        startedCourses = listOf(testCourse),
                        completedCourses = emptyList(),
                    ),
                ),
            )

        composeRule.setContent {
            MyCoursesScreen(viewModel = viewModel, navController = navController)
        }

        composeRule.onNodeWithText("XSS", substring = true)
            .performClick()

        verify { navController.navigate("taskScreen/XSS") }
    }

    @Test
    fun restartingCompletedCourse_showsConfirmationDialog() {
        val testCourse =
            Course(
                vulnerabilityType = VulnerabilityType.CSRF,
                completedTasks = 5,
                tasksCount = 5,
            )

        every { viewModel.myCoursesState } returns
            MutableStateFlow(
                MyCoursesState.Success(
                    CoursesUiState(
                        courses = listOf(testCourse),
                        startedCourses = emptyList(),
                        completedCourses = listOf(testCourse),
                    ),
                ),
            )

        composeRule.setContent {
            MyCoursesScreen(viewModel = viewModel, navController = navController)
        }

        composeRule.onNodeWithText(getString(R.string.completed_courses_tab_button))
            .performClick()

        composeRule.onNodeWithText(getString(R.string.restart_course_button))
            .performClick()

        composeRule.onNodeWithText(getString(R.string.reset_progress_title))
            .assertIsDisplayed()
    }

    @Test
    fun loadingState_displaysLoadingScreen() {
        every { viewModel.myCoursesState } returns MutableStateFlow(MyCoursesState.Loading)

        composeRule.setContent {
            MyCoursesScreen(viewModel = viewModel, navController = navController)
        }

        composeRule.onNodeWithTag("LoadingIndicator").assertIsDisplayed()
    }
}
