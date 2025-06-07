package hse.diploma.cybersecplatform.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hse.diploma.cybersecplatform.domain.model.AppTheme
import hse.diploma.cybersecplatform.domain.model.Language
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
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _themePreference = MutableStateFlow(AppTheme.SYSTEM)
    val themePreference: StateFlow<AppTheme> = _themePreference.asStateFlow()

    private val _languagePreference = MutableStateFlow(Language.ENGLISH)
    val languagePreference: StateFlow<Language> = _languagePreference.asStateFlow()

    init {
        loadSettings()
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

    companion object {
        private const val TAG = "SettingsViewModel"
    }
}
