package hse.diploma.cybersecplatform.utils

import kotlinx.coroutines.delay

/**
 * The extension that performs the operation with repeated attempts
 * @param maxRetries maximum number of attempts
 * @param initialDelayMs initial delay between attempts in milliseconds
 * @param maxDelayMs maximum delay between attempts
 * @param factor multiplier to increase the waiting time between attempts
 * @param block operation to be performed
 */
suspend fun <T> retry(
    maxRetries: Int = 3,
    initialDelayMs: Long = 200,
    maxDelayMs: Long = 5000,
    factor: Double = 2.0,
    block: suspend () -> Result<T>,
): Result<T> {
    var currentDelay = initialDelayMs
    repeat(maxRetries - 1) {
        val result = block()
        if (result.isSuccess) return result

        currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelayMs)
        delay(currentDelay)
    }

    return block()
}
