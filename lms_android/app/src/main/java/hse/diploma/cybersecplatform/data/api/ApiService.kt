package hse.diploma.cybersecplatform.data.api

import hse.diploma.cybersecplatform.data.model.UserData
import hse.diploma.cybersecplatform.data.model.request.ChangePasswordRequest
import hse.diploma.cybersecplatform.data.model.request.ForgotPasswordRequest
import hse.diploma.cybersecplatform.data.model.request.LoginRequest
import hse.diploma.cybersecplatform.data.model.request.RegisterRequest
import hse.diploma.cybersecplatform.data.model.request.ResetPasswordRequest
import hse.diploma.cybersecplatform.data.model.request.VerifyOtpRequest
import hse.diploma.cybersecplatform.data.model.response.LoginResponse
import hse.diploma.cybersecplatform.data.model.response.MessageResponse
import hse.diploma.cybersecplatform.data.model.response.RegisterResponse
import hse.diploma.cybersecplatform.data.model.response.TempTokenResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
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
        @Body userData: UserData,
    ): Response<MessageResponse>

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
    ): Response<MessageResponse>

    @Multipart
    @POST("api/account/profile/image")
    suspend fun uploadAvatar(
        @Part image: MultipartBody.Part,
    ): Response<ResponseBody>

    @POST("api/account/delete")
    suspend fun requestDeleteAccount(): Response<TempTokenResponse>

    @POST("api/account/delete/confirm")
    suspend fun confirmDeleteAccount(
        @Body request: VerifyOtpRequest,
    ): Response<MessageResponse>
}
