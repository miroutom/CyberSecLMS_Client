package hse.diploma.cybersecplatform.data.api

import hse.diploma.cybersecplatform.data.model.ChangePasswordRequest
import hse.diploma.cybersecplatform.data.model.ForgotPasswordRequest
import hse.diploma.cybersecplatform.data.model.LoginRequest
import hse.diploma.cybersecplatform.data.model.LoginResponse
import hse.diploma.cybersecplatform.data.model.RegisterRequest
import hse.diploma.cybersecplatform.data.model.RegisterResponse
import hse.diploma.cybersecplatform.data.model.ResetPasswordRequest
import hse.diploma.cybersecplatform.data.model.SuccessResponse
import hse.diploma.cybersecplatform.data.model.TempTokenResponse
import hse.diploma.cybersecplatform.data.model.UpdateProfileRequest
import hse.diploma.cybersecplatform.data.model.UserData
import hse.diploma.cybersecplatform.data.model.VerifyOtpRequest
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

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

    @PUT("api/profile")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest,
    ): Response<UserData>

    @POST("api/account/change-password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest,
    ): Response<Map<String, String>>

    @POST("api/forgot-password")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest,
    ): Response<TempTokenResponse>

    @POST("api/reset-password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest,
    ): Response<SuccessResponse>

    @Multipart
    @POST("api/account/profile/image")
    suspend fun uploadAvatar(
        @Part avatar: MultipartBody.Part,
    ): Response<Map<String, String>>

    @POST("api/account/delete")
    suspend fun requestDeleteAccount(): Response<TempTokenResponse>

    @POST("api/account/delete/confirm")
    suspend fun confirmDeleteAccount(
        @Body request: VerifyOtpRequest,
    ): Response<SuccessResponse>
}
