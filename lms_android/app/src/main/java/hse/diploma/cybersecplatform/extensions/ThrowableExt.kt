package hse.diploma.cybersecplatform.extensions

import hse.diploma.cybersecplatform.domain.model.ErrorType
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun Throwable.toErrorType(): ErrorType =
    when (this) {
        is UnknownHostException, is ConnectException, is SocketTimeoutException -> ErrorType.NoInternet
        is HttpException -> ErrorType.Server(code = this.code(), msg = this.message())
        else -> ErrorType.Other
    }
