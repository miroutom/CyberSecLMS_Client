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
//    @GET("api/my-courses")
//    suspend fun getMyCourses(): Response<List<Course>>

    // TODO: replace with real api
//    @GET("api/all-courses")
//    suspend fun getAllCourses(): Response<List<Course>>

    // TODO: replace with real api
//    @POST("api/profile")
//    suspend fun updateProfile(@Body user: UserData): Response<Unit>

    // TODO: replace with real api
//    @POST("api/change_password")
//    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<Unit>
}
