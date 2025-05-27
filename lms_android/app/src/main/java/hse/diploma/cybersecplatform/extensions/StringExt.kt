package hse.diploma.cybersecplatform.extensions

import hse.diploma.cybersecplatform.BuildConfig.UPLOADS_URL

fun String.addTimestamp(): String = this + "?ts=" + System.currentTimeMillis()

fun String.toAbsoluteUrl() = if (startsWith("https")) this else "$UPLOADS_URL$this"
