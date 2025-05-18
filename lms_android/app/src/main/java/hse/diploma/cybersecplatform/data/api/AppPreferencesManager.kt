package hse.diploma.cybersecplatform.data.api

import android.content.Context
import hse.diploma.cybersecplatform.domain.model.AppTheme
import hse.diploma.cybersecplatform.domain.model.Language
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferencesManager @Inject constructor(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _themeFlow = MutableStateFlow(getTheme())
    val themeFlow: StateFlow<AppTheme> = _themeFlow

    private val _languageFlow = MutableStateFlow(getLanguage())
    val languageFlow: StateFlow<Language> = _languageFlow

    init {
        prefs.registerOnSharedPreferenceChangeListener { _, key ->
            when (key) {
                KEY_THEME -> _themeFlow.value = getTheme()
                KEY_LANGUAGE -> _languageFlow.value = getLanguage()
            }
        }
    }

    fun isFirstLaunch(): Boolean {
        return prefs.getBoolean(KEY_FIRST_LAUNCH, true)
    }

    fun markAppLaunched() {
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
    }

    private fun getTheme(): AppTheme {
        val themeOrdinal = prefs.getInt(KEY_THEME, AppTheme.SYSTEM.ordinal)
        return AppTheme.entries.toTypedArray().getOrElse(themeOrdinal) { AppTheme.SYSTEM }
    }

    fun setTheme(theme: AppTheme) {
        prefs.edit().putInt(KEY_THEME, theme.ordinal).apply()
    }

    private fun getLanguage(): Language {
        val langOrdinal = prefs.getInt(KEY_LANGUAGE, Language.ENGLISH.ordinal)
        return Language.entries.toTypedArray().getOrElse(langOrdinal) { Language.ENGLISH }
    }

    fun setLanguage(language: Language) {
        prefs.edit().putInt(KEY_LANGUAGE, language.ordinal).apply()
    }

    companion object {
        private const val PREFS_NAME = "app_preferences"
        private const val KEY_FIRST_LAUNCH = "is_first_launch"
        private const val KEY_THEME = "app_theme"
        private const val KEY_LANGUAGE = "app_language"
    }
}
