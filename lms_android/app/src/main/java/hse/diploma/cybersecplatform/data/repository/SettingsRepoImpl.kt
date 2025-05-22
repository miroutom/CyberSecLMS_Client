package hse.diploma.cybersecplatform.data.repository

import hse.diploma.cybersecplatform.data.api.ApiService
import hse.diploma.cybersecplatform.data.api.AppPreferencesManager
import hse.diploma.cybersecplatform.data.model.request.ChangePasswordRequest
import hse.diploma.cybersecplatform.data.model.response.MessageResponse
import hse.diploma.cybersecplatform.data.model.response.TempTokenResponse
import hse.diploma.cybersecplatform.data.model.request.VerifyOtpRequest
import hse.diploma.cybersecplatform.domain.model.AppTheme
import hse.diploma.cybersecplatform.domain.model.Language
import hse.diploma.cybersecplatform.domain.repository.SettingsRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepoImpl @Inject constructor(
    private val preferencesManager: AppPreferencesManager,
    private val apiService: ApiService,
) : SettingsRepo {
    override fun getThemePreference(): Flow<AppTheme> = preferencesManager.themeFlow

    override suspend fun setThemePreference(theme: AppTheme) {
        preferencesManager.setTheme(theme)
    }

    override fun getLanguagePreference(): Flow<Language> = preferencesManager.languageFlow

    override suspend fun setLanguagePreference(language: Language) {
        preferencesManager.setLanguage(language)
    }

    override suspend fun initiatePasswordUpdate(
        currentPassword: String,
        newPassword: String,
    ): Result<TempTokenResponse> {
        return try {
            val request =
                ChangePasswordRequest(
                    currentPassword = currentPassword,
                    newPassword = newPassword,
                )
            val response = apiService.changePassword(request)

            if (response.isSuccessful) {
                val tempToken = response.body()?.get("tempToken")
                if (tempToken != null) {
                    Result.success(
                        TempTokenResponse(
                            tempToken = tempToken,
                            message = "OTP requested",
                        ),
                    )
                } else {
                    Result.failure(Exception("No temporary token returned"))
                }
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Failed to update password"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun confirmPasswordUpdate(
        otpValue: String,
        tempToken: String,
    ): Result<MessageResponse> {
        return try {
            val request =
                VerifyOtpRequest(
                    otp = otpValue,
                    tempToken = tempToken,
                )
            val response = apiService.verifyOtp(request)

            if (response.isSuccessful) {
                Result.success(MessageResponse(message = "Password updated successfully"))
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Failed to verify OTP"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
