package hse.diploma.cybersecplatform.data.api

import hse.diploma.cybersecplatform.data.model.LoginRequest
import hse.diploma.cybersecplatform.data.model.LoginResponse
import hse.diploma.cybersecplatform.data.model.RegisterRequest
import hse.diploma.cybersecplatform.data.model.RegisterResponse
import hse.diploma.cybersecplatform.data.model.TempTokenResponse
import hse.diploma.cybersecplatform.data.model.UserData
import hse.diploma.cybersecplatform.data.model.VerifyOtpRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("api/login")
    suspend fun login(
        @Body loginRequest: LoginRequest,
    ): Response<TempTokenResponse>

    @POST("api/register")
    suspend fun register(
        @Body registerRequest: RegisterRequest,
    ): Response<RegisterResponse>

    @GET("api/profile")
    suspend fun getUserProfile(): Response<UserData>

    @POST("api/verify-otp")
    suspend fun verifyOtp(
        @Body request: VerifyOtpRequest,
    ): Response<LoginResponse>

    // TODO: replace with real api
//    @GET("mock/my-courses")
//    suspend fun getMyCourses(): Response<List<Course>> = Response.success(mockCourses)

    // TODO: replace with real api
//    @GET("mock/all-courses")
//    suspend fun getAllCourses(): Response<List<Course>> = Response.success(mockAllCourses)
}
