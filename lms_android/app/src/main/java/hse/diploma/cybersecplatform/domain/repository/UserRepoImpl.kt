package hse.diploma.cybersecplatform.domain.repository

import android.content.ContentResolver
import android.net.Uri
import hse.diploma.cybersecplatform.data.api.ApiService
import hse.diploma.cybersecplatform.data.model.UserData
import hse.diploma.cybersecplatform.mock.mockAvatarUrl
import hse.diploma.cybersecplatform.mock.mockNewUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import javax.inject.Inject

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
            val response = Response.success(mockNewUser)
            if (response.isSuccessful) {
                val courses = response.body()
                if (courses != null) {
                    Result.success(courses)
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to load courses"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadAvatar(contentUri: Uri, contentResolver: ContentResolver): Result<UserData> {
        return try {
            val inputStream = contentResolver.openInputStream(contentUri)
                ?: return Result.failure(Exception("Can't open image stream"))
            val bytes = inputStream.readBytes()
            withContext(Dispatchers.IO) {
                inputStream.close()
            }
            val filename = "avatar_${System.currentTimeMillis()}.jpg"
            val requestFile = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("avatar", filename, requestFile)
            val response = Response.success(mockNewUser)
            if (response.isSuccessful) {
                val user = response.body()
                if (user != null) {
                    Result.success(user)
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
}
