package hse.diploma.cybersecplatform.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.di.vm.LocalViewModelFactory
import hse.diploma.cybersecplatform.ui.components.dialogs.AvatarChooserDialog
import hse.diploma.cybersecplatform.ui.components.dialogs.EditProfileDialog
import hse.diploma.cybersecplatform.ui.screens.profile.ProfileViewModel
import hse.diploma.cybersecplatform.ui.state.ProfileState
import hse.diploma.cybersecplatform.ui.theme.CyberSecPlatformTheme
import hse.diploma.cybersecplatform.utils.logD
import hse.diploma.cybersecplatform.utils.logE

@Composable
fun EditProfile(
    profileViewModel: ProfileViewModel,
    userProfileImageUrl: String?,
) {
    val profileState by profileViewModel.profileState.collectAsState()

    val context = LocalContext.current

    var isEditProfileDialogOpened by remember { mutableStateOf(false) }
    var isAvatarChooserDialogOpened by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(end = 16.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(48.dp)
                    .clip(CircleShape),
        ) {
            ProfileIcon(
                userProfileImageUrl = userProfileImageUrl,
                onClick = { isAvatarChooserDialogOpened = !isAvatarChooserDialogOpened },
            )
        }

        IconButton(onClick = {
            if (profileState is ProfileState.Loading) {
                Toast.makeText(context, "Loading profile data...", Toast.LENGTH_SHORT).show()
            } else {
                isEditProfileDialogOpened = !isEditProfileDialogOpened
            }
        }, modifier = Modifier.size(24.dp)) {
            Icon(
                painter = painterResource(R.drawable.ic_expand_more),
                contentDescription = "Expand",
            )
        }
    }

    if (isEditProfileDialogOpened) {
        when (profileState) {
            is ProfileState.Success -> {
                EditProfileDialog(
                    uiState = (profileState as ProfileState.Success).uiState,
                    onDismiss = {
                        isEditProfileDialogOpened = !isEditProfileDialogOpened
                    },
                    onSave = { username, fullName, email ->
                        profileViewModel.updateProfile(username, fullName, email)
                    },
                )
            }
            is ProfileState.Error -> {
                AlertDialog(
                    onDismissRequest = { isEditProfileDialogOpened = false },
                    title = { Text("Error") },
                    text = { Text("Failed to load profile: ${(profileState as ProfileState.Error).errorType}") },
                    confirmButton = {
                        Button(onClick = { isEditProfileDialogOpened = false }) {
                            Text("OK")
                        }
                    },
                )
            }
            is ProfileState.Loading -> {
                AlertDialog(
                    onDismissRequest = { isEditProfileDialogOpened = false },
                    title = { Text("Loading") },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Loading profile data...")
                        }
                    },
                    confirmButton = {},
                )
            }
        }
    }

    if (isAvatarChooserDialogOpened) {
        AvatarChooserDialog(
            onPhotoPicked = {
                profileViewModel.uploadPhoto(it, context.contentResolver)
            },
            onDismiss = {
                isAvatarChooserDialogOpened = !isAvatarChooserDialogOpened
            },
        )
    }
}

@Composable
private fun ProfileIcon(
    userProfileImageUrl: String?,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (userProfileImageUrl != null) {
            AsyncImage(
                model = userProfileImageUrl,
                contentDescription = "User avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize(),
                placeholder = painterResource(R.drawable.ic_account),
                error = painterResource(R.drawable.ic_account),
                onError = { logE("EditProfile", "Avatar upload error", it.result.throwable) }
            )
        } else {
            Icon(
                painter = painterResource(R.drawable.ic_account),
                contentDescription = "Profile",
                tint = colorResource(R.color.supporting_text),
                modifier = Modifier.size(36.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditProfilePreview() {
    CyberSecPlatformTheme {
        EditProfile(
            profileViewModel = viewModel(factory = LocalViewModelFactory.current),
            null,
        )
    }
}
