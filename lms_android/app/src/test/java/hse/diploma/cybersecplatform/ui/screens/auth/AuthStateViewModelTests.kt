package hse.diploma.cybersecplatform.ui.screens.auth

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import hse.diploma.cybersecplatform.domain.repository.AuthRepo
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthStateViewModelTest {

    private val authRepo: AuthRepo = mock()

    @Test
    fun `isAuthorized returns true if repo isAuthorized`() {
        whenever(authRepo.isAuthorized()).thenReturn(true)
        val viewModel = AuthStateViewModel(authRepo)
        assertTrue(viewModel.isAuthorized.value)
    }

    @Test
    fun `isAuthorized returns false if repo not authorized`() {
        whenever(authRepo.isAuthorized()).thenReturn(false)
        val viewModel = AuthStateViewModel(authRepo)
        assertFalse(viewModel.isAuthorized.value)
    }

    @Test
    fun `authorize sets isAuthorized to true`() {
        whenever(authRepo.isAuthorized()).thenReturn(false)
        val viewModel = AuthStateViewModel(authRepo)
        viewModel.authorize()
        assertTrue(viewModel.isAuthorized.value)
    }

    @Test
    fun `logout calls repo logout and sets isAuthorized false`() {
        whenever(authRepo.isAuthorized()).thenReturn(true)
        val viewModel = AuthStateViewModel(authRepo)
        viewModel.logout()
        assertFalse(viewModel.isAuthorized.value)
        verify(authRepo).logout()
    }
}

