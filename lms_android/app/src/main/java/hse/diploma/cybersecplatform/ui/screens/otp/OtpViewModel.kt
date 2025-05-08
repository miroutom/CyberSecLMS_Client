package hse.diploma.cybersecplatform.ui.screens.otp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hse.diploma.cybersecplatform.data.model.LoginResponse
import hse.diploma.cybersecplatform.domain.repository.AuthRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class OtpViewModel @Inject constructor(
    private val authRepo: AuthRepo,
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun verifyOtp(
        tempToken: String,
        otp: String,
        onResult: (Result<LoginResponse>) -> Unit,
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                onResult(authRepo.verifyOtp(otp, tempToken))
            } finally {
                _isLoading.value = false
            }
        }
    }
}
