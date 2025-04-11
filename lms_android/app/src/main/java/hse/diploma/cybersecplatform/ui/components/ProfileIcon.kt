package hse.diploma.cybersecplatform.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.ui.theme.CyberSecPlatformTheme

@Composable
fun ProfileIcon(userProfileImageUrl: String?) {
    if (userProfileImageUrl != null) {
        AsyncImage(
            model = userProfileImageUrl,
            contentDescription = "User avatar",
            contentScale = ContentScale.Crop,
            modifier =
                Modifier
                    .size(48.dp)
                    .clip(CircleShape),
        )
    } else {
        Box(
            modifier =
                Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color = colorResource(R.color.text_disabled)),
            contentAlignment = Alignment.Center,
        ) {
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
fun ProfileIconPreview() {
    CyberSecPlatformTheme {
        ProfileIcon(null)
    }
}
