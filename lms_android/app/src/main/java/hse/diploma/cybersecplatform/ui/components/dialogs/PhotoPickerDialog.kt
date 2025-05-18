package hse.diploma.cybersecplatform.ui.components.dialogs

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.ui.theme.Montserrat
import hse.diploma.cybersecplatform.utils.PickerSource
import hse.diploma.cybersecplatform.utils.createImageUri

@Composable
fun PhotoPickerDialog(
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
                            contentColor = Color.White,
                        ),
                ) { Text(stringResource(R.string.camera), fontFamily = Montserrat) }
            },
            dismissButton = {
                Row {
                    Button(
                        onClick = { launchSource = PickerSource.GALLERY },
                        colors =
                            ButtonDefaults.filledTonalButtonColors(
                                containerColor = colorResource(R.color.button_enabled),
                                contentColor = Color.White,
                            ),
                    ) { Text(stringResource(R.string.gallery), fontFamily = Montserrat) }

                    Spacer(Modifier.width(8.dp))

                    Button(
                        onClick = onDismiss,
                        colors =
                            ButtonDefaults.filledTonalButtonColors(
                                containerColor = colorResource(R.color.button_enabled),
                                contentColor = Color.White,
                            ),
                    ) { Text(stringResource(R.string.cancel_button), fontFamily = Montserrat) }
                }
            },
            containerColor = colorResource(R.color.dialog_color),
            tonalElevation = 8.dp,
        )
    } else {
        LaunchedEffect(launchSource) {
            when (launchSource) {
                PickerSource.CAMERA -> {
                    currentImageUri = createImageUri(context)
                    currentImageUri?.let { cameraLauncher.launch(it) }
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
private fun PhotoPickerDialogPreview() {
    PhotoPickerDialog(onPhotoPicked = {}, onDismiss = {})
}
