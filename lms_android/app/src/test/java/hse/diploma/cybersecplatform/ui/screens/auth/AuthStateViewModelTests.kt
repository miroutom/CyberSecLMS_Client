package hse.diploma.cybersecplatform.ui.screens.auth

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import hse.diploma.cybersecplatform.domain.repository.AuthRepo
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthStateViewModelTests {
    private val authRepo: AuthRepo = mock()

    @Test
    fun `when repository has authorization, then isAuthorized returns true`() {
        whenever(authRepo.isAuthorized()).thenReturn(true)
        val viewModel = AuthStateViewModel(authRepo)

        assertTrue(viewModel.isAuthorized.value)
    }

    @Test
    fun `when repository has no authorization, then isAuthorized returns false`() {
        whenever(authRepo.isAuthorized()).thenReturn(false)
        val viewModel = AuthStateViewModel(authRepo)

        assertFalse(viewModel.isAuthorized.value)
    }

    @Test
    fun `when authorize is called, then isAuthorized is set to true`() {
        whenever(authRepo.isAuthorized()).thenReturn(false)

        val viewModel = AuthStateViewModel(authRepo)
        viewModel.authorize()

        assertTrue(viewModel.isAuthorized.value)
    }

    @Test
    fun `when logout is called, then repository logout is called and isAuthorized is set to false`() {
        whenever(authRepo.isAuthorized()).thenReturn(true)

        val viewModel = AuthStateViewModel(authRepo)
        viewModel.logout()

        assertFalse(viewModel.isAuthorized.value)
        verify(authRepo).logout()
    }
}
