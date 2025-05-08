package hse.diploma.cybersecplatform.data.model

data class VerifyOtpRequest(
    val otp: String,
    val tempToken: String,
)
