package hse.diploma.cybersecplatform.utils

import android.util.Log

fun logD(
    tag: String,
    message: String,
) {
    Log.d(tag, message)
}

fun logE(
    tag: String,
    message: String,
    error: Throwable,
) {
    Log.e(tag, message, error)
}

fun logI(
    tag: String,
    message: String,
) {
    Log.i(tag, message)
}

fun logW(
    tag: String,
    message: String,
) {
    Log.w(tag, message)
}

fun logV(
    tag: String,
    message: String,
) {
    Log.v(tag, message)
}
