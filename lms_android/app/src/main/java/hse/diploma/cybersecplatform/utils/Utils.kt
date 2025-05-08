package hse.diploma.cybersecplatform.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import hse.diploma.cybersecplatform.R

fun isLoginValidAndAuthMethodType(login: String): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    return login.matches(emailRegex.toRegex())
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

fun maskEmail(email: String): String {
    val atIndex = email.indexOf('@')
    if (atIndex <= 0) return "*".repeat(email.length)
    val namePart = email.substring(0, atIndex)
    val domainPart = email.substring(atIndex + 1)
    val nb = namePart.length
    return if (nb <= 2) {
        namePart.first() + "*".repeat(nb - 1) + "@" + domainPart
    } else {
        namePart.first() + "*".repeat(nb - 2) + namePart.last() + "@" + domainPart
    }
}
