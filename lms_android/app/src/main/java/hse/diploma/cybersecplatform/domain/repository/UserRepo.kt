package hse.diploma.cybersecplatform.domain.repository

import android.content.ContentResolver
import android.net.Uri
import hse.diploma.cybersecplatform.data.model.analytics.UserStatistics
import hse.diploma.cybersecplatform.data.model.response.MessageResponse
import hse.diploma.cybersecplatform.data.model.user.UserData
import hse.diploma.cybersecplatform.data.model.user.UserProgress

interface UserRepo {
    suspend fun getUserProfile(): Result<UserData>

    suspend fun updateProfile(userData: UserData): Result<MessageResponse>

    suspend fun uploadAvatar(
        contentUri: Uri,
        contentResolver: ContentResolver,
    ): Result<UserData>

    suspend fun getUserProgress(userId: Int): Result<UserProgress>

    suspend fun completeTask(
        userId: Int,
        taskId: Int,
    ): Result<MessageResponse>

    suspend fun getUserStatistics(userId: Int): Result<UserStatistics>
}
