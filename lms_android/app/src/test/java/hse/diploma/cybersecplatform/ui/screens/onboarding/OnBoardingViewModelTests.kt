package hse.diploma.cybersecplatform.ui.screens.onboarding

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class OnBoardingViewModelTests {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: OnBoardingViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = OnBoardingViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial page is 0`() =
        runTest {
            assertEquals(0, viewModel.currentPage.value)
        }

    @Test
    fun `onNextPage increases page number`() =
        runTest {
            viewModel.onNextPage()
            assertEquals(1, viewModel.currentPage.value)

            viewModel.onNextPage()
            assertEquals(2, viewModel.currentPage.value)
        }

    @Test
    fun `onNextPage does not exceed maximum page`() =
        runTest {
            viewModel.setPage(2)
            assertEquals(2, viewModel.currentPage.value)

            viewModel.onNextPage()
            assertEquals(2, viewModel.currentPage.value)
        }

    @Test
    fun `setPage updates current page correctly`() =
        runTest {
            viewModel.setPage(1)
            assertEquals(1, viewModel.currentPage.value)

            viewModel.setPage(0)
            assertEquals(0, viewModel.currentPage.value)

            viewModel.setPage(2)
            assertEquals(2, viewModel.currentPage.value)
        }
}
