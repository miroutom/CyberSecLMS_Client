package hse.diploma.cybersecplatform.domain.repository

import androidx.appcompat.app.AppCompatDelegate.NightMode
import hse.diploma.cybersecplatform.data.model.UserData
import java.util.Locale

interface SettingsRepo {
    fun getNightMode(): Int
    fun setNightMode(@NightMode nightMode: Int)

    fun getLocale(): Locale
    fun setLocale(locale: Locale)

    suspend fun updateUserProfile(user: UserData): Result<Unit>

    suspend fun updatePassword(oldPassword: String, newPassword: String): Result<Unit>
}
