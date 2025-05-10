package hse.diploma.cybersecplatform.domain.model

import androidx.annotation.StringRes
import hse.diploma.cybersecplatform.R

enum class Difficulty(
    @StringRes val value: Int,
) {
    EASY(R.string.easy_level),
    MEDIUM(R.string.medium_level),
    HARD(R.string.hard_level),
}
