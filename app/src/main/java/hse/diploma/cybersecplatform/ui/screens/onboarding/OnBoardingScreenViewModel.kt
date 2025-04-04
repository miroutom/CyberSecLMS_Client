package hse.diploma.cybersecplatform.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

open class OnBoardingScreenViewModel : ViewModel() {

    private val _currentPage = MutableStateFlow(0)
    open val currentPage = _currentPage.asStateFlow().value

    fun onNextPage() {
        _currentPage.value++
    }
}