package hse.diploma.cybersecplatform.data.repository

import hse.diploma.cybersecplatform.data.api.AppPreferencesManager
import hse.diploma.cybersecplatform.domain.model.AppTheme
import hse.diploma.cybersecplatform.domain.model.Language
import hse.diploma.cybersecplatform.domain.repository.SettingsRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepoImpl @Inject constructor(
    private val preferencesManager: AppPreferencesManager,
) : SettingsRepo {
    override fun getThemePreference(): Flow<AppTheme> = preferencesManager.themeFlow

    override suspend fun setThemePreference(theme: AppTheme) {
        preferencesManager.setTheme(theme)
    }

    override fun getLanguagePreference(): Flow<Language> = preferencesManager.languageFlow

    override suspend fun setLanguagePreference(language: Language) {
        preferencesManager.setLanguage(language)
    }
}
