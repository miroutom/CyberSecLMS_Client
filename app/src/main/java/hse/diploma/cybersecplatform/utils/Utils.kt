package hse.diploma.cybersecplatform.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import hse.diploma.cybersecplatform.R

enum class AuthMethodType {
    PHONE, EMAIL
}

fun isLoginValidAndAuthMethodType(login: String): Pair<Boolean, AuthMethodType> {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    val phoneRegex = "^((\\+7|8)[\\s-]?)?(\\(?\\d{3}\\)?[\\s-]?)?[\\d\\s-]{7}$"

    val isPhoneValid = login.matches(phoneRegex.toRegex())
    val isEmailValid = login.matches(emailRegex.toRegex())

    val authMethodType = if (isPhoneValid) AuthMethodType.PHONE else AuthMethodType.EMAIL

    return Pair(isPhoneValid || isEmailValid, authMethodType)
}

fun isPasswordValid(password: String): Boolean {
    val passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#\$%^&.*]).{8,}$"
    return password.matches(passwordRegex.toRegex())
}

@Composable
fun getPasswordErrorMessage(password: String): String {
    return when {
        password.length < 8 -> stringResource(R.string.auth_password_error_length)
        !password.contains(Regex("[0-9]")) -> stringResource(R.string.auth_password_error_no_numbers)
        !password.contains(Regex("[a-z]")) -> stringResource(R.string.auth_password_error_no_lowercase_letters)
        !password.contains(Regex("[A-Z]")) -> stringResource(R.string.auth_password_error_no_uppercase_letters)
        else -> stringResource(R.string.auth_password_error_no_symbols)
    }
}
