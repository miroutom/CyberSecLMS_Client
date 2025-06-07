package hse.diploma.cybersecplatform.data.repository

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import androidx.annotation.VisibleForTesting
import hse.diploma.cybersecplatform.data.api.ApiService
import hse.diploma.cybersecplatform.data.model.analytics.UserStatistics
import hse.diploma.cybersecplatform.data.model.response.MessageResponse
import hse.diploma.cybersecplatform.data.model.user.UserData
import hse.diploma.cybersecplatform.data.model.user.UserProgress
import hse.diploma.cybersecplatform.domain.repository.UserRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepoImpl @Inject constructor(
    private val apiService: ApiService,
) : UserRepo {
    override suspend fun getUserProfile(): Result<UserData> {
        return try {
            val response = apiService.getUserProfile()
            if (response.isSuccessful) {
                val user = response.body()
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to load profile"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProfile(userData: UserData): Result<MessageResponse> {
        return try {
            val response = apiService.updateProfile(userData)

            if (response.isSuccessful) {
                val message = response.body()
                if (message != null) {
                    Result.success(message)
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to update profile"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserProgress(userId: Int): Result<UserProgress> {
        return try {
            val response = apiService.getUserProgress(userId)

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to get progress"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun completeTask(
        userId: Int,
        taskId: Int,
    ): Result<MessageResponse> {
        return try {
            val response = apiService.completeTask(userId, taskId)

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to complete task"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadAvatar(
        contentUri: Uri,
        contentResolver: ContentResolver,
    ): Result<UserData> {
        return try {
            val inputStream =
                contentResolver.openInputStream(contentUri)
                    ?: return Result.failure(Exception("Can't open image stream"))

            val bytes = inputStream.readBytes()
            withContext(Dispatchers.IO) {
                inputStream.close()
            }

            val mimeType = contentResolver.getType(contentUri) ?: "image/jpeg"

            val filename = getFileName(contentUri, contentResolver) ?: "image_${System.currentTimeMillis()}.jpg"

            val requestFile = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("image", filename, requestFile)

            val response = apiService.uploadAvatar(part)

            if (response.isSuccessful) {
                val responseMap = response.body()
                if (responseMap != null) {
                    val profileResponse = apiService.getUserProfile()
                    if (profileResponse.isSuccessful && profileResponse.body() != null) {
                        Result.success(profileResponse.body()!!)
                    } else {
                        Result.failure(Exception("Failed to get updated profile after avatar upload"))
                    }
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to upload avatar"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserStatistics(userId: Int): Result<UserStatistics> {
        return try {
            val response = apiService.getUserStatistics(userId)

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to get user statistics"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getFileName(
        uri: Uri,
        contentResolver: ContentResolver,
    ): String? {
        return when (uri.scheme) {
            ContentResolver.SCHEME_CONTENT -> {
                val cursor = contentResolver.query(uri, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (displayNameIndex != -1) {
                            it.getString(displayNameIndex)
                        } else {
                            null
                        }
                    } else {
                        null
                    }
                }
            }
            ContentResolver.SCHEME_FILE -> uri.lastPathSegment
            else -> null
        }
    }
}
