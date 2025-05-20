package hse.diploma.cybersecplatform.ui.components.dialogs

import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.ui.theme.Montserrat
import hse.diploma.cybersecplatform.utils.PickerSource
import hse.diploma.cybersecplatform.utils.createImageUri

@Composable
fun AvatarChooserDialog(
    onPhotoPicked: (Uri) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    var currentImageUri by remember { mutableStateOf<Uri?>(null) }
    var launchSource by remember { mutableStateOf<PickerSource?>(null) }

    val galleryLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
        ) { uri: Uri? ->
            if (uri != null) onPhotoPicked(uri)
            onDismiss()
        }

    val cameraLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture(),
        ) { success: Boolean ->
            if (success && currentImageUri != null) onPhotoPicked(currentImageUri!!)
            onDismiss()
        }

    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted ->
            if (isGranted) {
                currentImageUri = createImageUri(context)
                currentImageUri?.let { cameraLauncher.launch(it) }
            } else {
                Toast.makeText(
                    context,
                    "Camera permission is required to take photos",
                    Toast.LENGTH_SHORT,
                ).show()
                onDismiss()
            }
        }

    if (launchSource == null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = stringResource(R.string.upload_photo),
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    fontFamily = Montserrat,
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.choose_source_to_upload),
                    fontFamily = Montserrat,
                )
            },
            confirmButton = {
                Button(
                    onClick = { launchSource = PickerSource.CAMERA },
                    colors =
                        ButtonDefaults.filledTonalButtonColors(
                            containerColor = colorResource(R.color.button_enabled),
                            contentColor = colorResource(R.color.background),
                        ),
                ) { Text(stringResource(R.string.camera), fontFamily = Montserrat) }
            },
            dismissButton = {
                Button(
                    onClick = { launchSource = PickerSource.GALLERY },
                    colors =
                        ButtonDefaults.filledTonalButtonColors(
                            containerColor = colorResource(R.color.button_enabled),
                            contentColor = colorResource(R.color.background),
                        ),
                ) { Text(stringResource(R.string.gallery), fontFamily = Montserrat) }
            },
            containerColor = colorResource(R.color.dialog_color),
            tonalElevation = 8.dp,
        )
    } else {
        LaunchedEffect(launchSource) {
            when (launchSource) {
                PickerSource.CAMERA -> {
                    val permissionCheckResult =
                        ContextCompat.checkSelfPermission(
                            context,
                            android.Manifest.permission.CAMERA,
                        )

                    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                        currentImageUri = createImageUri(context)
                        currentImageUri?.let { cameraLauncher.launch(it) }
                    } else {
                        cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                    }
                }

                PickerSource.GALLERY -> {
                    galleryLauncher.launch("image/*")
                }

                else -> {}
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AvatarChooserDialogPreview() {
    AvatarChooserDialog(onPhotoPicked = {}, onDismiss = {})
}
