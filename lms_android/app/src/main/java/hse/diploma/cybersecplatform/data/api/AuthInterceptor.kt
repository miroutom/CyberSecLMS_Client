package hse.diploma.cybersecplatform.data.api

import okhttp3.Interceptor

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val originalRequest = chain.request()
        val token = tokenManager.getToken()

        return if (!token.isNullOrEmpty()) {
            val newRequest =
                originalRequest.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(originalRequest)
        }
    }
}
