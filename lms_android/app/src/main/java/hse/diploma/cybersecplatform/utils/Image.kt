package hse.diploma.cybersecplatform.utils

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class PickerSource { CAMERA, GALLERY }

fun createImageUri(context: Context): Uri? {
    return try {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        val file =
            File.createTempFile(
                // prefix
                "JPEG_${timeStamp}_",
                // suffix
                ".jpg",
                // directory
                storageDir,
            )
        FileProvider.getUriForFile(
            context,
            context.packageName + ".fileprovider",
            file,
        )
    } catch (e: Exception) {
        null
    }
}
