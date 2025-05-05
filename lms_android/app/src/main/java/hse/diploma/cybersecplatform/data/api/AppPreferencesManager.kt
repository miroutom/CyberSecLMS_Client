package hse.diploma.cybersecplatform.data.api

import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferencesManager @Inject constructor(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isFirstLaunch(): Boolean {
        return prefs.getBoolean(KEY_FIRST_LAUNCH, true)
    }

    fun markAppLaunched() {
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
    }

    companion object {
        private const val PREFS_NAME = "app_preferences"
        private const val KEY_FIRST_LAUNCH = "is_first_launch"
    }
}
