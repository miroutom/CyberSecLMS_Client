package hse.diploma.cybersecplatform.domain.repository

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import hse.diploma.cybersecplatform.data.api.ApiService
import hse.diploma.cybersecplatform.data.model.UpdateProfileRequest
import hse.diploma.cybersecplatform.data.model.UserData
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

    override suspend fun updateProfile(userData: UserData): Result<UserData> {
        return try {
            val request = UpdateProfileRequest(userData)
            val response = apiService.updateProfile(request)

            if (response.isSuccessful) {
                val updatedUser = response.body()
                if (updatedUser != null) {
                    Result.success(updatedUser)
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

            val filename = getFileName(contentUri, contentResolver) ?: "avatar_${System.currentTimeMillis()}.jpg"

            val requestFile = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("avatar", filename, requestFile)

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

    private fun getFileName(
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
