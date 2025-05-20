package hse.diploma.cybersecplatform.extensions

import hse.diploma.cybersecplatform.BuildConfig.BASE_URL

fun String.addTimestamp(): String = this + "?ts=" + System.currentTimeMillis()

fun String.toAbsoluteUrl() = if (startsWith("http")) this else "$BASE_URL$this"
