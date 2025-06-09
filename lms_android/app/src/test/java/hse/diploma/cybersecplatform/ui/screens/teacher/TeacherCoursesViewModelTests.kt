package hse.diploma.cybersecplatform.ui.screens.teacher

import hse.diploma.cybersecplatform.data.model.response.MessageResponse
import hse.diploma.cybersecplatform.data.model.user.UserData
import hse.diploma.cybersecplatform.domain.model.Course
import hse.diploma.cybersecplatform.domain.model.Task
import hse.diploma.cybersecplatform.domain.repository.CoursesRepo
import hse.diploma.cybersecplatform.domain.repository.UserRepo
import hse.diploma.cybersecplatform.ui.model.Difficulty
import hse.diploma.cybersecplatform.ui.model.VulnerabilityType
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TeacherCoursesViewModelTests {
    private val testDispatcher = StandardTestDispatcher()
    private val coursesRepo: CoursesRepo = mockk()
    private val userRepo: UserRepo = mockk()

    private val testCourse =
        Course(
            id = 1,
            title = "Test Course",
            description = "Test Description",
            vulnerabilityType = VulnerabilityType.XSS.name,
            difficultyLevel = "medium",
            category = "web",
            tasks = emptyList(),
            completedTasks = 0,
            tasksCount = 0,
            isStarted = false,
            progress = 0,
        )

    private val testTask =
        Task(
            id = 1,
            courseId = 1,
            title = "Test Task",
            description = "Test Description",
            content = "Test Content",
            solution = "Решение задания...",
            vulnerabilityType = VulnerabilityType.XSS.name,
            number = 1,
            difficulty = Difficulty.EASY.name,
            type = "XSS",
            points = 10,
            isCompleted = false,
        )

    private val testUser =
        UserData(
            id = 1,
            username = "testuser",
            email = "test@example.com",
            fullName = "Test User",
            isTeacher = true,
        )

    private lateinit var viewModel: TeacherCoursesViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { userRepo.getUserProfile() } returns Result.success(testUser)

        val updatedCourse = testCourse.copy(title = "Updated")
        coEvery { coursesRepo.updateCourse(any(), any()) } returns Result.success(updatedCourse)

        viewModel = TeacherCoursesViewModel(coursesRepo, userRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when teacher status is checked with teacher user, then isTeacher should be set to true`() =
        runTest {
            advanceUntilIdle()
            assertEquals(true, viewModel.isTeacher.value)
        }

    @Test
    fun `when course is updated successfully, then selectedCourse should be updated`() =
        runTest {
            viewModel.selectCourse(testCourse)
            advanceUntilIdle()

            viewModel.updateCourse(1, "Updated", "Desc", "web")
            advanceUntilIdle()

            assertEquals("Updated", viewModel.selectedCourse.value?.title)
        }

    @Test
    fun `when task is created successfully, then it should be added to selectedCourse tasks`() =
        runTest {
            val newTask = testTask.copy(id = 2)
            coEvery { coursesRepo.createTask(any(), any()) } returns Result.success(newTask)
            viewModel.selectCourse(testCourse.copy(tasks = listOf(testTask)))

            viewModel.createTask(1, "New Task", "Desc", "type", 10, "Content")
            advanceUntilIdle()

            assertEquals(2, viewModel.selectedCourse.value?.tasks?.size)
        }

    @Test
    fun `when task is deleted successfully, then it should be removed from selectedCourse tasks`() =
        runTest {
            coEvery { coursesRepo.deleteTask(any(), any()) } returns Result.success(MessageResponse("Deleted"))
            viewModel.selectCourse(testCourse.copy(tasks = listOf(testTask)))

            viewModel.deleteTask(1, 1)
            advanceUntilIdle()

            assertEquals(0, viewModel.selectedCourse.value?.tasks?.size)
        }

    @Test
    fun `when course is selected, then selectedCourse should be updated and selectedTask cleared`() {
        viewModel.selectCourse(testCourse)
        assertEquals(testCourse, viewModel.selectedCourse.value)
        assertNull(viewModel.selectedTask.value)
    }

    @Test
    fun `when task is selected, then selectedTask should be updated`() {
        viewModel.selectTask(testTask)
        assertEquals(testTask, viewModel.selectedTask.value)
    }
}
