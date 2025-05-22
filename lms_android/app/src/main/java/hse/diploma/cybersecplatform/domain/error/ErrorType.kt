package hse.diploma.cybersecplatform.domain.error

sealed class ErrorType {
    data object NoInternet : ErrorType()

    data class Server(val code: Int? = null, val msg: String? = null) : ErrorType()

    data object Other : ErrorType()
}
