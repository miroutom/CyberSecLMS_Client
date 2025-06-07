package hse.diploma.cybersecplatform.data.api

import hse.diploma.cybersecplatform.data.model.request.ChangePasswordRequest
import hse.diploma.cybersecplatform.data.model.request.CreateCourseRequest
import hse.diploma.cybersecplatform.data.model.request.CreateTaskRequest
import hse.diploma.cybersecplatform.data.model.request.DeleteAccountConfirmRequest
import hse.diploma.cybersecplatform.data.model.request.DeleteAccountInitRequest
import hse.diploma.cybersecplatform.data.model.request.ForgotPasswordRequest
import hse.diploma.cybersecplatform.data.model.request.LoginRequest
import hse.diploma.cybersecplatform.data.model.request.RegisterRequest
import hse.diploma.cybersecplatform.data.model.request.ResetPasswordRequest
import hse.diploma.cybersecplatform.data.model.request.UpdateCourseRequest
import hse.diploma.cybersecplatform.data.model.request.UpdateTaskRequest
import hse.diploma.cybersecplatform.data.model.request.VerifyOtpRequest
import hse.diploma.cybersecplatform.data.model.response.AllCoursesResponse
import hse.diploma.cybersecplatform.data.model.response.LoginResponse
import hse.diploma.cybersecplatform.data.model.response.MessageResponse
import hse.diploma.cybersecplatform.data.model.response.MyCoursesResponse
import hse.diploma.cybersecplatform.data.model.response.RegisterResponse
import hse.diploma.cybersecplatform.data.model.response.TempTokenResponse
import hse.diploma.cybersecplatform.data.model.user.UserData
import hse.diploma.cybersecplatform.data.model.user.UserProgress
import hse.diploma.cybersecplatform.domain.model.Course
import hse.diploma.cybersecplatform.domain.model.Task
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    // Auth endpoints
    @POST("api/login")
    suspend fun login(
        @Body loginRequest: LoginRequest,
    ): Response<TempTokenResponse>

    @POST("api/register")
    suspend fun register(
        @Body registerRequest: RegisterRequest,
    ): Response<RegisterResponse>

    @POST("api/verify-otp")
    suspend fun verifyOtp(
        @Body request: VerifyOtpRequest,
    ): Response<LoginResponse>

    // Profile endpoints
    @GET("api/profile")
    suspend fun getUserProfile(): Response<UserData>

    @PUT("api/profile")
    suspend fun updateProfile(
        @Body userData: UserData,
    ): Response<MessageResponse>

    @POST("api/forgot-password")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest,
    ): Response<TempTokenResponse>

    @POST("api/reset-password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest,
    ): Response<MessageResponse>

    @POST("api/account/change-password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest,
    ): Response<Map<String, String>>

    @Multipart
    @POST("api/account/profile/image")
    suspend fun uploadAvatar(
        @Part image: MultipartBody.Part,
    ): Response<ResponseBody>

    @POST("api/account/delete")
    suspend fun requestDeleteAccount(
        @Body request: DeleteAccountInitRequest,
    ): Response<TempTokenResponse>

    @POST("api/account/delete/confirm")
    suspend fun confirmDeleteAccount(
        @Body request: DeleteAccountConfirmRequest,
    ): Response<MessageResponse>

    // Course endpoints
    @GET("api/courses")
    suspend fun getAllCourses(): Response<AllCoursesResponse>

    @GET("api/my-courses")
    suspend fun getMyCourses(): Response<MyCoursesResponse>

    @GET("api/courses/{id}")
    suspend fun getCourseById(
        @Path("id") courseId: Int,
    ): Response<Course>

    // Progress endpoints
    @GET("api/progress/{user_id}")
    suspend fun getUserProgress(
        @Path("user_id") userId: Int,
    ): Response<UserProgress>

    @POST("api/progress/{user_id}/tasks/{task_id}/complete")
    suspend fun completeTask(
        @Path("user_id") userId: Int,
        @Path("task_id") taskId: Int,
    ): Response<MessageResponse>

    // Course management (for teachers)
    @POST("api/courses")
    suspend fun createCourse(
        @Body course: CreateCourseRequest,
    ): Response<Course>

    @PUT("api/courses/{id}")
    suspend fun updateCourse(
        @Path("id") courseId: Int,
        @Body course: UpdateCourseRequest,
    ): Response<Course>

    @DELETE("api/courses/{id}")
    suspend fun deleteCourse(
        @Path("id") courseId: Int,
    ): Response<MessageResponse>

    // Task management (for teachers)
    @POST("api/courses/{course_id}/tasks")
    suspend fun createTask(
        @Path("course_id") courseId: Int,
        @Body task: CreateTaskRequest,
    ): Response<Task>

    @PUT("api/courses/{course_id}/tasks/{task_id}")
    suspend fun updateTask(
        @Path("course_id") courseId: Int,
        @Path("task_id") taskId: Int,
        @Body task: UpdateTaskRequest,
    ): Response<Task>

    @DELETE("api/courses/{course_id}/tasks/{task_id}")
    suspend fun deleteTask(
        @Path("course_id") courseId: Int,
        @Path("task_id") taskId: Int,
    ): Response<MessageResponse>
}
