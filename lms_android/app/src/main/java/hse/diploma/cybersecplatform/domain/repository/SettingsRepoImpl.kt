package hse.diploma.cybersecplatform.domain.repository

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import hse.diploma.cybersecplatform.data.api.ApiService
import hse.diploma.cybersecplatform.data.model.ChangePasswordRequest
import hse.diploma.cybersecplatform.data.model.UserData
import retrofit2.Response
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepoImpl @Inject constructor(
    private val prefs: SharedPreferences,
    private val apiService: ApiService,
) : SettingsRepo {

    override fun getNightMode(): Int {
        val modeInt = prefs.getInt(KEY_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        return modeInt
    }

    override fun setNightMode(@AppCompatDelegate.NightMode nightMode: Int) {
        prefs.edit().putInt(KEY_THEME, nightMode).apply()
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }

    override fun getLocale(): Locale {
        val languageCode = prefs.getString(KEY_LOCALE, java.util.Locale.getDefault().language) ?: java.util.Locale.getDefault().language
        return Locale(languageCode)
    }

    override fun setLocale(locale: Locale) {
        prefs.edit().putString(KEY_LOCALE, locale.language).apply()
    }


    override suspend fun updateUserProfile(user: UserData): Result<Unit> {
        return try {
            // TODO: replace with api service
            val response = Response.success(user)
            if (response.isSuccessful) {
                val newUserData = response.body()
                if (newUserData != null) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to update profile"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePassword(oldPassword: String, newPassword: String): Result<Unit> {
        return try {
            // TODO: replace with api service
            val response = Response.success(ChangePasswordRequest(oldPassword, newPassword))
            if (response.isSuccessful) {
                val passwordRequest = response.body()
                if (passwordRequest != null) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to update password"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        private const val KEY_THEME = "key_theme"
        private const val KEY_LOCALE = "key_locale"
    }

}
