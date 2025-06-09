package hse.diploma.cybersecplatform.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.ui.model.Difficulty
import hse.diploma.cybersecplatform.ui.model.VulnerabilityType
import hse.diploma.cybersecplatform.ui.model.VulnerabilityType.CSRF
import hse.diploma.cybersecplatform.ui.model.VulnerabilityType.SQL
import hse.diploma.cybersecplatform.ui.model.VulnerabilityType.XSS
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

enum class PasswordError {
    NONE,
    LENGTH,
    NO_NUMBERS,
    NO_LOWERCASE,
    NO_UPPERCASE,
    NO_SYMBOLS,
}

fun isEmailValid(login: String): Boolean {
    val emailRegex =
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)+$"
    return login.matches(emailRegex.toRegex())
}

fun isPasswordValid(password: String): Boolean {
    val passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#\$%^&.*]).{8,}$"
    return password.matches(passwordRegex.toRegex())
}

fun getPasswordError(password: String): PasswordError {
    return when {
        password.length < 8 -> PasswordError.LENGTH
        !password.contains(Regex("[0-9]")) -> PasswordError.NO_NUMBERS
        !password.contains(Regex("[a-z]")) -> PasswordError.NO_LOWERCASE
        !password.contains(Regex("[A-Z]")) -> PasswordError.NO_UPPERCASE
        !password.contains(Regex("[!@#\$%^&.*]")) -> PasswordError.NO_SYMBOLS
        else -> PasswordError.NONE
    }
}

@Composable
fun getPasswordErrorMessage(password: String): String {
    return when (getPasswordError(password)) {
        PasswordError.LENGTH -> stringResource(R.string.auth_password_error_length)
        PasswordError.NO_NUMBERS -> stringResource(R.string.auth_password_error_no_numbers)
        PasswordError.NO_LOWERCASE -> stringResource(R.string.auth_password_error_no_lowercase_letters)
        PasswordError.NO_UPPERCASE -> stringResource(R.string.auth_password_error_no_uppercase_letters)
        PasswordError.NO_SYMBOLS -> stringResource(R.string.auth_password_error_no_symbols)
        PasswordError.NONE -> ""
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

fun formatIsoDateToReadable(isoDate: String?): String {
    if (isoDate.isNullOrEmpty()) return "N/A"

    return try {
        val instant = Instant.parse(isoDate)
        val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault())
        dateTime.format(formatter)
    } catch (e: Exception) {
        logE("Utils", "Error parsing ISO date: $isoDate", e)
        "N/A"
    }
}

fun String?.toVulnerabilityType(): VulnerabilityType {
    return when (this) {
        "XSS" -> XSS
        "CSRF" -> CSRF
        "SQL" -> SQL
        else -> XSS
    }
}

fun String?.toDifficulty(): Difficulty {
    return when (this) {
        "easy" -> Difficulty.EASY
        "medium" -> Difficulty.MEDIUM
        "hard" -> Difficulty.HARD
        else -> Difficulty.EASY
    }
}
