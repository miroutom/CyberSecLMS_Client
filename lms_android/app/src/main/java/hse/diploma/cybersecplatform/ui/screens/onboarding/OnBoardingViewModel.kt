package hse.diploma.cybersecplatform.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class OnBoardingViewModel @Inject constructor() : ViewModel() {
    private val _currentPage = MutableStateFlow(0)
    val currentPage = _currentPage.asStateFlow()

    fun onNextPage() {
        _currentPage.value = (_currentPage.value + 1).coerceAtMost(2)
    }

    fun setPage(page: Int) {
        _currentPage.value = page
    }
}
