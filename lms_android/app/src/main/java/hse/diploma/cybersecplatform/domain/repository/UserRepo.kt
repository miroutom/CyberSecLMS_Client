package hse.diploma.cybersecplatform.domain.repository

import android.content.ContentResolver
import android.net.Uri
import hse.diploma.cybersecplatform.data.model.UserData
import hse.diploma.cybersecplatform.data.model.response.MessageResponse

interface UserRepo {
    suspend fun getUserProfile(): Result<UserData>

    suspend fun updateProfile(userData: UserData): Result<MessageResponse>

    suspend fun uploadAvatar(
        contentUri: Uri,
        contentResolver: ContentResolver,
    ): Result<UserData>
}
