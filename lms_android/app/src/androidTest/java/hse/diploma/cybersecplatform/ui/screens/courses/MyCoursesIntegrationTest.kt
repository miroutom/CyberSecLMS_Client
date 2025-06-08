package hse.diploma.cybersecplatform.ui.screens.courses

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.mock.mockCourses
import hse.diploma.cybersecplatform.ui.screens.isCircularProgressIndicator
import hse.diploma.cybersecplatform.ui.state.shared.MyCoursesState
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
        every { viewModel.selectedCourseForRestart } returns MutableStateFlow(null)
    }

    private fun getString(resId: Int): String = context.getString(resId)

    @Test
    fun shouldDisplayTabsAndCoursesWhenSuccessState() {
        val startedCourses = mockCourses.filter { it.isStarted }
        every { viewModel.myCoursesState } returns
            MutableStateFlow(
                MyCoursesState.Success(
                    CoursesUiState(
                        courses = mockCourses,
                        startedCourses = startedCourses,
                        completedCourses = emptyList(),
                    ),
                ),
            )

        composeRule.setContent {
            MyCoursesScreenWrapper(viewModel = viewModel, navController = navController)
        }

        composeRule.waitForIdle()

        composeRule.onNodeWithText(getString(R.string.started_courses_tab_button))
            .assertIsDisplayed()
        composeRule.onNodeWithText(getString(R.string.completed_courses_tab_button))
            .assertIsDisplayed()

        if (startedCourses.isNotEmpty()) {
            composeRule.onNodeWithText(startedCourses.first().title.substring(0, 3), substring = true)
                .assertIsDisplayed()
        }
    }

    @Test
    fun shouldNavigateToTaskScreenWhenCourseClicked() {
        val testCourse = mockCourses.first()
        every { viewModel.myCoursesState } returns
            MutableStateFlow(
                MyCoursesState.Success(
                    CoursesUiState(
                        courses = mockCourses,
                        startedCourses = mockCourses,
                        completedCourses = emptyList(),
                    ),
                ),
            )

        composeRule.setContent {
            MyCoursesScreenWrapper(viewModel = viewModel, navController = navController)
        }

        composeRule.onNodeWithText(testCourse.title.substring(0, 3), substring = true)
            .performClick()

        verify { navController.navigate("tasksScreen/${testCourse.id}") }
    }

    @Test
    fun shouldShowDialogWhenRestartingCompletedCourse() {
        val completedCourse = mockCourses.first().copy(isStarted = false, progress = 100)
        every { viewModel.myCoursesState } returns
            MutableStateFlow(
                MyCoursesState.Success(
                    CoursesUiState(
                        courses = mockCourses,
                        startedCourses = emptyList(),
                        completedCourses = listOf(completedCourse),
                    ),
                ),
            )
        every { viewModel.selectedCourseForRestart } returns MutableStateFlow(completedCourse)

        composeRule.setContent {
            MyCoursesScreenWrapper(viewModel = viewModel, navController = navController)
        }

        composeRule.onNodeWithText(getString(R.string.completed_courses_tab_button))
            .performClick()

        composeRule.onNodeWithText(getString(R.string.restart_course_button), substring = true)
            .performClick()

        composeRule.onNodeWithText(getString(R.string.reset_progress_title))
            .assertIsDisplayed()
    }

    @Test
    fun shouldDisplayLoadingIndicatorWhenLoadingState() {
        every { viewModel.myCoursesState } returns MutableStateFlow(MyCoursesState.Loading)

        composeRule.setContent {
            MyCoursesScreenWrapper(viewModel = viewModel, navController = navController)
        }

        composeRule.onNode(isCircularProgressIndicator())
            .assertIsDisplayed()
    }
}
