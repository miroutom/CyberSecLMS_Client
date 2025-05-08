package hse.diploma.cybersecplatform.data.api

import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    context: Context,
) {
    private val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        sharedPreferences.edit().putString(TOKEN_KEY, token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(TOKEN_KEY, null)
    }

    fun clearToken() {
        sharedPreferences.edit().remove(TOKEN_KEY).apply()
    }

    fun hasToken(): Boolean {
        return !getToken().isNullOrEmpty()
    }

    companion object {
        private const val TOKEN_KEY = "auth_token"
    }
}
