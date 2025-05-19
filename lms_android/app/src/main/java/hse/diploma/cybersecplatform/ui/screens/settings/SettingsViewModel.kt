package hse.diploma.cybersecplatform.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hse.diploma.cybersecplatform.data.model.UserData
import hse.diploma.cybersecplatform.domain.model.AppTheme
import hse.diploma.cybersecplatform.domain.model.Language
import hse.diploma.cybersecplatform.domain.repository.AuthRepo
import hse.diploma.cybersecplatform.domain.repository.SettingsRepo
import hse.diploma.cybersecplatform.domain.repository.UserRepo
import hse.diploma.cybersecplatform.utils.logD
import hse.diploma.cybersecplatform.utils.logE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val settingsRepo: SettingsRepo,
    private val userRepo: UserRepo,
    private val authRepo: AuthRepo,
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _themePreference = MutableStateFlow(AppTheme.SYSTEM)
    val themePreference: StateFlow<AppTheme> = _themePreference.asStateFlow()

    private val _languagePreference = MutableStateFlow(Language.ENGLISH)
    val languagePreference: StateFlow<Language> = _languagePreference.asStateFlow()

    private val _user = MutableStateFlow<UserData?>(null)
    val user: StateFlow<UserData?> = _user.asStateFlow()

    private val _passwordTempToken = MutableStateFlow<String?>(null)
    val passwordTempToken: StateFlow<String?> = _passwordTempToken.asStateFlow()

    private val _deleteTempToken = MutableStateFlow<String?>(null)
    val deleteTempToken: StateFlow<String?> = _deleteTempToken.asStateFlow()

    private val _passwordOtpError = MutableStateFlow<String?>(null)
    val passwordOtpError: StateFlow<String?> = _passwordOtpError.asStateFlow()

    private val _deleteOtpError = MutableStateFlow<String?>(null)
    val deleteOtpError: StateFlow<String?> = _deleteOtpError.asStateFlow()

    init {
        loadSettings()
        loadUserData()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            settingsRepo.getThemePreference().collect { theme ->
                _themePreference.value = theme
            }
        }

        viewModelScope.launch {
            settingsRepo.getLanguagePreference().collect { language ->
                _languagePreference.value = language
            }
        }
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                userRepo.getUserProfile().onSuccess { userData ->
                    _user.value = userData
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setThemePreference(theme: AppTheme) {
        viewModelScope.launch {
            try {
                logD(TAG, "Setting theme to: ${theme.name}")
                settingsRepo.setThemePreference(theme)
            } catch (e: Exception) {
                logE(TAG, "Error setting theme", e)
            }
        }
    }

    fun setLanguagePreference(language: Language) {
        viewModelScope.launch {
            try {
                logD(TAG, "Setting language to: ${language.name}")
                settingsRepo.setLanguagePreference(language)
            } catch (e: Exception) {
                logE(TAG, "Error setting language", e)
            }
        }
    }

    fun initiatePasswordChange(
        currentPassword: String,
        newPassword: String,
        onResult: (Result<String>) -> Unit,
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _passwordOtpError.value = null

            try {
                settingsRepo.initiatePasswordUpdate(currentPassword, newPassword).onSuccess { response ->
                    _passwordTempToken.value = response.tempToken
                    onResult(Result.success("OTP sent to your email"))
                }.onFailure { error ->
                    onResult(Result.failure(error))
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun verifyPasswordOtp(
        otpCode: String,
        onResult: (Result<String>) -> Unit,
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _passwordOtpError.value = null

            try {
                val tempToken = _passwordTempToken.value ?: throw Exception("No temporary token")

                settingsRepo.confirmPasswordUpdate(otpCode, tempToken).onSuccess { response ->
                    _passwordTempToken.value = null
                    onResult(Result.success(response.message))
                }.onFailure { error ->
                    _passwordOtpError.value = error.message ?: "Invalid OTP"
                    onResult(Result.failure(error))
                }
            } catch (e: Exception) {
                _passwordOtpError.value = e.message
                onResult(Result.failure(e))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun cancelPasswordOtp() {
        _passwordTempToken.value = null
        _passwordOtpError.value = null
    }

    fun initiateAccountDeletion(onResult: (Result<String>) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _deleteOtpError.value = null

            try {
                authRepo.requestDeleteAccount().onSuccess { response ->
                    _deleteTempToken.value = response.tempToken
                    onResult(Result.success("OTP sent to your email"))
                }.onFailure { error ->
                    onResult(Result.failure(error))
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
            _deleteOtpError.value = null

            try {
                val tempToken = _deleteTempToken.value ?: throw Exception("No temporary token")

                authRepo.confirmDeleteAccount(otpCode, tempToken).onSuccess { response ->
                    _deleteTempToken.value = null
                    onResult(Result.success(response.message))
                }.onFailure { error ->
                    _deleteOtpError.value = error.message ?: "Invalid OTP"
                    onResult(Result.failure(error))
                }
            } catch (e: Exception) {
                _deleteOtpError.value = e.message
                onResult(Result.failure(e))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun cancelDeleteOtp() {
        _deleteTempToken.value = null
        _deleteOtpError.value = null
    }

    companion object {
        private const val TAG = "SettingsViewModel"
    }
}

data class UpdatePasswordUiState(
    val newPassword: String,
)
