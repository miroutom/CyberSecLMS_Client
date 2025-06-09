package hse.diploma.cybersecplatform.data.api

import hse.diploma.cybersecplatform.data.model.analytics.CourseStatistics
import hse.diploma.cybersecplatform.data.model.analytics.UserStatistics
import hse.diploma.cybersecplatform.data.model.request.*
import hse.diploma.cybersecplatform.data.model.response.*
import hse.diploma.cybersecplatform.data.model.submission.TaskSubmission
import hse.diploma.cybersecplatform.data.model.submission.TaskSubmissionDetails
import hse.diploma.cybersecplatform.data.model.user.LearningPath
import hse.diploma.cybersecplatform.data.model.user.UserData
import hse.diploma.cybersecplatform.data.model.user.UserProgress
import hse.diploma.cybersecplatform.domain.model.Course
import hse.diploma.cybersecplatform.domain.model.Task
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

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

    @POST("api/forgot-password")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest,
    ): Response<TempTokenResponse>

    @POST("api/reset-password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest,
    ): Response<MessageResponse>

    // Profile endpoints
    @GET("api/profile")
    suspend fun getUserProfile(): Response<UserData>

    @PUT("api/profile")
    suspend fun updateProfile(
        @Body userData: UserData,
    ): Response<MessageResponse>

    // Account management
    @POST("api/account/change-password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest,
    ): Response<Map<String, String>>

    @Multipart
    @POST("api/account/profile/image")
    suspend fun uploadAvatar(
        @Part image: MultipartBody.Part,
    ): Response<Map<String, String>>

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
    suspend fun getAllCourses(): Response<List<Course>>

    @GET("api/courses/{id}")
    suspend fun getCourseById(
        @Path("id") courseId: Int,
    ): Response<Course>

    @GET("api/courses/{course_id}/tasks/{task_id}")
    suspend fun getTaskById(
        @Path("course_id") courseId: Int,
        @Path("task_id") taskId: Int,
    ): Response<Task>

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

    @POST("api/progress/{user_id}/tasks/{task_id}/submit")
    suspend fun submitTask(
        @Path("user_id") userId: Int,
        @Path("task_id") taskId: Int,
        @Body submission: TaskSubmission,
    ): Response<TaskSubmissionResponse>

    @GET("api/progress/{user_id}/learning-path")
    suspend fun getLearningPath(
        @Path("user_id") userId: Int,
    ): Response<LearningPath>

    @GET("api/progress/{user_id}/submissions")
    suspend fun getUserSubmissions(
        @Path("user_id") userId: Int,
    ): Response<List<TaskSubmissionDetails>>

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

    // Analytics
    @GET("api/analytics/courses/{course_id}/statistics")
    suspend fun getCourseStatistics(
        @Path("course_id") courseId: Int,
    ): Response<CourseStatistics>

    @GET("api/analytics/users/{user_id}/statistics")
    suspend fun getUserStatistics(
        @Path("user_id") userId: Int,
    ): Response<UserStatistics>
}
