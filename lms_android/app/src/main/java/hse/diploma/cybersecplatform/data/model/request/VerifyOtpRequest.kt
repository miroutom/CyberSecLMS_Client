package hse.diploma.cybersecplatform.data.model.request

data class VerifyOtpRequest(
    val otp: String,
    val tempToken: String,
)
