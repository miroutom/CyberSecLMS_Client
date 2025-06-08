package hse.diploma.cybersecplatform.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hse.diploma.cybersecplatform.domain.model.AppTheme
import hse.diploma.cybersecplatform.domain.model.Language
import hse.diploma.cybersecplatform.domain.repository.AuthRepo
import hse.diploma.cybersecplatform.domain.repository.SettingsRepo
import hse.diploma.cybersecplatform.utils.logD
import hse.diploma.cybersecplatform.utils.logE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val settingsRepo: SettingsRepo,
    private val authRepo: AuthRepo,
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _themePreference = MutableStateFlow(AppTheme.SYSTEM)
    val themePreference: StateFlow<AppTheme> = _themePreference.asStateFlow()

    private val _languagePreference = MutableStateFlow(Language.ENGLISH)
    val languagePreference: StateFlow<Language> = _languagePreference.asStateFlow()

    private val _deleteTempToken = MutableStateFlow<String?>(null)
    val deleteTempToken: StateFlow<String?> = _deleteTempToken.asStateFlow()

    private val _deleteOtpError = MutableStateFlow<String?>(null)
    val deleteOtpError: StateFlow<String?> = _deleteOtpError.asStateFlow()

    init {
        loadPreferences()
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            try {
                settingsRepo.getThemePreference().collect { theme ->
                    _themePreference.value = theme
                    logD(TAG, "Theme preference loaded: $theme")
                }
                settingsRepo.getLanguagePreference().collect { language ->
                    _languagePreference.value = language
                    logD(TAG, "Language preference loaded: $language")
                }
            } catch (e: Exception) {
                logE(TAG, "Error loading preferences", e)
            }
        }
    }

    fun setThemePreference(theme: AppTheme) {
        viewModelScope.launch {
            logD(TAG, "Setting theme preference to: $theme")
            _isLoading.value = true
            try {
                settingsRepo.setThemePreference(theme)
                _themePreference.value = theme
                logD(TAG, "Theme preference updated successfully")
            } catch (e: Exception) {
                logE(TAG, "Failed to set theme preference", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setLanguagePreference(language: Language) {
        viewModelScope.launch {
            logD(TAG, "Setting language preference to: $language")
            _isLoading.value = true
            try {
                settingsRepo.setLanguagePreference(language)
                _languagePreference.value = language
                logD(TAG, "Language preference updated successfully")
            } catch (e: Exception) {
                logE(TAG, "Failed to set language preference", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun initiatePasswordChange(
        currentPassword: String,
        newPassword: String,
        onResult: (Result<String>) -> Unit,
    ) {
        viewModelScope.launch {
            logD(TAG, "Initiating password change...")
            _isLoading.value = true
            try {
                authRepo.changePassword(currentPassword, newPassword).onSuccess { response ->
                    logD(TAG, "Password changed successfully")
                    onResult(Result.success("Password changed successfully"))
                }.onFailure { e ->
                    logE(TAG, "Password change failed", e)
                    onResult(Result.failure(e))
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun initiateAccountDeletion(
        password: String,
        onResult: (Result<String>) -> Unit,
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                authRepo.requestDeleteAccount(password).onSuccess { response ->
                    onResult(Result.success("Account deletion initiated. Check your email for confirmation code."))
                }.onFailure { e ->
                    onResult(Result.failure(e))
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun confirmAccountDeletion(
        otpCode: String,
        onResult: (Result<String>) -> Unit,
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                authRepo.confirmDeleteAccount(otpCode).onSuccess {
                    onResult(Result.success("Account deleted successfully"))
                }.onFailure { e ->
                    onResult(Result.failure(e))
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun cancelDeleteOtp() {
        logD(TAG, "Cancelling account deletion OTP flow")
        _deleteTempToken.value = null
        _deleteOtpError.value = null
    }

    companion object {
        private const val TAG = "SettingsViewModel"
    }
}
