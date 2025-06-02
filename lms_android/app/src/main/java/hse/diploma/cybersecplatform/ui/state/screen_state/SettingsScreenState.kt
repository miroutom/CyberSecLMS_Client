package hse.diploma.cybersecplatform.ui.state.screen_state

import hse.diploma.cybersecplatform.data.model.UserData
import hse.diploma.cybersecplatform.domain.model.AppTheme
import hse.diploma.cybersecplatform.domain.model.Language

data class SettingsScreenState(
    val isLoading: Boolean = false,
    val theme: AppTheme = AppTheme.SYSTEM,
    val language: Language = Language.ENGLISH,
    val user: UserData? = null,
    val passwordTempToken: String? = null,
    val deleteTempToken: String? = null,
    val passwordOtpError: String? = null,
    val deleteOtpError: String? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null,
) {
    fun onErrorDismissed() = copy(errorMessage = null)

    fun onSuccessMessageShown() = copy(successMessage = null)
}
