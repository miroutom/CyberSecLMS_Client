package hse.diploma.cybersecplatform.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

open class OnBoardingViewModel : ViewModel() {
    private val _currentPage = MutableStateFlow(0)
    open val currentPage = _currentPage.asStateFlow()

    fun onNextPage() {
        _currentPage.value = (_currentPage.value + 1).coerceAtMost(2)
    }

    fun setPage(page: Int) {
        _currentPage.value = page
    }
}
