package hse.diploma.cybersecplatform.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hse.diploma.cybersecplatform.data.model.UserData
import hse.diploma.cybersecplatform.domain.model.ErrorType
import hse.diploma.cybersecplatform.domain.repository.SettingsRepo
import hse.diploma.cybersecplatform.extensions.toErrorType
import hse.diploma.cybersecplatform.ui.state.UpdateProfileState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

class SettingsScreenViewModel @Inject constructor(
    private val settingsRepo: SettingsRepo
) : ViewModel() {
    private val _locale = MutableStateFlow(settingsRepo.getLocale())
    val locale = _locale.asStateFlow()

    private val _updateProfileState = MutableStateFlow<UpdateProfileState>(UpdateProfileState.Loading)
    val updateProfileState: StateFlow<UpdateProfileState> = _updateProfileState.asStateFlow()

    private val _updatePasswordState = MutableStateFlow<UpdateProfileState>(UpdateProfileState.Loading)
    val updatePasswordState: StateFlow<UpdateProfileState> = _updatePasswordState.asStateFlow()

    fun setLocale(locale: Locale) {
        settingsRepo.setLocale(locale)
        _locale.value = locale
    }

    fun setNightMode(mode: Int) {
        settingsRepo.setNightMode(mode)
    }

    fun updateProfile(userData: UserData) {
        viewModelScope.launch {
            _updateProfileState.value = UpdateProfileState.Loading
            val result = settingsRepo.updateUserProfile(userData)
            _updateProfileState.value = if (result.isSuccess) UpdateProfileState.Success
            else UpdateProfileState.Error(result.exceptionOrNull()?.toErrorType(TAG) ?: ErrorType.Other)
        }
    }

    fun updatePassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            _updatePasswordState.value = UpdateProfileState.Loading
            val result = settingsRepo.updatePassword(oldPassword, newPassword)
            _updatePasswordState.value = if (result.isSuccess) UpdateProfileState.Success
            else UpdateProfileState.Error(result.exceptionOrNull()?.toErrorType(TAG) ?: ErrorType.Other)
        }
    }

    companion object {
        const val TAG = "SettingsScreenViewModel"
    }
}
