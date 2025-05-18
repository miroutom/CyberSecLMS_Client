package hse.diploma.cybersecplatform.ui.components.dialogs

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import coil.request.ImageRequest
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.mock.mockNewUser
import hse.diploma.cybersecplatform.ui.screens.profile.ProfileUiState
import hse.diploma.cybersecplatform.ui.state.ProfileState
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AvatarChooserDialog(
    profileState: ProfileState,
    onPhotoPicked: (Uri) -> Unit,
) {
    val context = LocalContext.current
    val tempImageUri = remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onPhotoPicked(it) }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && tempImageUri.value != null) {
            onPhotoPicked(tempImageUri.value!!)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            tempImageUri.value = createTempImageUri(context)
            tempImageUri.value?.let { cameraLauncher.launch(it) }
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.camera_permission_required),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun selectPhotoSource() {
        val options = arrayOf(
            context.getString(R.string.gallery),
            context.getString(R.string.camera)
        )

        androidx.appcompat.app.AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.choose_photo_source))
            .setItems(options) { _, which ->
                when (which) {
                    0 -> galleryLauncher.launch("image/*")
                    1 -> {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                android.Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            tempImageUri.value = createTempImageUri(context)
                            tempImageUri.value?.let { cameraLauncher.launch(it) }
                        } else {
                            permissionLauncher.launch(android.Manifest.permission.CAMERA)
                        }
                    }
                }
            }
            .show()
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(colorResource(R.color.button_enabled))
            .clickable { selectPhotoSource() }
    ) {
        val success = profileState as? ProfileState.Success
        val avatarUrl = success?.uiState?.userData?.avatarUrl
            ?: "https://placehold.co/256x256.png?text=Avatar"

        if (avatarUrl.isNotEmpty()) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(avatarUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Profile image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                painter = painterResource(R.drawable.ic_account),
                contentDescription = "Profile Image",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.Center)
            )
        }

        Icon(
            painter = painterResource(R.drawable.ic_account),
            contentDescription = "Edit photo",
            tint = Color.White,
            modifier = Modifier
                .size(16.dp)
                .align(Alignment.BottomEnd)
                .background(
                    color = colorResource(R.color.button_enabled),
                    shape = CircleShape
                )
                .padding(2.dp)
        )
    }
}

fun createTempImageUri(context: Context): Uri? {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_$timeStamp"
    val storageDir = context.cacheDir

    return try {
        val tempFile = File.createTempFile(imageFileName, ".jpg", storageDir)
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tempFile
        )
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

@Preview(showBackground = true)
@Composable
private fun AvatarChooserDialogPreview() {
    AvatarChooserDialog(
        profileState = ProfileState.Success(ProfileUiState(userData = mockNewUser)),
        onPhotoPicked = {}
    )
}
